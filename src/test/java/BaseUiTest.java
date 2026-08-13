import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public abstract class BaseUiTest {

    protected WebDriver driver;

    @BeforeEach
    void setUpDriver() {
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1280,720");
            driver = new ChromeDriver(options);
        } catch (RuntimeException chromeFailure) {
            // Fallback for constrained environments where Chrome runtime libraries are unavailable.
            driver = new HtmlUnitDriver(true);
        }

        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    protected DemoAppPage openDemoApp() {
        Path appPath = Paths.get("app", "index.html").toAbsolutePath();
        driver.get(appPath.toUri().toString());
        return new DemoAppPage(driver);
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
