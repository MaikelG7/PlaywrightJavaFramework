package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.BeforeMethod;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestBase {
    Page page;
    String base_url;

    @BeforeMethod(alwaysRun = true)
    public void setup() throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
        prop.load(fis);
        String browserName = System.getProperty("browser") != null ? System.getProperty("browser") : prop.getProperty("browser");
        String envName = System.getProperty("env") != null ? System.getProperty("env") : prop.getProperty("env");

        Playwright playwright = Playwright.create();
        Browser browser;
        if ("firefox".equals(browserName)) {
            browser = playwright.firefox().launch();
        }
        else if ("safari".equals(browserName)) {
            browser = playwright.webkit().launch();
        }
        else {
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        }

        page = browser.newPage();
        page.setDefaultTimeout(5000);
        base_url = prop.getProperty(envName + ".base_url");
    }
}
