package tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.*;
import utils.DataProviderUtil;

import java.io.IOException;
import java.util.HashMap;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FrameworkBuildDataDrivenTest extends TestBase {

    @DataProvider(name = "eventBookingData")
    public Object[][] eventBookingData() throws IOException {
        return DataProviderUtil.getJsonDataToMap("/src/test/resources/eventBookingData.json");
    }

    @Test(groups = {"framework"}, description = "Create an event, book that event and verify that it's booked", dataProvider = "eventBookingData")
    public void demoTest(HashMap<String, String> data) {
        String eventTitle = "QA Summit RSA";

        LoginPage loginPage = new LoginPage(page, base_url);
        DashboardPage dashboardPage = loginPage.loginToApplication();
        dashboardPage.waitForEventsToLoad();

        // Create a new event
        AdminEventsPage adminEventsPage = new AdminEventsPage(page);
        adminEventsPage.goTo();
        adminEventsPage.createEvent(data.get("titlePrefix"), data.get("description"), data.get("city"),
                data.get("venue"), data.get("dateTime"), data.get("price"), data.get("totalSeats"));

        // Check new event in events page
        EventsPage eventsPage = new EventsPage(page);
        eventsPage.goTo();
        Locator targetCard = eventsPage.findEventCard(eventTitle);
        int seatsNumberBeforeBooking = eventsPage.getSeatsCount(targetCard);
        BookingFormPage bookingFormPage = eventsPage.proceedToBookingEvent(targetCard);

        // Book tickets for the new event
        bookingFormPage.fillAndConfirm(data.get("fullName"),data.get("email"), data.get("phone"));
        String bookingRef = page.locator(".booking-ref").innerText();

        // Check the booked tickets
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("View My Bookings")).click();
        Locator bookingCards = page.locator("#booking-card");
        Locator targetBookingCard = bookingCards.filter(new Locator.FilterOptions().setHasText(bookingRef));
        assertThat(targetBookingCard).isVisible();

        // Check the event seat count
        page.locator("#nav-events").click();
        page.waitForTimeout(1000);
        Locator targetCardAfterBooking = page.getByTestId("event-card").filter(new Locator.FilterOptions().setHasText("QA Summit RSA"));
        String seatsTextAfterBooking = targetCardAfterBooking.getByText("seats").innerText();
        int seatsNumberAfterBooking = Integer.parseInt(seatsTextAfterBooking.split(" ")[0]);
        Assert.assertTrue(seatsNumberBeforeBooking > seatsNumberAfterBooking);
    }
}
