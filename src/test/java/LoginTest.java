import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest {

    @Test
    void verifyButtonExists() throws Exception {

        String html = Files.readString(
                Paths.get("app/index.html")
        );

     boolean found =
        html.contains("submitBtn1")
        || html.contains("saveBtn1");

assertTrue(
        found,
        "Button not found"
);
    }
}
