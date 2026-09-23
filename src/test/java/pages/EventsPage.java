package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class EventsPage {

    Page page;

    public EventsPage(Page page) {
        this.page = page;
    }

    public void goTo() {
        page.locator("#nav-events").click();
    }

    public Locator waitForEventsToLoad() {
        Locator eventsCards = page.getByTestId("event-card");
        assertThat(eventsCards.first()).isVisible();
        return eventsCards;
    }

    public Locator findEventCard(String titleCard) {
        Locator targetCard = waitForEventsToLoad().filter(new Locator.FilterOptions().setHasText(titleCard));
        assertThat(targetCard).isVisible();
        return targetCard;
    }

    public Integer getSeatsCount(Locator targetCard) {
        return Integer.parseInt(targetCard.getByText("seats").innerText().split(" ")[0]);
    }

    public BookingFormPage proceedToBookingEvent(Locator targetCard) {
        targetCard.getByTestId("book-now-btn").click();
        return new BookingFormPage(page);
    }
}
