# Intelligent Regression Testing Agent

This repository contains Selenium UI tests for a demo web application.
This agent automatically detects, diagnoses, and repairs broken Selenium tests caused by UI changes.

---

## Workflow — When Tests Fail

1. **Run the tests (BEFORE)** — execute `mvn test` and capture the full Surefire output.
2. **Record BEFORE status** — for every test method and every row in the end-to-end flow table, note whether it **PASSED ✅** or **FAILED ❌** along with the exact failure message and line number.
3. **Read the failure** — identify the exact assertion or locator that broke (error message, file, line number).
4. **Compare UI source with test code** — open `app/index.html` (or the target URL) and compare element IDs, text, attributes against locators in `DemoAppPage.java` and assertions in `LoginTest.java`.
5. **Repair (never rewrite business logic)**:
   - Update stale locators (IDs, CSS selectors, XPath) to match the current UI.
   - Update stale assertion values (button text, heading text, etc.) to match the current UI.
   - Do NOT modify `app/index.html` or any application source.
6. **Re-run tests (AFTER)** — run `mvn test` again. Only proceed when all tests pass (0 failures, 0 errors).
7. **Record AFTER status** — for every test method and every row in the end-to-end flow table, note the new result **PASSED ✅** or **FAILED ❌**.
8. **Post a detailed PR comment** using the format below (which must include the full end-to-end flow table).
9. **Open a pull request** with a clear title and description summarising the self-healing fix.

---

## PR Comment Format

Every pull request opened by this agent **must** include a comment posted directly on the PR (using the PR comment tool, not only in the PR description) in this exact structure:

```
## 🔍 Root Cause
Plain-English explanation of what changed in the UI and why the test broke.
Include the exact failure message from the test output.

## 🔧 Files Changed
| File | What changed | Why |
|------|-------------|-----|
| path/to/File.java | old value → new value | brief reason |

## 🔄 End-to-End Test Flow — Before & After Fix

| # | Test Name | Before Fix | After Fix | Failure Detail (if any) |
|---|-----------|:----------:|:---------:|------------------------|
| 1 | loginPageDisplaysAllExpectedElements | ❌ FAILED | ✅ PASSED | e.g. expected "Sign in" but was "Login" |
| 2 | successfulLoginShowsDashboard        | ✅ PASSED | ✅ PASSED | — |
| 3 | invalidCredentialsShowsErrorBanner   | ✅ PASSED | ✅ PASSED | — |
| 4 | emptyUsernameShowsErrorBanner        | ❌ FAILED | ✅ PASSED | e.g. NoSuchElementException on #username |
| 5 | emptyPasswordShowsErrorBanner        | ✅ PASSED | ✅ PASSED | — |
| 6 | logoutReturnsToLoginPage             | ✅ PASSED | ✅ PASSED | — |
| 7 | loginButtonReceivesFocusAfterClick   | ✅ PASSED | ✅ PASSED | — |

> Replace the example values above with the **actual** before/after results from each `mvn test` run.
> Every test must appear in the table — including those that were already passing.
> The "Failure Detail" column must contain the exact assertion or exception message for any FAILED row, or "—" for rows that passed.
> Show the full flow for all test rows with explicit **PASSED ✅** or **FAILED ❌** status in both the Before Fix and After Fix columns; do not summarize or collapse any row.

## 🧪 Summary
Tests run: X, Failures: 0, Errors: 0, Skipped: 0  ✅

## 📌 Locator Self-Healing Strategy
List the FallbackLocator chain and which strategy successfully located the element.
```

**No duplication rule:** the Root Cause, Files Changed, End-to-End Flow table, Summary, and Locator Strategy details must appear **exactly once** — in the PR comment above. Do not repeat them in the PR description, the PR title body, or any response text. The PR description may contain a one-line summary only.

**Single chat output rule:** the full structured report must be shown **once and only once** in the chat. Do not re-display it as a follow-up message, a summary, or a confirmation after posting the PR comment.

**End-to-End Flow table rule:** every test method must appear as its own row in the Before & After table. Do not omit passing tests. The BEFORE column reflects the `mvn test` run taken *before* any fix; the AFTER column reflects the `mvn test` run taken *after* the fix.

---

## Locator Self-Healing Strategy (`FallbackLocator`)

The `FallbackLocator` class tries a chain of `By` strategies in order of stability:

| Priority | Strategy | When to use |
|----------|----------|-------------|
| 1 | `By.id("...")` | Most stable — prefer always |
| 2 | `By.cssSelector("[data-testid='...']")` | Semantic attribute — resilient to style changes |
| 3 | `By.cssSelector("tag[attr='value']")` | Structural — use when IDs are absent |
| 4 | `By.xpath("//tag[normalize-space()='text']")` | Text-based — last resort; update when labels change |

When a locator fails, update the broken strategy to match the current UI. Keep all four strategies coherent and ordered by stability.

---

## Rules

- Never modify application source files (`app/index.html`, etc.).
- Only repair tests when the UI has genuinely changed.
- Always run `mvn test` **before** making any change and record each test's BEFORE status.
- Always run `mvn test` **after** every fix; stop only when all tests pass (0 failures, 0 errors) and record each test's AFTER status.
- Every test method must appear in the End-to-End Flow table — including tests that were already passing.
- Every End-to-End Flow row must show explicit **PASSED ✅** or **FAILED ❌** status before and after the fix.
- Always post the structured PR comment (format above) before closing the task.
- Keep this file updated whenever agent behaviour or workflow changes.
