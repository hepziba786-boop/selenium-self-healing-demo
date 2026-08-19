# 🏗️ Architecture Overview

Visual representation of the Intelligent Self-Healing Agent architecture.

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         ECLIPSE IDE                                  │
│                                                                      │
│  ┌────────────────────────────────────────────────────────────┐   │
│  │                    Test Execution Layer                     │   │
│  │                                                              │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │   │
│  │  │ LoginTest.java│  │ProfileNavTest│  │ Future Tests │     │   │
│  │  │              │  │              │  │              │     │   │
│  │  │ @ExtendWith( │  │ @ExtendWith( │  │              │     │   │
│  │  │  SelfHealing │  │  SelfHealing │  │              │     │   │
│  │  │  Extension)  │  │  Extension)  │  │              │     │   │
│  │  └──────┬───────┘  └──────┬───────┘  └──────────────┘     │   │
│  │         │                  │                                │   │
│  └─────────┼──────────────────┼────────────────────────────────┘   │
│            │                  │                                     │
│            └──────────┬───────┘                                     │
│                       │                                              │
│  ┌────────────────────▼───────────────────────────────────────┐   │
│  │         SelfHealingExtension.java (JUnit 5)                │   │
│  │                                                             │   │
│  │  • beforeTestExecution() - Capture UI state                │   │
│  │  • testSuccessful()      - Record PASS                     │   │
│  │  • testFailed()          - Record FAIL + analyze           │   │
│  │  • afterAll()            - Generate report                 │   │
│  │                                                             │   │
│  └─────────────────────┬───────────────────────────────────────┘   │
│                        │                                            │
│                        │ Calls on failure                           │
│                        ▼                                            │
│  ┌──────────────────────────────────────────────────────────┐     │
│  │     IntelligentSelfHealingAgent.java                     │     │
│  │                                                           │     │
│  │  Core Analysis Engine:                                   │     │
│  │  ┌─────────────────────────────────────────────────┐    │     │
│  │  │ 1. loadUISource()                                │    │     │
│  │  │    • Parse app/index.html with JSoup            │    │     │
│  │  │    • Build DOM tree                             │    │     │
│  │  └─────────────────────────────────────────────────┘    │     │
│  │  ┌─────────────────────────────────────────────────┐    │     │
│  │  │ 2. extractUITextValues()                        │    │     │
│  │  │    • Get current element text                   │    │     │
│  │  │    • Get current attributes (href, etc)         │    │     │
│  │  │    • Map element IDs to actual values           │    │     │
│  │  └─────────────────────────────────────────────────┘    │     │
│  │  ┌─────────────────────────────────────────────────┐    │     │
│  │  │ 3. analyzeFailure()                             │    │     │
│  │  │    • Compare expected vs actual                 │    │     │
│  │  │    • Identify stale locators                    │    │     │
│  │  │    • Detect changed text/attributes             │    │     │
│  │  │    • Generate RepairSuggestion                  │    │     │
│  │  └─────────────────────────────────────────────────┘    │     │
│  │  ┌─────────────────────────────────────────────────┐    │     │
│  │  │ 4. suggestLocators()                            │    │     │
│  │  │    • Recommend new locator strategies           │    │     │
│  │  │    • Build FallbackLocator chains               │    │     │
│  │  └─────────────────────────────────────────────────┘    │     │
│  │  ┌─────────────────────────────────────────────────┐    │     │
│  │  │ 5. generateReport()                             │    │     │
│  │  │    • Root cause analysis                        │    │     │
│  │  │    • Files changed table                        │    │     │
│  │  │    • Before/After flow table                    │    │     │
│  │  │    • Summary statistics                         │    │     │
│  │  └─────────────────────────────────────────────────┘    │     │
│  │                                                           │     │
│  └──────────────────┬────────────────────────────────────────┘     │
│                     │                                              │
│                     │ Generates                                    │
│                     ▼                                              │
│  ┌───────────────────────────────────────────────────────────┐   │
│  │           target/self-healing/*.md Reports                │   │
│  │                                                            │   │
│  │  📄 LoginTest-report.md                                   │   │
│  │  📄 ProfileNavigationTest-report.md                       │   │
│  │                                                            │   │
│  │  Each contains:                                           │   │
│  │  • 🔍 Root Cause                                          │   │
│  │  • 🔧 Suggested Fixes                                     │   │
│  │  • 🔄 Detailed Test Flow (Before/After)                  │   │
│  │  • 🧪 Summary Statistics                                 │   │
│  │                                                            │   │
│  └────────────────────────────────────────────────────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘

         │                           │
         │                           │
         ▼                           ▼

┌─────────────────────┐    ┌──────────────────────────────┐
│  Page Objects Layer │    │   Application Under Test      │
│                     │    │                               │
│  DemoAppPage.java   │    │   app/index.html              │
│                     │    │                               │
│  • FallbackLocator  │    │   • Login Panel               │
│    chains           │    │   • Dashboard                 │
│  • Self-healing     │    │   • Profile Page              │
│    element finding  │◄───┤                               │
│                     │    │   ⚠️ NEVER modified by agent  │
│  ✅ Updated by      │    │   ✅ Source of truth for UI  │
│     agent when      │    │                               │
│     locators stale  │    │                               │
└─────────────────────┘    └──────────────────────────────┘

         │
         │
         ▼

┌─────────────────────┐
│  Base Infrastructure│
│                     │
│  BaseUiTest.java    │
│                     │
│  • WebDriverManager │
│  • ChromeDriver     │
│    auto-setup       │
│  • Headless mode    │
│                     │
└─────────────────────┘
```

---

## Data Flow

### 1. Test Execution Flow

```
Developer
   │
   │ Runs test in Eclipse
   ▼
JUnit 5 Test Runner
   │
   │ Loads test class
   ▼
@BeforeEach: BaseUiTest.setUpDriver()
   │
   │ WebDriverManager.chromedriver().setup()
   │ Creates ChromeDriver instance
   ▼
@BeforeTestExecution: SelfHealingExtension
   │
   │ Captures UI snapshot
   │ Records "BEFORE" state
   ▼
Test Method Executes
   │
   ├─ PASS? ──► @TestSuccessful: Record PASS status
   │
   └─ FAIL? ──► @TestFailed: Capture failure details
                    │
                    │ Extract file:line from stack trace
                    │ Call IntelligentSelfHealingAgent.analyzeFailure()
                    ▼
                 IntelligentSelfHealingAgent
                    │
                    │ loadUISource() - Parse HTML
                    │ extractUITextValues() - Get current UI state
                    │ Compare expected vs actual
                    │ Identify root cause
                    │ Generate RepairSuggestion
                    ▼
                 SelfHealingExtension
                    │
                    │ Store suggestion in TestRecord
                    ▼
@AfterEach: BaseUiTest.tearDownDriver()
   │
   │ driver.quit()
   ▼
@AfterAll: SelfHealingExtension.afterAll()
   │
   │ Collect all TestRecords for class
   │ Generate markdown report
   │ Write to target/self-healing/<TestClass>-report.md
   ▼
Report Ready
   │
   │ Developer opens in Eclipse
   │ Reviews root cause & suggestions
   │ Applies fixes
   │ Re-runs tests
   ▼
All Tests PASS ✅
```

---

## Component Interaction Matrix

| Component | Reads From | Writes To | Purpose |
|-----------|-----------|-----------|---------|
| **LoginTest** | app/index.html (via WebDriver) | N/A | Execute test scenarios |
| **SelfHealingExtension** | Test execution context, WebDriver | target/self-healing/*.md | Capture & report test results |
| **IntelligentSelfHealingAgent** | app/index.html (via JSoup), test failures | N/A (analysis only) | Analyze failures & suggest fixes |
| **DemoAppPage** | WebDriver → app/index.html | N/A | Provide page element accessors |
| **FallbackLocator** | WebDriver | N/A | Find elements with fallback strategies |
| **BaseUiTest** | N/A | WebDriver instance | Setup/teardown test infrastructure |

---

## Failure Analysis Pipeline

```
Test Fails
   │
   ▼
┌─────────────────────────────────────┐
│ 1. Capture Exception                │
│    • Get failure message            │
│    • Extract file:line from stack   │
│    • Identify test method name      │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ 2. Parse Failure Message            │
│    • Regex for "expected:<X> but    │
│      was:<Y>"                        │
│    • Detect NoSuchElementException  │
│    • Identify assertion type        │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ 3. Load UI Source                   │
│    • Read app/index.html            │
│    • Parse with JSoup               │
│    • Build DOM tree                 │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ 4. Extract Current UI State         │
│    • Find element by ID/testid      │
│    • Get text content               │
│    • Get attributes (href, class)   │
│    • Map to element identifiers     │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ 5. Compare Expected vs Actual       │
│    • Match failure message to UI    │
│    • Identify changed values        │
│    • Detect stale locators          │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ 6. Generate Root Cause              │
│    • "UI text changed from X to Y"  │
│    • "Element locator is stale"     │
│    • "Href attribute changed"       │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ 7. Create Fix Suggestion            │
│    • File: <TestFile.java>:<line>   │
│    • Action: "Update assertion      │
│      from 'X' to 'Y'"                │
│    • Rationale: <root cause>        │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ 8. Add to Report                    │
│    • RepairSuggestion object        │
│    • Stored in TestRecord           │
│    • Used in markdown generation    │
└─────────────────────────────────────┘
```

---

## Report Generation Process

```
@AfterAll triggered
   │
   ▼
Collect TestRecords for class
   │
   │ Filter by className
   │ Preserve execution order
   ▼
Calculate Statistics
   │
   │ Total tests
   │ Passed count
   │ Failed count
   ▼
Build Markdown Sections
   │
   ├─► 🔍 Root Cause
   │   • First failure's suggestion.rootCause
   │   • Or "All tests passing"
   │
   ├─► 🔧 Suggested Fixes
   │   • Table with file, change, reason
   │   • One row per unique fix needed
   │
   ├─► 🔄 Detailed Test Flow
   │   • Table with all test results
   │   • Show PASS/FAIL status
   │   • Include failure details
   │
   └─► 🧪 Summary
       • Statistics (passed/failed/total)
       • Errors: 0, Skipped: 0
       ▼
Write to File
   │
   │ Path: target/self-healing/<ClassName>-report.md
   │ Format: Markdown with emojis
   ▼
Report Available
   │
   │ Developer refreshes Eclipse
   │ Opens .md file
   │ Reviews and applies fixes
   ▼
Done
```

---

## Technology Stack

```
┌─────────────────────────────────────┐
│         Application Layer           │
│                                     │
│  • Java 11+                         │
│  • JUnit 5 (Jupiter)                │
│  • Custom test classes              │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│      Test Framework Layer           │
│                                     │
│  • JUnit 5 Extensions API           │
│  • @BeforeEach, @AfterAll hooks     │
│  • Test lifecycle management        │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│     Selenium & WebDriver Layer      │
│                                     │
│  • Selenium 4.24.0                  │
│  • WebDriverManager 5.9.2           │
│  • ChromeDriver (auto-downloaded)   │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│      Analysis & Parsing Layer       │
│                                     │
│  • JSoup 1.18.1 (HTML parsing)      │
│  • Regex (failure message parsing)  │
│  • Reflection (WebDriver access)    │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│        Reporting Layer              │
│                                     │
│  • Markdown generation              │
│  • File I/O (java.nio.file)         │
│  • Template-based reports           │
└─────────────────────────────────────┘
```

---

## Safety Boundaries

```
┌──────────────────────────────────────────────┐
│          APPLICATION SOURCE                   │
│                                              │
│  app/index.html                              │
│                                              │
│  ⚠️  NEVER TOUCHED BY AGENT                 │
│  ✅  Read-only access via JSoup             │
│  ✅  Source of truth for UI state           │
│                                              │
└──────────────────────────────────────────────┘

        ▲ Read-only (via JSoup)
        │
        │
        │ Analysis flow
        │
        ▼

┌──────────────────────────────────────────────┐
│      INTELLIGENT SELF-HEALING AGENT          │
│                                              │
│  IntelligentSelfHealingAgent.java            │
│                                              │
│  ✅  Reads UI HTML                          │
│  ✅  Analyzes failures                      │
│  ✅  Generates suggestions                  │
│  ❌  Does NOT modify any files              │
│                                              │
└──────────────────────────────────────────────┘

        │
        │ Suggestions
        │
        ▼

┌──────────────────────────────────────────────┐
│          MARKDOWN REPORTS                    │
│                                              │
│  target/self-healing/*.md                    │
│                                              │
│  ✅  Created by extension                   │
│  ✅  Read by developer                      │
│  ✅  Contain fix suggestions                │
│                                              │
└──────────────────────────────────────────────┘

        │
        │ Developer reviews
        │
        ▼

┌──────────────────────────────────────────────┐
│          DEVELOPER (HUMAN)                   │
│                                              │
│  ✅  Reviews report                         │
│  ✅  Understands root cause                 │
│  ✅  Applies fix to test code               │
│  ✅  Re-runs tests to verify                │
│                                              │
└──────────────────────────────────────────────┘

        │
        │ Manual edits
        │
        ▼

┌──────────────────────────────────────────────┐
│          TEST CODE (Fixable by Agent)        │
│                                              │
│  • DemoAppPage.java (locators)               │
│  • LoginTest.java (assertions)               │
│  • ProfileNavigationTest.java (assertions)   │
│                                              │
│  ✅  May be updated by developer            │
│  ✅  Based on agent suggestions             │
│  ❌  NOT auto-modified (human approval)     │
│                                              │
└──────────────────────────────────────────────┘
```

---

## Key Design Principles

1. **Separation of Concerns**
   - Extension: Captures data
   - Agent: Analyzes data
   - Reports: Present findings
   - Developer: Applies fixes

2. **Read-Only Analysis**
   - Agent never modifies files
   - Only reads UI HTML
   - Only generates suggestions

3. **Human in the Loop**
   - Agent suggests
   - Human reviews
   - Human applies
   - Human verifies

4. **Clear Boundaries**
   - App source: Read-only, never modified
   - Test code: Fixable, but requires approval
   - Reports: Write-only output

5. **Fail-Safe Design**
   - If analysis fails, still generates report
   - If UI parsing fails, records error
   - If WebDriver unavailable, captures state

---

## Extension Points

The architecture allows easy extension:

### Add New Test Classes
```java
@ExtendWith(SelfHealingExtension.class)
public class NewFeatureTest extends BaseUiTest {
    // Automatic reporting enabled
}
```

### Add New Page Objects
```java
public class NewPage {
    private static final FallbackLocator NEW_ELEMENT = 
        FallbackLocator.of(
            By.id("new-element"),
            By.cssSelector("[data-testid='new-element']"),
            // ... more fallbacks
        );
}
```

### Customize Analysis
```java
// In IntelligentSelfHealingAgent.java
public RepairSuggestion analyzeFailure(...) {
    // Add custom failure pattern detection
    // Add custom fix suggestion logic
}
```

### Customize Reports
```java
// In SelfHealingExtension.java
public void afterAll(...) {
    // Customize markdown format
    // Add new report sections
    // Change output location
}
```

---

**For detailed usage instructions, see [QUICKSTART.md](QUICKSTART.md) or [ECLIPSE_SETUP.md](ECLIPSE_SETUP.md)**
