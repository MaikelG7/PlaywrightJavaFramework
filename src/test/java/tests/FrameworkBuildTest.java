package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FrameworkBuildTest extends TestBase {

    @Test(groups = {"framework"}, description = "Create an event, book that event and verify that it's booked")
    public void demoTest() {
        String eventTitle = "QA Summit RSA";

        // Login
        LoginPage loginPage = new LoginPage(page, base_url);
        DashboardPage dashboardPage = loginPage.loginToApplication();
        dashboardPage.waitForEventsToLoad();

        // Create a new event
        AdminEventsPage adminEventsPage = new AdminEventsPage(page);
        adminEventsPage.goTo();
        adminEventsPage.createEvent(eventTitle, "QA Meetup", "Test City", "Test Venue", "2026-12-18T14:20", "100", "50");

        // Check new event in events page
        EventsPage eventsPage = new EventsPage(page);
        eventsPage.goTo();
        Locator targetCard = eventsPage.findEventCard(eventTitle);
        int seatsNumberBeforeBooking = eventsPage.getSeatsCount(targetCard);
        BookingFormPage bookingFormPage = eventsPage.proceedToBookingEvent(targetCard);

        // Book tickets for the new event
        bookingFormPage.fillAndConfirm("Test Student", "test.student@example.com", "9876543210");
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
