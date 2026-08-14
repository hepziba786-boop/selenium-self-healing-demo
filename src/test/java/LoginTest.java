import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest extends BaseUiTest {

    @Test
    void verifySubmitButtonThroughTheBrowser() {
        DemoAppPage page = openDemoApp();

        assertAll(
                () -> assertEquals("Demo App", page.getHeadingText()),
                () -> assertTrue(page.isSubmitButtonDisplayed(), "submit button should be visible"),
                () -> assertTrue(page.isSubmitButtonEnabled(), "submit button should be enabled"),
                () -> assertEquals("ADD", page.getSubmitButtonText())
        );

        page.clickSubmit();

        assertTrue(page.isSubmitButtonFocused(), "submit button should receive focus after click");
    }
}
