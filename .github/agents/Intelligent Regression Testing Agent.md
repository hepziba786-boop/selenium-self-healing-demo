name:# Intelligent Regression Testing Agent

## Overview

The Intelligent Regression Testing Agent is an AI-powered QA assistant that automates the execution, analysis, and maintenance of Selenium regression tests. With a single command, the agent runs the test suite, investigates failures, applies fixes when possible, validates the results, and generates a report with all changes made.

## Trigger

**Command:** `Execute Selenium Test`

## Workflow

### 1. Execute Tests

* Run the Selenium regression suite using Maven.
* Capture test results, logs, screenshots, and error messages.

### 2. Analyze Failures

For each failed test:

* Review stack traces and execution logs.
* Analyze the related HTML and Java files.
* Identify the likely root cause (locator issue, synchronization issue, assertion failure, etc.).

### 3. Apply Fixes

* Update Selenium Java code when a fix can be confidently determined.
* Repair broken locators, waits, or test logic.
* Save the modified files.

### 4. Validate Changes

* Rebuild the project.
* Re-run the affected tests.
* Verify whether the fix resolves the failure.

### 5. Generate Results

Provide a summary including:
* Total tests executed
* Passed tests
* Failed tests
* Fixed/Healed tests
* Build status (Pass/Fail)

### 6. Healing Report

Generate a Detailed Report containing:

* Failed test details
* If Nothing Failes Success Message
* Root cause identified
* Files modified
* Code changes applied
* Validation results

### 7. Create Pull Request in case of fix

Automatically:

* Create a branch for the fix
* Commit the changes
* Generate a Pull Request with a one-line summary in the PR description.
* Post the full fix details as a **PR comment** (using the PR comment tool) in this exact structure:

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

**No duplication rule:** Root Cause, Files Changed, Test Result, and Locator Strategy must appear **exactly once** — in the PR comment above. Do not repeat them in the PR description, PR title, or any response text.

**Single chat output rule:** the full structured report must be shown **once and only once** in the chat. Do not re-display it as a follow-up message, a summary, or a confirmation after posting the PR comment.


## Goal

Reduce manual maintenance of Selenium automation by automatically detecting failures, fixing common issues, validating the solution, and creating a ready-to-review Pull Request.
