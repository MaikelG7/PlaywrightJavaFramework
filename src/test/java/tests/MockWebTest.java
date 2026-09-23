package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MockWebTest {
    Page page;

    @BeforeMethod
    public void setup() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate("https://eventhub.rahulshettyacademy.com/login");
    }

    @Test(description = "Check if sandbox banner is shown when 6 events are returned")
    public void demoTest() {
        // Login
        page.getByPlaceholder("you@email.com").fill("rahulshetty1@yahoo.com");
        page.getByLabel("Password").fill("Magiclife1!");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
        assertThat(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

        // Force the event hub to have 6 events and check that they and the notification banner are shown
        page.route("**/api/events**", route -> route.fulfill(new Route.FulfillOptions().setPath(Paths.get("src/test/resources/events_6.json"))));
        page.navigate("https://eventhub.rahulshettyacademy.com/events");
        Locator eventCards = page.getByTestId("event-card");
        assertThat(eventCards.first()).isVisible();
        Assert.assertEquals(eventCards.count(), 6);
        assertThat(page.locator(".mx-1").first()).isVisible();

        // Force the event hub to have 4 events, check that they are shown and check that the notification banner is not shown
        page.route("**/api/events**", route -> route.fulfill(new Route.FulfillOptions().setPath(Paths.get("src/test/resources/events_4.json"))));
        page.navigate("https://eventhub.rahulshettyacademy.com/events");
        eventCards = page.getByTestId("event-card");
        assertThat(eventCards.first()).isVisible();
        Assert.assertEquals(eventCards.count(), 4);
        assertThat(page.locator(".mx-1").first()).isHidden();
    }

    @Test
    public void routeResumeTest() {
        // Login
        page.getByPlaceholder("you@email.com").fill("rahulshetty1@yahoo.com");
        page.getByLabel("Password").fill("Magiclife1!");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
        assertThat(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

        // Check the booking of another user
        page.getByTestId("nav-bookings").click();
        page.route("**/api/bookings**", route -> route.resume(
                new Route.ResumeOptions().setUrl("https://api.eventhub.rahulshettyacademy.com/api/bookings/72116")));
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("View Details")).first().click();
        assertThat(page.getByText("Access Denied")).isVisible();
    }
}