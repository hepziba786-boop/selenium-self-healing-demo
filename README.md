# 🤖 Selenium Self-Healing Demo - Eclipse Edition

An intelligent regression testing system that automatically detects, diagnoses, and suggests repairs for broken Selenium tests caused by UI changes.

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://adoptium.net/)
[![Selenium](https://img.shields.io/badge/Selenium-4.24-green.svg)](https://www.selenium.dev/)
[![JUnit](https://img.shields.io/badge/JUnit-5.13-blue.svg)](https://junit.org/junit5/)
[![Eclipse](https://img.shields.io/badge/Eclipse-Ready-purple.svg)](https://www.eclipse.org/)

---

## 📋 Overview

This project demonstrates an **Intelligent Self-Healing Agent** that monitors Selenium test failures and provides automated repair suggestions. The agent analyzes UI changes, compares expected vs actual element states, and generates detailed reports with fix recommendations.

### Key Features

✅ **Automatic Failure Detection** - Captures all test failures with detailed diagnostics  
✅ **Root Cause Analysis** - Analyzes UI HTML to identify stale locators and changed assertions  
✅ **Intelligent Fix Suggestions** - Recommends specific code changes to repair tests  
✅ **Detailed Reporting** - Generates markdown reports with before/after comparisons  
✅ **Eclipse Integration** - Works seamlessly in Eclipse IDE with JUnit 5  
✅ **Zero Manual Setup** - WebDriverManager handles ChromeDriver automatically  
✅ **Safe by Design** - NEVER modifies application source files (only test code)

---

## 🚀 Quick Start (Eclipse)

### 1. Import Project

```
File → Import → Maven → Existing Maven Projects
Select: C:\Users\hepziba.selvamani\git\selenium-self-healing-demo\
```

### 2. Run Tests

```
Right-click: src/test/java → Run As → JUnit Test
```

### 3. View Reports

```
target/self-healing/LoginTest-report.md
target/self-healing/ProfileNavigationTest-report.md
```

📖 **Full Eclipse setup guide:** [ECLIPSE_SETUP.md](ECLIPSE_SETUP.md)

---

## 🏗️ Architecture

### Project Structure

```
selenium-self-healing-demo/
├── src/test/java/
│   ├── IntelligentSelfHealingAgent.java    # Core analysis engine
│   ├── SelfHealingExtension.java           # JUnit 5 extension
│   ├── BaseUiTest.java                     # WebDriver setup
│   ├── DemoAppPage.java                    # Page Object with fallback locators
│   ├── FallbackLocator.java                # Self-healing locator utility
│   ├── LoginTest.java                      # Login test cases
│   └── ProfileNavigationTest.java          # Profile navigation tests
├── app/
│   └── index.html                          # Demo web application
├── .github/
│   └── copilot-instructions.md             # Agent behavior specification
├── pom.xml                                 # Maven dependencies
├── RUNBOOK.md                              # CI/CD integration guide
└── ECLIPSE_SETUP.md                        # Eclipse setup instructions
```

### Component Responsibilities

#### **IntelligentSelfHealingAgent.java**
- Parses `app/index.html` using JSoup
- Extracts current UI element properties
- Compares expected vs actual values
- Suggests locator and assertion fixes
- Generates repair recommendations

#### **SelfHealingExtension.java**
- JUnit 5 extension (runs automatically)
- Captures test execution state (before/after)
- Records failures with file/line numbers
- Integrates with IntelligentSelfHealingAgent
- Generates markdown reports

#### **DemoAppPage.java**
- Page Object Model with FallbackLocator chains
- Each element has 4 fallback selectors: ID → data-testid → CSS → XPath
- Self-healing: tries fallbacks if primary locator fails

#### **FallbackLocator.java**
- Implements fallback locator strategy
- Tries each selector until one succeeds
- Provides built-in resilience to minor UI changes

---

## 🔬 How It Works

### Workflow

```
1. Developer runs tests in Eclipse
   ↓
2. SelfHealingExtension captures execution data
   ↓
3. IntelligentSelfHealingAgent analyzes failures
   ↓
4. Agent parses UI HTML and compares with test expectations
   ↓
5. Agent suggests fixes for stale locators/assertions
   ↓
6. Report generated in target/self-healing/
   ↓
7. Developer reviews report and applies fixes
   ↓
8. Developer re-runs tests → All pass ✅
```

### Example Failure Analysis

**Scenario:** Button text changed from "Sign In" to "Login Now"

**Before Fix:**
```java
// LoginTest.java line 21
assertEquals("Sign In", page.getLoginButtonText(), "login button label")
// ❌ FAIL: expected: <Sign In> but was: <Login Now>
```

**Agent Analysis:**
```markdown
## 🔍 Root Cause
UI text changed from 'Sign In' to 'Login Now'

## 🔧 Suggested Fix
File: LoginTest.java:21
Update test assertion from 'Sign In' to 'Login Now'
```

**After Fix:**
```java
assertEquals("Login Now", page.getLoginButtonText(), "login button label")
// ✅ PASS
```

---

## 📊 Report Format

Reports follow the specification in `.github/copilot-instructions.md`:

### 🔍 Root Cause
Plain-English explanation of what changed in the UI and why the test failed.

### 🔧 Suggested Fixes
| File | What to change | Why |
|------|---------------|-----|
| LoginTest.java | Update assertion from 'Sign In' to 'Login Now' | Button text changed in UI |

### 🔄 Detailed Test Flow
| # | Test Name | Expected | Actual Result | Status | Failure Details |
|---|-----------|----------|---------------|--------|-----------------|
| 1 | loginPageDisplaysAllExpectedElements | Test should pass | ✗ expected: \<Sign In\> but was: \<Login Now\> | FAIL ❌ | LoginTest.java:21 |

### 🧪 Summary
- Total tests: 7
- Passed: 6 ✅
- Failed: 1 ❌
- Errors: 0
- Skipped: 0

---

## 🎯 Demo Scenarios

### Scenario 1: Button Text Changed

1. **Modify UI:** Change "Sign In" to "Login Now" in `app/index.html`
2. **Run Test:** `LoginTest.java` → Run As → JUnit Test
3. **Check Report:** `target/self-healing/LoginTest-report.md`
4. **Apply Fix:** Update assertion in `LoginTest.java`
5. **Verify:** Re-run test → Should pass ✅

### Scenario 2: Link Href Changed

1. **Modify UI:** Change href="/my-profile data" to href="/user/profile" in `app/index.html`
2. **Run Test:** `ProfileNavigationTest.java` → Run As → JUnit Test
3. **Check Report:** `target/self-healing/ProfileNavigationTest-report.md`
4. **Apply Fix:** Update expectation in `ProfileNavigationTest.java`
5. **Verify:** Re-run test → Should pass ✅

Full demo instructions: [ECLIPSE_SETUP.md#how-to-trigger-self-healing-demo](ECLIPSE_SETUP.md#how-to-trigger-self-healing-demo)

---

## 🛡️ Agent Rules

Per `.github/copilot-instructions.md`, the agent follows these strict rules:

✅ **Always:**
- Record BEFORE state for every test (PASS/FAIL)
- Record AFTER state for every test (PASS/FAIL)
- Analyze UI HTML to find correct selectors
- Suggest fixes for stale locators and assertions
- Generate detailed markdown reports
- Include root cause analysis

❌ **Never:**
- Modify application source files (`app/index.html`)
- Skip test verification steps
- Omit any test from the report
- Change business logic in tests
- Hide PASS/FAIL status

---

## 🧪 Testing

### Run All Tests (Eclipse)
```
Right-click: src/test/java → Run As → JUnit Test
```

### Run All Tests (PowerShell)
```powershell
cd 'C:\Users\hepziba.selvamani\git\selenium-self-healing-demo'
mvn clean test
```

### Run Specific Test Class
```powershell
mvn test -Dtest=LoginTest
```

### Run Single Test Method
```powershell
mvn test -Dtest=LoginTest#successfulLoginShowsDashboard
```

---

## 📚 Documentation

- **[ECLIPSE_SETUP.md](ECLIPSE_SETUP.md)** - Complete Eclipse setup and usage guide
- **[RUNBOOK.md](RUNBOOK.md)** - CI/CD integration and workflow reference
- **[.github/copilot-instructions.md](.github/copilot-instructions.md)** - Agent behavior specification

---

## 🔧 Technologies

- **Java 11+** - Programming language
- **Selenium 4.24.0** - Browser automation
- **JUnit 5.13.4** - Testing framework
- **WebDriverManager 5.9.2** - Automatic ChromeDriver setup
- **JSoup 1.18.1** - HTML parsing for UI analysis
- **Maven** - Build and dependency management

---

## 💡 Key Concepts

### FallbackLocator Pattern

Each UI element has 4 locator strategies tried in order:

```java
private static final FallbackLocator LOGIN_BUTTON = FallbackLocator.of(
    By.id("login-btn"),                              // 1. ID (fastest)
    By.cssSelector("[data-testid='login-button']"),  // 2. data-testid
    By.cssSelector("button.btn-login"),              // 3. CSS class
    By.xpath("//button[normalize-space()='Sign In']") // 4. XPath with text
);
```

If the primary locator breaks, fallbacks provide automatic resilience.

### Self-Healing Workflow

1. **Detection** - JUnit extension captures failures
2. **Analysis** - Agent parses UI HTML and failure messages
3. **Suggestion** - Agent recommends specific code fixes
4. **Reporting** - Detailed markdown report generated
5. **Manual Fix** - Developer applies suggested changes
6. **Verification** - Re-run tests to confirm fixes

This approach balances automation with human oversight.

---

## 🚦 Status

✅ **Working Features:**
- Eclipse integration with JUnit 5
- Automatic ChromeDriver setup via WebDriverManager
- Test failure detection and capture
- UI HTML parsing and analysis
- Fix suggestion generation
- Markdown report generation
- Before/After comparison tables

🔄 **Future Enhancements:**
- Automatic code fix application (opt-in)
- Screenshot capture on failure
- GitHub Actions CI/CD integration
- Visual diff for UI changes
- Machine learning for pattern recognition

---

## 📝 License

This is a demonstration project for educational purposes.

---

## 🤝 Contributing

This is a demo project. To adapt for your own use:

1. Copy the agent classes: `IntelligentSelfHealingAgent.java` and `SelfHealingExtension.java`
2. Update `IntelligentSelfHealingAgent` to parse your specific UI
3. Adapt `FallbackLocator` chains in your Page Objects
4. Annotate tests with `@ExtendWith(SelfHealingExtension.class)`
5. Customize report format in `SelfHealingExtension.afterAll()`

---

## 🙋 Support

For questions or issues:

1. Review [ECLIPSE_SETUP.md](ECLIPSE_SETUP.md) for setup help
2. Check [RUNBOOK.md](RUNBOOK.md) for workflow guidance
3. Examine generated reports in `target/self-healing/`
4. Read agent specification in `.github/copilot-instructions.md`

---

**Built with ❤️ for reliable Selenium testing in Eclipse**
