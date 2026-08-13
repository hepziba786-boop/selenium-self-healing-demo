import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

public final class FallbackLocator {

    private final List<By> candidates;

    private FallbackLocator(List<By> candidates) {
        this.candidates = candidates;
    }

    public static FallbackLocator of(By... candidates) {
        return new FallbackLocator(Arrays.asList(candidates));
    }

    public WebElement find(WebDriver driver) {
        NoSuchElementException lastFailure = null;

        for (By candidate : candidates) {
            try {
                return driver.findElement(candidate);
            } catch (NoSuchElementException failure) {
                lastFailure = failure;
            }
        }

        throw new NoSuchElementException(
                "Unable to find element with fallback locators: " + candidates,
                lastFailure
        );
    }
}
