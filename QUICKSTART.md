# ⚡ Quick Start - Get Running in 5 Minutes

The fastest way to get the Selenium Self-Healing Agent working in Eclipse.

---

## Prerequisites ✅

Before you start, ensure you have:

- ✅ **Eclipse IDE** (any recent version with Maven support)
- ✅ **Java 11+** (verify: `java -version` in PowerShell)
- ✅ **Google Chrome** (any recent version)

That's it! No ChromeDriver installation needed - it's handled automatically.

---

## Step 1: Import Project (2 minutes)

1. **Open Eclipse**

2. **Import the Maven project:**
   ```
   File → Import → Maven → Existing Maven Projects
   ```

3. **Select the project folder:**
   - Click "Browse"
   - Navigate to: `C:\Users\hepziba.selvamani\git\selenium-self-healing-demo\`
   - Click "Select Folder"

4. **Finish import:**
   - Check the box next to `pom.xml`
   - Click "Finish"

5. **Wait for dependencies:**
   - Eclipse will download Selenium, JUnit, WebDriverManager, JSoup
   - Watch the progress in bottom-right corner (usually 1-2 minutes)

---

## Step 2: Run Tests (1 minute)

### Option A: Run All Tests

```
Right-click: src/test/java folder
Select: Run As → JUnit Test
```

### Option B: Use the Launch Configuration

```
Run → Run Configurations → JUnit
Select: "Run All Tests"
Click: Run
```

### Option C: Run Individual Test Class

```
Open: LoginTest.java
Right-click in editor
Select: Run As → JUnit Test
```

---

## Step 3: View Results (2 minutes)

### In Eclipse JUnit View:

- ✅ Green bar = All tests passed
- ❌ Red bar = Some tests failed

### View Detailed Reports:

1. **Refresh the project:**
   ```
   Right-click project → Refresh (or press F5)
   ```

2. **Open the reports folder:**
   ```
   Expand: target → self-healing
   ```

3. **View report:**
   ```
   Double-click: LoginTest-report.md
   ```

The report shows:
- 🔍 Root cause of any failures
- 🔧 Suggested fixes
- 🔄 Before/After comparison
- 🧪 Test summary statistics

---

## 🎯 Try a Demo (Optional)

Want to see the self-healing agent in action? Try this:

### Demo: Change Button Text

1. **Open the UI file:**
   ```
   Open: app/index.html
   ```

2. **Find line 363 (around there):**
   ```html
   <button ... class="btn-login">Sign In</button>
   ```

3. **Change "Sign In" to "Login Now":**
   ```html
   <button ... class="btn-login">Login Now</button>
   ```

4. **Save the file**

5. **Run LoginTest again:**
   ```
   Right-click: LoginTest.java → Run As → JUnit Test
   ```

6. **Test will FAIL** ❌
   - One test fails: `loginPageDisplaysAllExpectedElements()`

7. **Check the report:**
   ```
   Refresh project (F5)
   Open: target/self-healing/LoginTest-report.md
   ```

8. **See the analysis:**
   ```markdown
   ## 🔍 Root Cause
   UI text changed from 'Sign In' to 'Login Now'
   
   ## 🔧 Suggested Fix
   File: LoginTest.java:21
   Update test assertion from 'Sign In' to 'Login Now'
   ```

9. **Apply the fix:**
   - Open `LoginTest.java`
   - Go to line 21
   - Change: `assertEquals("Sign In", ...`
   - To: `assertEquals("Login Now", ...`
   - Save

10. **Re-run the test:**
    ```
    Right-click: LoginTest.java → Run As → JUnit Test
    ```

11. **Test now PASSES** ✅

---

## 🚀 What Just Happened?

The **Intelligent Self-Healing Agent** did this:

1. ✅ **Detected** the test failure automatically
2. ✅ **Analyzed** the UI HTML to find what changed
3. ✅ **Compared** expected vs actual button text
4. ✅ **Suggested** the exact fix needed
5. ✅ **Generated** a detailed markdown report

You then applied the fix and verified it worked!

---

## 📚 Next Steps

Now that you're up and running:

1. **Read the full guide:** [ECLIPSE_SETUP.md](ECLIPSE_SETUP.md)
   - Detailed troubleshooting
   - More demo scenarios
   - Best practices

2. **Explore the code:**
   - `IntelligentSelfHealingAgent.java` - Core analysis engine
   - `SelfHealingExtension.java` - JUnit integration
   - `DemoAppPage.java` - Page Object with FallbackLocators

3. **Check the workflow:** [RUNBOOK.md](RUNBOOK.md)
   - CI/CD integration
   - GitHub Actions setup
   - Team collaboration

4. **Review agent rules:** [.github/copilot-instructions.md](.github/copilot-instructions.md)
   - Agent behavior specification
   - What it WILL do
   - What it WON'T do (never modifies app source!)

---

## 🛠️ Troubleshooting

### "Maven dependencies not resolving"

```
Right-click project → Maven → Update Project
Check: "Force Update of Snapshots/Releases"
Click: OK
```

### "ChromeDriver not found"

**This should not happen** - WebDriverManager handles it automatically. If it does:

```powershell
# In PowerShell, clear the cache:
Remove-Item -Recurse -Force "$env:USERPROFILE\.cache\selenium"
```

Then re-run tests in Eclipse.

### "No reports generated"

1. Verify `@ExtendWith(SelfHealingExtension.class)` is in test classes ✅ (it is!)
2. Refresh the project: Right-click → Refresh (F5)
3. Look in: `target/self-healing/`

### "Tests fail with 'element not found'"

This is **expected behavior** when you modify the UI! The agent will:
- Detect the failure
- Analyze what changed
- Suggest how to fix the test

Check the generated report for the suggested fix.

---

## 🎉 You're All Set!

You now have a working intelligent self-healing Selenium test system running in Eclipse!

**Key Points:**
- ✅ Tests run in Eclipse with JUnit 5
- ✅ Failures are automatically analyzed
- ✅ Detailed reports generated with fix suggestions
- ✅ Agent NEVER modifies your application code
- ✅ Only test code (locators & assertions) gets updated

**Questions?**
- Full setup guide: [ECLIPSE_SETUP.md](ECLIPSE_SETUP.md)
- Project overview: [README.md](README.md)
- CI/CD integration: [RUNBOOK.md](RUNBOOK.md)

---

**Happy Testing! 🚀**
