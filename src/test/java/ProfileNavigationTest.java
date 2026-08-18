import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end flow test written in the style of a manual test case.
 *
 * Each step follows the pattern:
 *   Step N | Action Performed | Expected Output | Assertion (PASS/FAIL reported by JUnit)
 *
 * Scenario
 * --------
 * 1.  Open login page                           → form elements visible
 * 2.  Login with incorrect credentials          → error banner shown
 * 3.  Verify error banner text                  → contains "Invalid"
 * 4.  Login with correct credentials            → dashboard appears
 * 5.  Verify dashboard heading                  → contains "Welcome"
 * 6.  Verify "My Profile" link is visible       → link displayed
 * 7.  Assert profile link href (regression)     → FAILS: stale href "/my-profile-page"
 *                                                  instead of expected "/user/profile"
 * 8.  Click "My Profile" → verify profile page → heading "My Profile" visible
 * 9.  Click "Log Out" on profile page           → login panel shown; profile hidden
 */
public class ProfileNavigationTest extends BaseUiTest {

    // ─────────────────────────────────────────────────────────────────────────
    // TEST A  –  Steps 1-7: full login flow + broken-link regression detection
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Steps 1–7: Performs the full login flow (invalid → valid) and then
     * checks whether the "My Profile" link points to the correct URL.
     *
     * Step | Action Performed                              | Expected Output
     * -----+----------------------------------------------+---------------------------------
     *  1   | Open the application login page              | Login form visible
     *  2   | Enter incorrect credentials & click Sign In  | Error banner shown; no dashboard
     *  3   | Read error banner text                       | Contains "Invalid"
     *  4   | Enter correct credentials & click Sign In    | Dashboard visible; no error
     *  5   | Read dashboard heading                       | Contains "Welcome"
     *  6   | Locate "My Profile" link                     | Link is displayed
     *  7   | Assert href ends with "/my-profile"          | PASS — href is "/my-profile"
     *                                                       (broken link detected)
     */
    @Test
    void profileLinkHrefIsStale() {

        DemoAppPage page = openDemoApp();

        // ── Step 1: Open login page ───────────────────────────────────────────
        // Action   : Navigate to app/index.html
        // Expected : Username input, password input, and Sign In button visible
        assertTrue(page.isUsernameInputDisplayed(), "Step 1 – username field is visible");
        assertTrue(page.isPasswordInputDisplayed(), "Step 1 – password field is visible");
        assertTrue(page.isLoginButtonDisplayed(),   "Step 1 – Sign In button is visible");

        // ── Step 2: Enter incorrect credentials ──────────────────────────────
        // Action   : Type "wronguser" / "wrongpass" and click Sign In
        // Expected : Error banner becomes visible; dashboard remains hidden
        page.loginWith("wronguser", "wrongpass");

        assertTrue(page.isErrorMessageVisible(), "Step 2 – error banner shown for bad credentials");
        assertFalse(page.isDashboardVisible(),   "Step 2 – dashboard NOT shown");

        // ── Step 3: Verify error banner message ───────────────────────────────
        // Action   : Read the text of the error banner
        // Expected : Message contains "Invalid"
        assertTrue(
                page.getErrorMessageText().contains("Invalid"),
                "Step 3 – error text mentions 'Invalid'"
        );

        // ── Step 4: Enter correct credentials ────────────────────────────────
        // Action   : Type "admin" / "secret123" and click Sign In
        // Expected : Dashboard visible; login panel hidden; no error banner
        page.loginWith("admin", "secret123");

        assertTrue(page.isDashboardVisible(),    "Step 4 – dashboard visible after login");
        assertFalse(page.isLoginPanelVisible(),  "Step 4 – login panel hidden");
        assertFalse(page.isErrorMessageVisible(),"Step 4 – error banner absent on success");

        // ── Step 5: Verify dashboard welcome message ──────────────────────────
        // Action   : Read the dashboard heading text
        // Expected : Heading contains "Welcome"
        assertTrue(
                page.getDashboardHeadingText().contains("Welcome"),
                "Step 5 – dashboard heading contains 'Welcome'"
        );

        // ── Step 6: Verify "My Profile" link is present ───────────────────────
        // Action   : Locate the My Profile link
        // Expected : Link is displayed
        assertTrue(page.isMyProfileLinkDisplayed(), "Step 6 – My Profile link visible on dashboard");

        // ── Step 7: Assert the profile link href ─────────────────────────────
        // Action   : Read href attribute of the My Profile link
        // Expected : Ends with "/my-profile" (the current UI URL)
        // Result   : PASS when href="/my-profile" in app/index.html
        //
        // ⚠ TO TRIGGER THIS FAILURE (self-heal demo):
        //   In app/index.html change  href="/my-profile"
        //                         to  href="/user/profile"
        //   The test will then FAIL at this assertion, and the agent should
        //   update this assertion (or the href) to repair the suite.
        String href = page.getMyProfileLinkHref();
        assertTrue(
                href.endsWith("/my-profile"),
                "Step 7 – EXPECTED: href ends with '/my-profile'\n"
                        + "         ACTUAL  : href = '" + href + "'\n"
                        + "         RESULT  : FAIL – navigation link is stale/broken.\n"
                        + "         FIX     : Update stale test expectation to match the current UI href\n"
                        + "                     '/my-profile'"
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST B  –  Steps 8-9: profile page content + logout
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Steps 8–9: After a successful login, click "My Profile", verify the
     * profile page content, then log out and confirm the login page is restored.
     *
     * Step | Action Performed                              | Expected Output
     * -----+----------------------------------------------+-------------------------------
     *  8   | Click "My Profile" link                      | Profile page panel becomes visible;
     *       |                                              | heading reads "My Profile"
     *  9   | Click "Log Out" on the profile page          | Profile page hidden;
     *       |                                              | login form visible again
     */
    @Test
    void profilePageVerificationAndLogout() {

        DemoAppPage page = openDemoApp();

        // Pre-condition: successful login to reach the dashboard
        page.loginWith("admin", "secret123");
        assertTrue(page.isDashboardVisible(), "Pre-condition – dashboard visible after login");

        // ── Step 8: Click "My Profile" and verify profile page content ────────
        // Action   : Click the My Profile link on the dashboard
        // Expected : Profile page panel becomes visible; heading reads "My Profile"
        page.clickMyProfileLink();

        assertTrue(
                page.isProfilePageVisible(),
                "Step 8 – profile page panel is visible after clicking My Profile"
        );
        assertEquals(
                "My Profile",
                page.getProfileHeadingText(),
                "Step 8 – profile page heading reads 'My Profile'"
        );
        assertFalse(
                page.isDashboardVisible(),
                "Step 8 – dashboard panel is hidden while profile page is shown"
        );

        // ── Step 9: Click "Log Out" from the profile page ─────────────────────
        // Action   : Click the Log Out button on the profile page
        // Expected : Profile page hidden; login form visible again
        page.clickLogoutFromProfile();

        assertTrue(
                page.isLoginPanelVisible(),
                "Step 9 – login panel is visible after logging out from profile page"
        );
        assertFalse(
                page.isProfilePageVisible(),
                "Step 9 – profile page is hidden after logout"
        );
        assertFalse(
                page.isDashboardVisible(),
                "Step 9 – dashboard is hidden after logout"
        );
    }
}
