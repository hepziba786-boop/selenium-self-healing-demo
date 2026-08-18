import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end flow test written in the style of a manual test case.
 *
 * Each step follows the pattern:
 *   Step N | Action Performed | Expected Output | Assertion (PASS/FAIL will be
 *   reported by JUnit)
 *
 * Scenario under test
 * -------------------
 * 1. Attempt login with incorrect credentials  → error banner appears
 * 2. Login with correct credentials            → dashboard is shown (success)
 * 3. Click "My Profile" link on the dashboard  → browser navigates to a WRONG /
 *    stale URL (regression bug: link points to the old profile path)
 *
 * The final step is expected to FAIL (the test asserts the CORRECT url and
 * receives the stale one), demonstrating how Selenium catches a broken
 * navigation link introduced by an HTML change.
 */
public class ProfileNavigationTest extends BaseUiTest {

    /**
     * Full login-to-profile flow.
     *
     * Step | Action Performed                              | Expected Output
     * -----+----------------------------------------------+-------------------------------
     *  1   | Open the application login page              | Login form is displayed
     *  2   | Enter incorrect username and password        | Error banner becomes visible
     *  3   | Verify error banner text                     | Text contains "Invalid"
     *  4   | Clear fields; enter valid credentials        | No error; dashboard appears
     *  5   | Verify dashboard heading                     | Heading contains "Welcome"
     *  6   | Click "My Profile" link                      | Browser navigates away from page
     *  7   | Compare navigated URL with expected profile  | URL ends with "/user/profile"
     *                                                       (FAILS: actual is "/my-profile-page"
     *                                                       → broken link bug detected)
     */
    @Test
    void loginErrorThenSuccessAndProfileNavigatesToWrongPage() {

        DemoAppPage page = openDemoApp();

        // ── Step 1: Open login page ───────────────────────────────────────────
        // Action   : Navigate to app/index.html
        // Expected : Username input, password input, and Sign In button are visible
        assertTrue(page.isUsernameInputDisplayed(),  "Step 1 – username field is visible");
        assertTrue(page.isPasswordInputDisplayed(),  "Step 1 – password field is visible");
        assertTrue(page.isLoginButtonDisplayed(),    "Step 1 – Sign In button is visible");

        // ── Step 2: Enter incorrect credentials ──────────────────────────────
        // Action   : Type "wronguser" / "wrongpass" and click Sign In
        // Expected : Error banner becomes visible; dashboard remains hidden
        page.loginWith("wronguser", "wrongpass");

        assertTrue(page.isErrorMessageVisible(),  "Step 2 – error banner is shown for bad credentials");
        assertFalse(page.isDashboardVisible(),    "Step 2 – dashboard is NOT shown");

        // ── Step 3: Verify the error banner message ───────────────────────────
        // Action   : Read the text of the error banner
        // Expected : Message contains the word "Invalid"
        assertTrue(
                page.getErrorMessageText().contains("Invalid"),
                "Step 3 – error text mentions 'Invalid'"
        );

        // ── Step 4: Enter correct credentials ────────────────────────────────
        // Action   : Type "admin" / "secret123" and click Sign In
        // Expected : Login panel hides; dashboard becomes visible; no error banner
        page.loginWith("admin", "secret123");

        assertTrue(page.isDashboardVisible(),         "Step 4 – dashboard is visible after successful login");
        assertFalse(page.isLoginPanelVisible(),        "Step 4 – login panel is hidden");
        assertFalse(page.isErrorMessageVisible(),      "Step 4 – error banner is NOT shown on success");

        // ── Step 5: Verify dashboard welcome message ──────────────────────────
        // Action   : Read the dashboard heading text
        // Expected : Heading contains "Welcome"
        assertTrue(
                page.getDashboardHeadingText().contains("Welcome"),
                "Step 5 – dashboard heading contains 'Welcome'"
        );

        // ── Step 6: Verify "My Profile" link is present on dashboard ─────────
        // Action   : Locate the My Profile link
        // Expected : Link is displayed
        assertTrue(page.isMyProfileLinkDisplayed(), "Step 6 – My Profile link is visible on dashboard");

        // ── Step 7: Click "My Profile" and verify destination URL ─────────────
        // Action   : Click the My Profile link
        // Expected : Browser navigates to "/user/profile"  ← CORRECT (new) path
        // Actual   : Browser navigates to "/my-profile-page"  ← stale/broken path
        // Result   : FAIL – regression bug detected (link not updated in HTML)
        String hrefBefore = page.getMyProfileLinkHref();

        // The link href must point to the current profile path "/user/profile".
        // The HTML still has the OLD value "/my-profile-page", so this assertion
        // will FAIL, exposing the broken navigation link.
        assertTrue(
                hrefBefore.endsWith("/user/profile"),
                "Step 7 – EXPECTED: My Profile link href ends with '/user/profile' "
                        + "(the correct, updated URL)\n"
                        + "         ACTUAL  : href = '" + hrefBefore + "'\n"
                        + "         RESULT  : FAIL – navigation link is stale/broken"
        );
    }
}
