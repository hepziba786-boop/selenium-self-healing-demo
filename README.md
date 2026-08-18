# Selenium Self-Healing Demo

## Intelligent Regression Testing Agent — Repository-Specific Problem Statement

### Team
Selvamani, Hepziba Nadar, and Tejal Vijay

### Repository
`hepziba786-boop/selenium-self-healing-demo`

### Technology Stack
Selenium WebDriver, Java, Maven, GitHub Actions, GitHub Copilot Agent, Git

---

## Problem Statement

This repository demonstrates how UI regression tests can break when a web interface changes even though the underlying user flow still works.

The demo application in `app/index.html` represents a lightweight ProjectFlow login, dashboard, and profile experience. The Selenium suite in `src/test/java` validates that experience through end-to-end browser tests. These tests rely on locators, visible labels, button text, headings, and navigation expectations. When the UI changes, the tests can fail because the automation no longer matches the page.

This creates a realistic maintenance problem for QA engineers:
- GitHub Actions reports a failing Selenium run
- Engineers must inspect Surefire output and identify the exact broken step
- They must compare the current UI with page objects and assertions
- They must update stale locators or expected values in the Selenium suite
- They must rerun the suite and confirm that the repair is safe

The goal of this repository is to show how an intelligent GitHub Copilot Agent can assist with that maintenance workflow instead of leaving the entire investigation and repair process to a human.

---

## Project Objective

The objective of this repository is to demonstrate an agent-assisted Selenium self-healing workflow for UI regression failures.

In this repo, the workflow is:
1. The demo UI changes in `app/index.html`
2. The `Selenium Tests` GitHub Actions workflow runs automatically
3. One or more Selenium tests fail in `LoginTest` or `ProfileNavigationTest`
4. A self-heal workflow opens or updates a tracked request when the failure is tied to a UI change
5. A QA engineer or Copilot automation invokes the Intelligent Regression Testing Agent
6. The agent analyzes CI logs, Surefire output, Selenium page objects, and the current UI
7. The agent updates only Selenium test code or page-object locators to match the new UI
8. The agent reruns `mvn test` to validate the repair
9. The agent produces a structured repair summary and proposes the change in a pull request

The focus is intelligent diagnosis and repair of broken UI automation, not just test execution.

---

## Current Repository Use Case

This repository is built around a self-healing demo for the ProjectFlow UI:
- `LoginTest` verifies login page rendering, successful login, invalid login handling, and logout behavior
- `ProfileNavigationTest` verifies dashboard-to-profile navigation and profile logout behavior
- `DemoAppPage` centralizes page interactions and fallback locators
- `FallbackLocator` demonstrates ordered locator recovery across multiple selector strategies
- `.github/workflows/self-heal-on-selenium-failure.yml` opens a self-heal issue when the main Selenium workflow fails after a UI change in `app/index.html`

This makes the agent use case concrete: repair Selenium tests after UI drift without modifying the application source as part of the fix.

---

## Scope

### In Scope
- Browser-based UI regression testing for the demo app
- Selenium WebDriver automation in Java
- Maven-based local and CI execution
- GitHub Actions failure detection
- GitHub Copilot Agent-guided failure investigation
- Selenium page-object and assertion repair
- Pull request generation for test-only fixes

### Out of Scope
- API or backend service testing
- Database validation
- Performance or load testing
- Mobile testing
- Production-grade self-healing across large enterprise systems
- Application feature changes as part of the repair flow

---

## Architecture and Workflow

1. A contributor changes the demo UI in `app/index.html`
2. GitHub Actions runs the `Selenium Tests` workflow
3. Selenium assertions or locators no longer match the UI
4. The CI run fails
5. The self-heal workflow detects the failure and opens or updates a self-heal issue
6. The Intelligent Regression Testing Agent investigates the failure
7. The agent correlates:
   - CI failure logs
   - Surefire output
   - `DemoAppPage` fallback locators
   - test assertions in `LoginTest` and `ProfileNavigationTest`
   - the current UI markup in `app/index.html`
8. The agent repairs only the Selenium test code
9. The agent reruns the regression suite
10. The agent summarizes the root cause and proposes the fix for review

---

## Why This Solution Is Agentic

Traditional automation stops at failure detection. This repository extends that flow by giving an agent enough context and tooling to continue with diagnosis, repair, validation, and reporting.

The agent does not just rerun tests. It reasons over:
- what failed in CI,
- which UI element or assertion became stale,
- where that expectation lives in the Selenium suite,
- and how to update the automation while preserving application behavior.

This turns the repository into a practical demo of AI-assisted regression maintenance.

---

## Expected Outcome

Before a UI change:
- the ProjectFlow demo page and Selenium expectations align
- `mvn test` passes

After a UI change:
- the business flow may still work
- one or more Selenium tests fail because locators or assertions are stale

After agent repair:
- the Selenium suite matches the current UI again
- `mvn test` passes
- the repair is documented for review

---

## Key Repository Files

| File | Purpose |
|------|---------|
| `app/index.html` | Demo UI that intentionally evolves over time |
| `src/test/java/DemoAppPage.java` | Page object and fallback locator definitions |
| `src/test/java/FallbackLocator.java` | Ordered locator recovery utility |
| `src/test/java/LoginTest.java` | Login regression coverage |
| `src/test/java/ProfileNavigationTest.java` | Profile navigation regression coverage |
| `.github/workflows/test.yml` | Main Selenium CI workflow |
| `.github/workflows/self-heal-on-selenium-failure.yml` | Failure intake workflow for self-heal requests |
| `.github/copilot-instructions.md` | Agent operating instructions for repair sessions |
| `RUNBOOK.md` | Manual invocation guide for maintainers |

---

## Benefits of This Demo

- Reduces manual effort to diagnose UI-driven Selenium failures
- Demonstrates structured agent-assisted regression repair
- Shows how CI, Selenium, and Copilot Agent workflows can be connected
- Improves auditability through issue creation, validation, and pull requests
- Provides a compact reference implementation for self-healing UI test maintenance
