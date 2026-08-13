# Intelligent Regression Testing — Healing Report

**Generated:** 2026-08-13  
**Branch:** main  
**Tool:** Maven + JUnit 5 + Selenium 4.24.0

---

## 1. Regression Suite Run

```
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
Test: LoginTest#verifySubmitButtonThroughTheBrowser
Status: PASSED
```

The suite is now passing after fixing the browser runtime configuration.

---

## 2. Investigation

### HTML Source (`app/index.html`)

```html
<!DOCTYPE html>
<html>
<body>
  <h1>Demo App</h1>
  <button id="saveBtn" data-testid="submit-button" type="button">Submit</button>
</body>
</html>
```

### Selenium Page Object (`DemoAppPage.java`)

| Locator Strategy | Value |
|---|---|
| Primary | `By.id("submitBtn")` |
| Fallback 1 | `By.cssSelector("[data-testid='submit-button']")` |
| Fallback 2 | `By.xpath("//button[normalize-space()='Submit']")` |
| Heading | `By.tagName("h1")` |

---

## 3. Change Detection

| Element | HTML Attribute | Java Locator | Match |
|---|---|---|---|
| Submit button | `id="saveBtn"` | `By.id("submitBtn")` | ❌ |
| Submit button | `data-testid="submit-button"` | `By.cssSelector("[data-testid='submit-button']")` | ✅ |
| Submit button | text `Submit` | `By.xpath("//button[normalize-space()='Submit']")` | ✅ |
| Page heading | `<h1>Demo App</h1>` | `By.tagName("h1")` | ✅ |

The UI had changed from `id="submitBtn"` to `id="saveBtn"`, which broke the primary locator. The fallback selectors still matched and the page object design already supported self-healing.

---

## 4. Issue Identification

The initial regression was not a business-logic bug; it was an environment issue caused by Selenium launching the downloaded Chrome for Testing binary without the required Linux runtime libraries. In this container, Chrome exited before creating the session, producing `SessionNotCreatedException`.

Once the browser runtime was installed and the tests were pointed at the local Chromium binary, the real UI mismatch was also visible: the submit button ID changed from `submitBtn` to `saveBtn`.

---

## 5. Fix Applied

1. Configured Selenium to use the installed system Chromium binary:
   - `/usr/bin/chromium-browser`
2. Explicitly set the ChromeDriver path:
   - `/usr/bin/chromedriver`
3. Kept the resilient fallback locator strategy in `DemoAppPage.java` so the selector remains robust.

This allowed the browser to start and the test to use the correct fallback locator when the button ID changed.

---

## 6. Validation

```bash
cd /workspaces/selenium-self-healing-demo && mvn test -q
```

Result: all tests passed successfully.

---

## 7. Recommendations

| # | Recommendation |
|---|---|
| 1 | Keep `data-testid` attributes on interactive elements for stable automation hooks. |
| 2 | Pin browser and driver tools in CI to the system-installed versions used by the environment. |
| 3 | Keep fallback locators in page objects to absorb harmless UI renames without rewriting business tests. |
