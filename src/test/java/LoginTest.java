import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SelfHealingExtension.class)
public class LoginTest extends BaseUiTest {

    // ── 1. Login page loads with all expected elements ───────────────────────
    @Test
    void loginPageDisplaysAllExpectedElements() {
        DemoAppPage page = openDemoApp();

        assertAll("login page initial state",
                () -> assertEquals("Log in to your account", page.getPageHeadingText(), "page heading"),
                () -> assertTrue(page.isUsernameInputDisplayed(),    "username input visible"),
                () -> assertTrue(page.isPasswordInputDisplayed(),    "password input visible"),
                () -> assertTrue(page.isLoginButtonDisplayed(),      "login button visible"),
                () -> assertTrue(page.isLoginButtonEnabled(),        "login button enabled"),
                () -> assertEquals("Sign In", page.getLoginButtonText(), "login button label"),
                () -> assertTrue(page.isForgotPasswordLinkDisplayed(), "forgot-password link visible"),
                () -> assertTrue(page.isSignupLinkDisplayed(),       "sign-up link visible"),
                () -> assertFalse(page.isErrorMessageVisible(),      "error banner hidden initially")
        );
    }

    // ── 2. Valid credentials → dashboard is shown ────────────────────────────
    @Test
    void successfulLoginShowsDashboard() {
        DemoAppPage page = openDemoApp();

        page.loginWith("admin", "secret123");

        assertAll("post-login dashboard",
                () -> assertTrue(page.isDashboardVisible(),           "dashboard panel visible"),
                () -> assertFalse(page.isLoginPanelVisible(),         "login panel hidden"),
                () -> assertTrue(page.getDashboardHeadingText().contains("Welcome"), "dashboard greeting contains 'Welcome'"),
                () -> assertTrue(page.isProjectsTableDisplayed(),     "projects table visible"),
                () -> assertFalse(page.isErrorMessageVisible(),       "error banner absent after success")
        );
    }

    // ── 3. Wrong credentials → error banner is shown ─────────────────────────
    @Test
    void invalidCredentialsShowsErrorBanner() {
        DemoAppPage page = openDemoApp();

        page.loginWith("wronguser", "wrongpass");

        assertAll("invalid-credentials error",
                () -> assertTrue(page.isErrorMessageVisible(),        "error banner visible"),
                () -> assertTrue(page.getErrorMessageText().contains("Invalid"), "error text mentions 'Invalid'"),
                () -> assertFalse(page.isDashboardVisible(),          "dashboard still hidden")
        );
    }

    // ── 4. Empty username → error banner shown (no dashboard) ────────────────
    @Test
    void emptyUsernameShowsErrorBanner() {
        DemoAppPage page = openDemoApp();

        page.loginWith("", "secret123");

        assertAll("empty username",
                () -> assertTrue(page.isErrorMessageVisible(),  "error banner visible for empty username"),
                () -> assertFalse(page.isDashboardVisible(),    "dashboard not shown")
        );
    }

    // ── 5. Empty password → error banner shown (no dashboard) ────────────────
    @Test
    void emptyPasswordShowsErrorBanner() {
        DemoAppPage page = openDemoApp();

        page.loginWith("admin", "");

        assertAll("empty password",
                () -> assertTrue(page.isErrorMessageVisible(),  "error banner visible for empty password"),
                () -> assertFalse(page.isDashboardVisible(),    "dashboard not shown")
        );
    }

    // ── 6. Logout returns to login page ──────────────────────────────────────
    @Test
    void logoutReturnsToLoginPage() {
        DemoAppPage page = openDemoApp();

        page.loginWith("admin", "secret123");
        assertTrue(page.isDashboardVisible(), "pre-condition: dashboard visible after login");

        page.clickLogout();

        assertAll("post-logout",
                () -> assertTrue(page.isLoginPanelVisible(),   "login panel visible after logout"),
                () -> assertFalse(page.isDashboardVisible(),   "dashboard hidden after logout"),
                () -> assertFalse(page.isErrorMessageVisible(),"error banner absent after logout")
        );
    }

    // ── 7. Login button is focusable after click ──────────────────────────────
    @Test
    void loginButtonReceivesFocusAfterClick() {
        DemoAppPage page = openDemoApp();

        page.clickLogin();

        assertTrue(page.isLoginButtonFocused(), "login button should receive focus after click");
    }
}
