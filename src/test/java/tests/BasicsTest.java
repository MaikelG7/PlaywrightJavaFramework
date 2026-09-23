package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BasicsTest {
    Page page;

    @BeforeMethod
    public void setup() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
        //Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));

        page = browser.newPage();
        page.navigate("https://eventhub.rahulshettyacademy.com/login");

        // Timeouts
        page.setDefaultTimeout(8000); // actions default 10 seconds
        PlaywrightAssertions.setDefaultAssertionTimeout(7000); // assertions default 5 seconds
    }

    @Test(description = "Create an event, book that event and verify that it's booked")
    public void demoTest() {
        System.out.println(page.title());
        assertThat(page).hasTitle("EventHub — Discover & Book Events");

        // Login
        page.getByPlaceholder("you@email.com").fill("rahulshetty1@yahoo.com");
        page.getByLabel("Password").fill("Magiclife1!");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
        assertThat(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

        // Create a new event
        page.navigate("https://eventhub.rahulshettyacademy.com/admin/events");
        page.locator("#event-title-input").fill("QA Summit RSA", new Locator.FillOptions().setTimeout(11000));
        page.locator("#admin-event-form textarea").fill("QA Meetup");
        page.getByLabel("Category").selectOption("Concert");
        page.getByLabel("City").fill("Test City");
        page.getByLabel("Venue").fill("Test Venue");
        page.getByLabel("Event Date & Time").fill(("2026-12-18T14:20"));
        page.getByLabel("Price").fill("100");
        page.getByLabel("Total Seats").fill("50");
        page.locator("#add-event-btn").click(new Locator.ClickOptions().setTimeout(12000));
        assertThat(page.getByText("Event created!")).isVisible();

        // Check new event in events page
        page.locator("#nav-events").click();
        assertThat(page.getByText("Upcoming Events")).isVisible();
        Locator targetCard = page.getByTestId("event-card").filter(new Locator.FilterOptions().setHasText("QA Summit RSA"));
        assertThat(targetCard).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000));

        // Book tickets for the new event
        int seatsNumberBeforeBooking = Integer.parseInt(targetCard.getByText("seats").innerText().split(" ")[0]);
        targetCard.getByTestId("book-now-btn").click();
        assertThat(page.getByText("Book tickets")).isVisible();
        page.getByText("+").click();
        page.getByText("+").click();
        page.getByLabel("Full name").fill("Test Student");
        page.locator("#customer-email").fill("test.student@example.com");
        page.getByPlaceholder("+91 98765 43210").fill("9876543210");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm Booking")).click();
        assertThat(page.getByText("Your tickets are reserved.")).isVisible();
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
