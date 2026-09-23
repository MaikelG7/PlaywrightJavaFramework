package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;

public class MoreUIValidationsTest {
    Page page;
    BrowserContext context;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));
        page = context.newPage();
        page.navigate("https://rahulshettyacademy.com/loginpagePractise/");
    }

    @Test
    public void childWindowHandle() {
        Locator blinkingTexts = page.locator(".blinkingText");
        Page newPage = context.waitForPage(() -> blinkingTexts.first().click());
        newPage.waitForLoadState();
        String childText = newPage.locator(".red").textContent();
        String email = childText.split("at ")[1].split(" ")[0];
        page.getByLabel("Username:").fill(email);
        page.waitForTimeout(3000);
        System.out.println(page.getByLabel("Username:").inputValue());
    }

    @Test(groups = {"smoke"})
    public void uiControls() {
        Locator userRadioButton = page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("User"));
        userRadioButton.click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Okay")).click();
        Assert.assertTrue(userRadioButton.isChecked());

        Locator checkboxTerms = page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("I agree to the terms and conditions"));
        checkboxTerms.check();
        Assert.assertTrue(checkboxTerms.isChecked());

        page.getByRole(AriaRole.COMBOBOX).selectOption("Teacher");
        page.waitForTimeout(3000);
    }

    @AfterMethod
    public void teardown() {
        context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get("trace.zip")));
    }
}
