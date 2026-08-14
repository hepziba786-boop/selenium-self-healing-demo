# Intelligent Regression Testing Agent

This repository contains Selenium UI tests for a demo web application.
This agent automatically detects, diagnoses, and repairs broken Selenium tests caused by UI changes.

---

## Workflow — When Tests Fail

1. **Run the tests** — execute `mvn test` and capture the full Surefire output.
2. **Read the failure** — identify the exact assertion or locator that broke (error message, file, line number).
3. **Compare UI source with test code** — open `app/index.html` (or the target URL) and compare element IDs, text, attributes against locators in `DemoAppPage.java` and assertions in `LoginTest.java`.
4. **Repair (never rewrite business logic)**:
   - Update stale locators (IDs, CSS selectors, XPath) to match the current UI.
   - Update stale assertion values (button text, heading text, etc.) to match the current UI.
   - Do NOT modify `app/index.html` or any application source.
5. **Re-run tests** — run `mvn test` again. Only proceed when all tests pass (0 failures, 0 errors).
6. **Post a detailed PR comment** using the format below.
7. **Open a pull request** with a clear title and description summarising the self-healing fix.

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

## 🧪 Test Result After Fix
Tests run: X, Failures: 0, Errors: 0, Skipped: 0  ✅

## 📌 Locator Self-Healing Strategy
List the FallbackLocator chain and which strategy successfully located the element.
```

**No duplication rule:** the Root Cause, Files Changed, Test Result, and Locator Strategy details must appear **exactly once** — in the PR comment above. Do not repeat them in the PR description, the PR title body, or any response text. The PR description may contain a one-line summary only.

**Single chat output rule:** the full structured report must be shown **once and only once** in the chat. Do not re-display it as a follow-up message, a summary, or a confirmation after posting the PR comment.

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
- Always run `mvn test` after every change; stop only when all tests pass.
- Always post the structured PR comment (format above) before closing the task.
- Keep this file updated whenever agent behaviour or workflow changes.
