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

        assertTrue(
    html.contains("submitBtn"),
    "submitBtn not found"
);
    }
}
