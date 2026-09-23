package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class UIValidationsContinuedTest {
    Page page;
    BrowserContext context;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));
        page = context.newPage();
        page.navigate("https://rahulshettyacademy.com/AutomationPractise/");
    }

    @Test(groups = {"smoke"})
    public void popupValidations() {
        assertThat(page.getByPlaceholder("Hide/Show Example")).isVisible();
        page.locator("#hide-textbox").click();
        assertThat(page.getByPlaceholder("Hide/Show Example")).isHidden();

        page.onDialog(dialog -> dialog.accept());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Alert")).click();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Mouse Hover")).hover();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Top")).click();

        /*FrameLocator framesPage = page.frameLocator("#course-iframe");
        framesPage.getByRole(AriaRole.LINK, new FrameLocator.GetByRoleOptions().setName("Learning Paths")).click();
        Assert.assertTrue(framesPage.locator(".inner-box h1").textContent().equalsIgnoreCase("Learning paths"));*/
    }

    @Test
    public void screenshotTest() {
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("pageScreenshot.png")));
        Locator displayedTextBox = page.getByPlaceholder("Hide/Show Example");
        displayedTextBox.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get("textBoxScreenshot.png")));
        page.locator("#hide-textbox").click();
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("pagePostScreenshot.png")));
    }

    @AfterMethod
    public void teardown() {
        context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get("trace.zip")));
    }
}
