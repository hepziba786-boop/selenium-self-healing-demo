import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoginTest {

    @Test
    void clickButton() {

        ChromeDriver driver = new ChromeDriver();

        driver.get("file:///app/index.html");

        driver.findElement(
            By.id("submitBtn")
        ).click();

        driver.quit();
    }
}
