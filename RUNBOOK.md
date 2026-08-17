# Selenium Failure Runbook

Quick reference for developers on how to invoke the Intelligent Regression Testing Agent when the Selenium build fails.

---

## When to Use This

Use this runbook whenever:
- The GitHub Actions CI workflow shows a **red ❌ test run**
- Running `mvn test` locally produces **failures or errors**
- A Selenium locator can no longer find an element after a UI update
- A test assertion fails because a label, heading, or value changed in the UI

---

## How to Invoke the Agent

### Option 1 — GitHub Copilot Chat (VS Code / github.com)

Open Copilot Chat and use the `@workspace` command with the agent name:

```
@workspace selenium-self-healing-demo /agent "Intelligent Regression Testing Agent"
```

The agent will:
1. Run `mvn test` **before** any change and record each test's explicit **PASSED ✅** / **FAILED ❌** status
2. Compare the UI source against Selenium locators and assertions
3. Repair only the broken selectors / expected values
4. Re-run `mvn test` **after** the fix and record each test's explicit **PASSED ✅** / **FAILED ❌** status
5. Open a PR with a structured comment that includes a full **End-to-End Before & After table** showing every test row with before/after pass-fail status and failure details where applicable

---

### Option 2 — Describe the Failure in Chat

If you already know which test failed, paste the failure message directly:

```
The LoginTest is failing with: expected: <APPLY> but was: <ENTER>
Please fix the Selenium test to match the current UI.
```

---

### Option 3 — From a CI Failure

1. Open the failed GitHub Actions run.
2. Copy the failure output from the **Surefire** step.
3. Open Copilot Chat and paste:

```
@workspace selenium-self-healing-demo
The following Selenium test failed in CI. Please diagnose and fix:

[paste failure output here]
```

---

## What the Agent Will NOT Do

- Modify `app/index.html` or any application source file
- Change business logic in test classes
- Skip the `mvn test` verification step (before **and** after the fix)
- Omit any test from the End-to-End Before & After flow table
- Omit explicit **PASSED ✅** / **FAILED ❌** status from any Before/After flow row
- Open a PR without posting the structured diagnostic comment including the flow table

---

## Key Files

| File | Purpose |
|------|---------|
| `app/index.html` | The demo web application (source of truth for the UI) |
| `src/test/java/LoginTest.java` | Test assertions — updated when UI text changes |
| `src/test/java/DemoAppPage.java` | Page object with `FallbackLocator` chains — updated when element locators change |
| `src/test/java/FallbackLocator.java` | Self-healing locator utility (do not modify) |
| `src/test/java/BaseUiTest.java` | Driver setup / teardown (do not modify) |
| `.github/copilot-instructions.md` | Agent instructions loaded by Copilot chat on every session |

---

## Running Tests Manually

```bash
# Run all tests
mvn test

# View the latest test report
cat target/surefire-reports/LoginTest.txt
```
