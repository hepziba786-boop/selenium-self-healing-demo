import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public abstract class BaseUiTest {

    protected WebDriver driver;

    @BeforeEach
    void setUpDriver() {
        String chromeDriverPath = "/home/codespace/.cache/selenium/chromedriver/linux64/152.0.7977.42/chromedriver";
        String chromeBinaryPath = "/home/codespace/.cache/selenium/chrome/linux64/152.0.7977.42/chrome";

        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        ChromeOptions options = new ChromeOptions();
        options.setBinary(chromeBinaryPath);
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1280,720");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
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
