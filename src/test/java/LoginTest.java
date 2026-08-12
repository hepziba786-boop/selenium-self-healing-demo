import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
public class LoginTest {

    @Test
    void clickButton() {

        ChromeOptions options = new ChromeOptions();

options.addArguments("--headless=new");
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");

ChromeDriver driver = new ChromeDriver(options);

        driver.get("file:///app/index.html");

        driver.findElement(
            By.id("submitBtn")
        ).click();

        driver.quit();
    }
}
