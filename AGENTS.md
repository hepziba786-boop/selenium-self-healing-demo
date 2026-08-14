# Intelligent Regression Testing Agent

## Goal

Automatically detect and repair broken Selenium UI tests caused by UI changes, without touching application business logic. After every fix, post a detailed, human-readable summary as a PR comment so that developers understand exactly what changed, why it broke, and how it was healed.

## What This Agent Does

1. **Detects failures** — runs `mvn test` and reads the Surefire failure output to identify which assertion or locator broke.
2. **Diagnoses the root cause** — compares the live HTML (`app/index.html` or the target URL) against the Selenium locators and assertions in the page objects and test classes.
3. **Repairs selectors and assertions** — updates only the stale locators or expected values so they match the current UI. Never alters application source files or business logic.
4. **Verifies the fix** — re-runs `mvn test` and only proceeds when all tests pass (0 failures, 0 errors).
5. **Documents every change** — posts a full, explainable PR comment (see format below) and summarises the fix in the session chat.

## Rules

- Do not modify application business logic or HTML source files.
- Only repair tests when the UI has genuinely changed (stale locator, stale text, renamed element, etc.).
- Run `mvn test` after every change; stop only when all tests pass.
- Always post the PR comment in the format described below before closing the task.
- Update this file (`AGENTS.md`) whenever the agent's behaviour or workflow changes.

## PR Comment Format

Every pull request opened or updated by this agent **must** include a comment in the following structure:

```
## 🔍 Root Cause
Explain in plain English what changed in the UI and why the test broke.
Include the exact failure message from the test output.

## 🔧 Files Changed
| File | What changed | Why |
|------|-------------|-----|
| path/to/File.java | old value → new value | brief reason |

## 🧪 Test Result After Fix
Tests run: X, Failures: 0, Errors: 0, Skipped: 0  ✅

## 📌 Locators Used (Self-Healing Strategy)
List the fallback locator chain and which one successfully located the element.
```

## Locator Self-Healing Strategy

The agent uses `FallbackLocator` — a chain of alternative `By` strategies tried in order:
1. `By.id(...)` — most stable; try first.
2. `By.cssSelector("[data-testid='...']")` — semantic attribute; resilient to style changes.
3. `By.cssSelector("tag[attr='value']")` — structural; use when IDs are absent.
4. `By.xpath("//tag[normalize-space()='text']")` — text-based; last resort, update when button labels change.

When a UI change causes a locator to fail, the agent updates the broken strategy and ensures the fallback chain remains coherent and ordered by stability.
