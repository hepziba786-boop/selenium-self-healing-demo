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
* Generate a Pull Request with the fix and details of the fix in the comment of pull request.


## Goal

Reduce manual maintenance of Selenium automation by automatically detecting failures, fixing common issues, validating the solution, and creating a ready-to-review Pull Request.
