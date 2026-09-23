package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPage {

    Page page;
    String base_url;
    private static final String email_placeholder = "you@email.com";
    private static final String password_label = "Password";

    public LoginPage(Page page, String base_url) {
        this.page = page;
        this.base_url = base_url;
    }

    public DashboardPage loginToApplication() {
        page.navigate(base_url);
        System.out.println(page.title());
        assertThat(page).hasTitle("EventHub — Discover & Book Events");
        page.getByPlaceholder(email_placeholder).fill("rahulshetty1@yahoo.com");
        page.getByLabel(password_label).fill("Magiclife1!");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
        DashboardPage dashboardPage = new DashboardPage(page);
        return dashboardPage;
    }
}
