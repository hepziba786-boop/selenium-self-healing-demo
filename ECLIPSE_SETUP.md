# Eclipse Setup Guide for Selenium Self-Healing Demo

Complete guide to run the Intelligent Self-Healing Agent in Eclipse IDE.

---

## Prerequisites

1. **Eclipse IDE for Java Developers** (2023-12 or newer recommended)
   - Download from: https://www.eclipse.org/downloads/
   - Ensure m2e (Maven integration) is installed (usually included by default)

2. **Java 11 or higher**
   - Verify: Open PowerShell and run: `java -version`
   - If not installed, download from: https://adoptium.net/

3. **Google Chrome Browser** (any recent version)
   - WebDriverManager will automatically download the matching ChromeDriver

---

## Step 1: Import Project into Eclipse

1. Launch Eclipse IDE

2. **Import Maven Project:**
   - File → Import → Maven → Existing Maven Projects
   - Click "Browse" and select: `C:\Users\hepziba.selvamani\git\selenium-self-healing-demo\`
   - Check the box next to `pom.xml`
   - Click "Finish"

3. **Wait for dependencies to download:**
   - Eclipse will automatically download:
     - JUnit 5.13.4
     - Selenium 4.24.0
     - WebDriverManager 5.9.2
     - JSoup 1.18.1
   - Check the bottom-right corner for the progress indicator

4. **Verify Project Structure:**
   ```
   selenium-self-healing-demo
   ├── src/test/java
   │   ├── BaseUiTest.java
   │   ├── DemoAppPage.java
   │   ├── FallbackLocator.java
   │   ├── IntelligentSelfHealingAgent.java
   │   ├── LoginTest.java
   │   ├── ProfileNavigationTest.java
   │   └── SelfHealingExtension.java
   ├── app
   │   └── index.html
   ├── pom.xml
   └── RUNBOOK.md
   ```

---

## Step 2: Run Tests in Eclipse

### Option A: Run All Tests

1. Right-click on `src/test/java` folder
2. Select **Run As → JUnit Test**
3. Watch the JUnit view as tests execute

### Option B: Run Individual Test Class

1. Open `LoginTest.java` or `ProfileNavigationTest.java`
2. Right-click inside the editor
3. Select **Run As → JUnit Test**

### Option C: Run Single Test Method

1. Open any test class (e.g., `LoginTest.java`)
2. Locate a test method (e.g., `successfulLoginShowsDashboard()`)
3. Right-click on the method name
4. Select **Run As → JUnit Test**

---

## Step 3: View Self-Healing Reports

After running tests, reports are automatically generated:

### Location:
```
C:\Users\hepziba.selvamani\git\selenium-self-healing-demo\target\self-healing\
```

### Generated Files:
- `LoginTest-report.md` - Report for login tests
- `ProfileNavigationTest-report.md` - Report for profile navigation tests

### View Reports in Eclipse:

1. **Refresh the Project:**
   - Right-click on project → Refresh (F5)

2. **Navigate to Reports:**
   - Expand: `target → self-healing`
   - Double-click on any `.md` file

3. **Better Viewing (Optional):**
   - Install Markdown Editor plugin from Eclipse Marketplace
   - Help → Eclipse Marketplace → Search "Markdown" → Install

---

## Step 4: Run Tests from PowerShell (Alternative)

### Run All Tests:
```powershell
cd 'C:\Users\hepziba.selvamani\git\selenium-self-healing-demo'
mvn clean test
```

### Run Specific Test Class:
```powershell
mvn test -Dtest=LoginTest
```

### Skip Tests (Compile Only):
```powershell
mvn clean compile -DskipTests
```

---

## Understanding the Self-Healing Agent

### What It Does:

1. **Detects Test Failures**
   - Captures detailed failure messages
   - Identifies file and line numbers
   - Records before/after UI state

2. **Analyzes Root Cause**
   - Compares expected vs actual UI elements
   - Parses HTML to find current element selectors
   - Detects stale locators and changed text

3. **Suggests Fixes**
   - Recommends locator updates for `DemoAppPage.java`
   - Suggests assertion updates for test files
   - NEVER modifies `app/index.html`

4. **Generates Reports**
   - Markdown format with emojis for readability
   - Before/After comparison tables
   - Root cause analysis
   - Summary statistics

---

## How to Trigger Self-Healing Demo

### Scenario 1: Button Text Changed

**1. Make a UI change** (for demo purposes):
```html
<!-- In app/index.html, change: -->
<button>Sign In</button>
<!-- to: -->
<button>Login Now</button>
```

**2. Run LoginTest:**
- Right-click `LoginTest.java` → Run As → JUnit Test
- Test `loginPageDisplaysAllExpectedElements()` will FAIL

**3. Check the Report:**
- Open: `target/self-healing/LoginTest-report.md`
- See: Root cause explaining the button text changed
- See: Suggested fix to update the assertion

**4. Apply the Fix:**
- In `LoginTest.java` line 21, change:
  ```java
  () -> assertEquals("Sign In", page.getLoginButtonText(), "login button label"),
  ```
  to:
  ```java
  () -> assertEquals("Login Now", page.getLoginButtonText(), "login button label"),
  ```

**5. Re-run the Test:**
- Right-click `LoginTest.java` → Run As → JUnit Test
- Test should now PASS ✅

### Scenario 2: Profile Link Changed

**1. In `app/index.html`, line 360, change:**
```html
<a href="/my-profile data"
```
**to:**
```html
<a href="/user/profile"
```

**2. Run ProfileNavigationTest:**
- Right-click `ProfileNavigationTest.java` → Run As → JUnit Test
- Test `profileLinkHrefIsStale()` will FAIL

**3. Check the Report:**
- Open: `target/self-healing/ProfileNavigationTest-report.md`
- See: Root cause explaining href changed
- See: Suggested fix to update the test expectation

**4. Apply the Fix:**
- In `ProfileNavigationTest.java` line 105, change:
  ```java
  href.endsWith("/my-profile%20page"),
  ```
  to:
  ```java
  href.endsWith("/user/profile"),
  ```

**5. Re-run the Test:**
- Right-click `ProfileNavigationTest.java` → Run As → JUnit Test
- Test should now PASS ✅

---

## Report Format Explained

The self-healing reports follow the structure defined in `.github/copilot-instructions.md`:

### 🔍 Root Cause
Plain-English explanation of what changed and why the test failed.

### 🔧 Suggested Fixes
Table showing:
- Which file needs changes
- What to change
- Why the change is needed

### 🔄 Detailed Test Flow
Table with columns:
- Test name
- Expected behavior
- Actual result
- Pass/Fail status
- Failure details with line numbers

### 🧪 Summary
Statistics:
- Total tests
- Passed count
- Failed count
- Errors
- Skipped

---

## Troubleshooting

### Problem: Tests fail with "ChromeDriver not found"

**Solution:** WebDriverManager should handle this automatically. If it fails:

```powershell
# Clear the WebDriverManager cache
Remove-Item -Recurse -Force "$env:USERPROFILE\.cache\selenium"
# Re-run tests
mvn clean test
```

### Problem: "Maven dependencies not resolved"

**Solution in Eclipse:**
1. Right-click project → Maven → Update Project
2. Check "Force Update of Snapshots/Releases"
3. Click OK

**Solution in PowerShell:**
```powershell
mvn clean install -U
```

### Problem: Tests pass but no report generated

**Solution:**
1. Verify `@ExtendWith(SelfHealingExtension.class)` is present in test classes
2. Check Eclipse console for errors
3. Manually refresh the project (F5)
4. Look in: `target/self-healing/`

### Problem: Eclipse can't find JUnit tests

**Solution:**
1. Verify JUnit 5 is configured:
   - Right-click project → Properties → Java Build Path → Libraries
   - Should see "JUnit 5" in the list
2. If missing: Right-click project → Build Path → Add Libraries → JUnit → JUnit 5

---

## Key Files Reference

| File | Purpose |
|------|---------|
| **IntelligentSelfHealingAgent.java** | Core agent that analyzes failures and suggests fixes |
| **SelfHealingExtension.java** | JUnit extension that captures test execution data |
| **BaseUiTest.java** | Base class with WebDriver setup (uses WebDriverManager) |
| **DemoAppPage.java** | Page Object with FallbackLocator chains |
| **FallbackLocator.java** | Self-healing locator utility |
| **LoginTest.java** | Login-related test cases |
| **ProfileNavigationTest.java** | Profile navigation test cases |
| **app/index.html** | Demo web application (DO NOT MODIFY in tests) |

---

## Agent Workflow

```
┌─────────────────────────────────────────┐
│  1. Developer runs tests in Eclipse    │
└────────────────┬────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────┐
│  2. SelfHealingExtension captures       │
│     test execution and failures         │
└────────────────┬────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────┐
│  3. IntelligentSelfHealingAgent         │
│     analyzes UI HTML and failures       │
└────────────────┬────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────┐
│  4. Agent suggests fixes:               │
│     - Update locators in DemoAppPage    │
│     - Update assertions in test files   │
│     - NEVER modify app/index.html       │
└────────────────┬────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────┐
│  5. Report generated in                 │
│     target/self-healing/<Test>-report.md│
└────────────────┬────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────┐
│  6. Developer reviews report            │
│     and applies suggested fixes         │
└────────────────┬────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────┐
│  7. Developer re-runs tests             │
│     All tests now PASS ✅               │
└─────────────────────────────────────────┘
```

---

## Best Practices

1. **Always run tests BEFORE making changes**
   - Establish baseline pass/fail state

2. **Review the report thoroughly**
   - Understand the root cause before applying fixes

3. **Apply fixes incrementally**
   - Fix one test at a time
   - Verify each fix works before moving to the next

4. **Never modify app/index.html from tests**
   - The agent follows this rule strictly
   - Only test code (Page Objects and assertions) should be updated

5. **Commit test fixes separately**
   - Make UI changes in one commit
   - Make test fixes in a separate commit
   - Helps with PR reviews

---

## Next Steps

1. ✅ Run all tests to verify setup: `mvn test`
2. ✅ Review generated reports in `target/self-healing/`
3. ✅ Try the demo scenarios above
4. ✅ Check `.github/copilot-instructions.md` for full agent specifications
5. ✅ Read `RUNBOOK.md` for CI/CD integration

---

## Support

For issues or questions:
- Check `RUNBOOK.md` for common scenarios
- Review `.github/copilot-instructions.md` for agent behavior
- Examine existing test reports for examples

**Happy Testing! 🚀**
