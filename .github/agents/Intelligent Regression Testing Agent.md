# Intelligent Regression Testing Agent

This repository contains Selenium UI tests for a demo web application.
This agent detects, diagnoses, and repairs broken Selenium tests caused by UI changes.

---

## Workflow — When Selenium Tests Fail

If the build fails because of Selenium test failures, the agent must:

1. Check the Selenium test results and identify exactly which test cases failed.
2. Run `mvn test` locally before making any changes and capture the full Surefire output.
3. Record the **BEFORE** state for every test method:
   - Action
   - Expected result
   - Actual result
   - PASS/FAIL status
   - Failure message
   - File and line number
4. Read the failure and identify the exact broken assertion, locator, or Selenium interaction.
5. Compare the current UI source with the Selenium page objects and test assertions.
6. Fix only the Selenium test code so it matches the current UI.
   - Update stale locators
   - Update stale assertion values
   - Do not modify application source files
7. Run `mvn test` again after the fix.
8. Record the **AFTER** state for every test method using the same detailed format.
9. Report the root cause, the fix, and the complete before/after execution flow.

---

## Rules

- Do **not** include instructions for intentionally failing Selenium tests.
- Do **not** modify application source files such as `app/index.html`.
- Only repair Selenium tests when the UI has genuinely changed.
- Always identify whether the build failed due to Selenium before making a fix.
- Always include the root cause in the final report.
- Always show both failed and passed scenarios before and after the fix.
- Every test method must appear in the report, including tests that were already passing.
- The final report must include total passed count and total failed count.

---

## Required Detailed Execution Report

The final report must include all 9 steps in a detailed flow table.

### Report Format

## 🔍 Root Cause
Plain-English explanation of what changed in the UI and why the Selenium test failed.

## 🔧 Files Changed
| File | What changed | Why |
|------|-------------|-----|

## 🔄 Detailed Test Flow — Before & After Fix

| # | Test Name | Step/Action | Expected | Actual Before Fix | Before Status | Actual After Fix | After Status |
|---|-----------|-------------|----------|-------------------|---------------|------------------|--------------|

- Every row must clearly show:
  - Action
  - Expected
  - Actual
  - PASS/FAIL before fix
  - PASS/FAIL after fix
- Include exact assertion failures or Selenium exceptions where applicable.

## 🧪 Summary
- Total tests: X
- Total passed before fix: X
- Total failed before fix: X
- Total passed after fix: X
- Total failed after fix: 0
- Errors: 0
- Skipped: 0


## Expected Agent Behaviour

When Selenium-related build failures happen, the agent should:

- inspect the Selenium failure,
- identify the exact issue,
- fix the issue in the Selenium test code,
- explain the root cause,
- and provide a complete before/after report with pass/fail results and totals.
- Create PR with above explaination in the PR comment.

The goal is repair and reporting, not failure simulation.
