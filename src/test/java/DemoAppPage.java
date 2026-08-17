import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DemoAppPage {

    // ── Login panel ──────────────────────────────────────────────────────────

    private static final FallbackLocator PAGE_HEADING = FallbackLocator.of(
            By.id("page-heading"),
            By.cssSelector("[data-testid='page-heading']"),
            By.cssSelector(".card h1"),
            By.xpath("//h1[contains(normalize-space(),'Sign in')]")
    );

    private static final FallbackLocator USERNAME_INPUT = FallbackLocator.of(
            By.id("username"),
            By.cssSelector("[data-testid='username-input']"),
            By.cssSelector("input[name='username']"),
            By.xpath("//input[@placeholder='you@company.com']")
    );

    private static final FallbackLocator PASSWORD_INPUT = FallbackLocator.of(
            By.id("password"),
            By.cssSelector("[data-testid='password-input']"),
            By.cssSelector("input[name='password']"),
            By.xpath("//input[@placeholder='••••••••']")
    );

    private static final FallbackLocator LOGIN_BUTTON = FallbackLocator.of(
            By.id("login-btn"),
            By.cssSelector("[data-testid='login-button']"),
            By.cssSelector("button.btn-login"),
            By.xpath("//button[normalize-space()='Sign In']")
    );

    private static final FallbackLocator ERROR_MESSAGE = FallbackLocator.of(
            By.id("error-message"),
            By.cssSelector("[data-testid='error-message']"),
            By.cssSelector(".error-banner"),
            By.xpath("//div[contains(@class,'error-banner')]")
    );

    private static final FallbackLocator FORGOT_PASSWORD_LINK = FallbackLocator.of(
            By.id("forgot-password-link"),
            By.cssSelector("[data-testid='forgot-password-link']"),
            By.cssSelector("a.forgot-link"),
            By.xpath("//a[normalize-space()='Forgot password?']")
    );

    private static final FallbackLocator SIGNUP_LINK = FallbackLocator.of(
            By.id("signup-link"),
            By.cssSelector("[data-testid='signup-link']"),
            By.cssSelector(".signup-prompt a"),
            By.xpath("//a[normalize-space()='Create one free']")
    );

    // ── Dashboard panel ───────────────────────────────────────────────────────

    private static final FallbackLocator DASHBOARD_HEADING = FallbackLocator.of(
            By.id("dashboard-heading"),
            By.cssSelector("[data-testid='dashboard-heading']"),
            By.cssSelector("#dashboard h2"),
            By.xpath("//h2[contains(normalize-space(),'Welcome')]")
    );

    private static final FallbackLocator LOGOUT_BUTTON = FallbackLocator.of(
            By.id("logout-btn"),
            By.cssSelector("[data-testid='logout-button']"),
            By.cssSelector("button.btn-logout"),
            By.xpath("//button[normalize-space()='Log out']")
    );

    private static final FallbackLocator PROJECTS_TABLE = FallbackLocator.of(
            By.cssSelector("[data-testid='projects-table']"),
            By.cssSelector("table.projects-table"),
            By.xpath("//table[contains(@class,'projects-table')]"),
            By.tagName("table")
    );

    // ─────────────────────────────────────────────────────────────────────────

    private final WebDriver driver;

    public DemoAppPage(WebDriver driver) {
        this.driver = driver;
    }

    // ── Login helpers ─────────────────────────────────────────────────────────

    public String getPageHeadingText() {
        return PAGE_HEADING.find(driver).getText();
    }

    public boolean isLoginButtonDisplayed() {
        return LOGIN_BUTTON.find(driver).isDisplayed();
    }

    public boolean isLoginButtonEnabled() {
        return LOGIN_BUTTON.find(driver).isEnabled();
    }

    public String getLoginButtonText() {
        return LOGIN_BUTTON.find(driver).getText();
    }

    public boolean isUsernameInputDisplayed() {
        return USERNAME_INPUT.find(driver).isDisplayed();
    }

    public boolean isPasswordInputDisplayed() {
        return PASSWORD_INPUT.find(driver).isDisplayed();
    }

    public boolean isForgotPasswordLinkDisplayed() {
        return FORGOT_PASSWORD_LINK.find(driver).isDisplayed();
    }

    public boolean isSignupLinkDisplayed() {
        return SIGNUP_LINK.find(driver).isDisplayed();
    }

    public void enterUsername(String value) {
        WebElement el = USERNAME_INPUT.find(driver);
        el.clear();
        el.sendKeys(value);
    }

    public void enterPassword(String value) {
        WebElement el = PASSWORD_INPUT.find(driver);
        el.clear();
        el.sendKeys(value);
    }

    public void clickLogin() {
        LOGIN_BUTTON.find(driver).click();
    }

    public void loginWith(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public boolean isErrorMessageVisible() {
        WebElement el = ERROR_MESSAGE.find(driver);
        return el.isDisplayed() && el.getAttribute("class").contains("visible");
    }

    public String getErrorMessageText() {
        return ERROR_MESSAGE.find(driver).getText();
    }

    public boolean isLoginButtonFocused() {
        WebElement loginButton = LOGIN_BUTTON.find(driver);
        return loginButton.equals(driver.switchTo().activeElement());
    }

    // ── Dashboard helpers ─────────────────────────────────────────────────────

    public boolean isDashboardVisible() {
        WebElement el = driver.findElement(By.id("dashboard"));
        return el.getAttribute("class").contains("visible");
    }

    public String getDashboardHeadingText() {
        return DASHBOARD_HEADING.find(driver).getText();
    }

    public boolean isProjectsTableDisplayed() {
        return PROJECTS_TABLE.find(driver).isDisplayed();
    }

    public void clickLogout() {
        LOGOUT_BUTTON.find(driver).click();
    }

    public boolean isLoginPanelVisible() {
        WebElement el = driver.findElement(By.id("login-panel"));
        String display = el.getCssValue("display");
        return !"none".equalsIgnoreCase(display);
    }
}
