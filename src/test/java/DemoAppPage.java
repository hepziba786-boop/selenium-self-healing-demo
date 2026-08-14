import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DemoAppPage {

    private static final By HEADING = By.tagName("h1");

    private static final FallbackLocator SUBMIT_BUTTON = FallbackLocator.of(
            By.id("Submit"),
            By.cssSelector("[data-testid='submit-button']"),
            By.cssSelector("button[type='button']"),
            By.xpath("//button[normalize-space()='ENTER']")
    );

    private final WebDriver driver;

    public DemoAppPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getHeadingText() {
        return driver.findElement(HEADING).getText();
    }

    public boolean isSubmitButtonDisplayed() {
        return getSubmitButton().isDisplayed();
    }

    public boolean isSubmitButtonEnabled() {
        return getSubmitButton().isEnabled();
    }

    public String getSubmitButtonText() {
        return getSubmitButton().getText();
    }

    public void clickSubmit() {
        getSubmitButton().click();
    }

    public boolean isSubmitButtonFocused() {
        WebElement submitButton = getSubmitButton();
        return submitButton.equals(driver.switchTo().activeElement());
    }

    private WebElement getSubmitButton() {
        return SUBMIT_BUTTON.find(driver);
    }
}
