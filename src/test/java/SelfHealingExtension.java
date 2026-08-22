import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Enhanced JUnit 5 extension that integrates with IntelligentSelfHealingAgent.
 * 
 * This extension:
 * 1. Collects detailed before/after test execution data
 * 2. Captures failure details with file/line information
 * 3. Integrates with the IntelligentSelfHealingAgent for automatic repair suggestions
 * 4. Generates comprehensive markdown reports in target/self-healing/
 * 
 * Per repository guidelines:
 * - Records ALL test results (PASS/FAIL)
 * - Provides detailed before/after flow comparison
 * - Suggests repairs when tests fail
 * - Never modifies application source files
 */
public class SelfHealingExtension implements BeforeTestExecutionCallback, TestWatcher, AfterAllCallback {

    private static final Map<String, TestRecord> records = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final IntelligentSelfHealingAgent agent = new IntelligentSelfHealingAgent();

    private static class TestRecord {
        String className;
        String methodName;
        String beforeSnapshot;
        String expected;
        String actualBefore;
        String beforeStatus = "NOT RUN";
        String failureMessage;
        String failureFileLine;
        String actualAfter;
        String afterStatus = "NOT RUN";
        IntelligentSelfHealingAgent.RepairSuggestion suggestion;
    }

    private String key(ExtensionContext ctx) {
        return ctx.getRequiredTestClass().getName() + "#" + ctx.getRequiredTestMethod().getName();
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        String k = key(context);
        TestRecord r = new TestRecord();
        r.className = context.getRequiredTestClass().getName();
        r.methodName = context.getRequiredTestMethod().getName();
        r.expected = "Test preconditions met";

        // Capture UI state snapshot
        try {
            Object testInstance = context.getRequiredTestInstance();
            Field driverField = testInstance.getClass().getSuperclass().getDeclaredField("driver");
            driverField.setAccessible(true);
            WebDriver driver = (WebDriver) driverField.get(testInstance);

            if (driver == null) {
                r.actualBefore = "driver=null";
            } else {
                r.actualBefore = captureUISnapshot(driver);
            }
        } catch (Exception ex) {
            r.actualBefore = "snapshot-error: " + ex.getMessage();
        }

        records.put(k, r);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        String k = key(context);
        TestRecord r = records.getOrDefault(k, new TestRecord());
        r.afterStatus = "PASS ✅";
        r.actualAfter = captureAfterSnapshot(context);
        records.put(k, r);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String k = key(context);
        TestRecord r = records.getOrDefault(k, new TestRecord());
        r.afterStatus = "FAIL ❌";
        r.failureMessage = cause == null ? "<no-exception>" : cause.getMessage();

        // Extract file and line number from stack trace
        if (cause != null) {
            for (StackTraceElement e : cause.getStackTrace()) {
                if (e.getFileName() != null && e.getFileName().endsWith(".java")) {
                    r.failureFileLine = e.getFileName() + ":" + e.getLineNumber();
                    
                    // Use the IntelligentSelfHealingAgent to analyze the failure
                    r.suggestion = agent.analyzeFailure(
                        r.methodName,
                        cause.toString(),
                        e.getFileName(),
                        e.getLineNumber()
                    );
                    break;
                }
            }
        }

        r.actualAfter = captureAfterSnapshot(context);
        records.put(k, r);
    }

    private String captureUISnapshot(WebDriver driver) {
        try {
            boolean loginPanel = !driver.findElements(By.cssSelector("#login-panel")).isEmpty();
            boolean loginPanelVisible = loginPanel && driver.findElement(By.cssSelector("#login-panel")).isDisplayed();
            boolean dashboardVisible = !driver.findElements(By.cssSelector("#dashboard.visible")).isEmpty();
            boolean usernamePresent = !driver.findElements(By.cssSelector("#username")).isEmpty();
            
            return String.format("loginPanel=%s, loginVisible=%s, dashboardVisible=%s, usernamePresent=%s",
                    loginPanel, loginPanelVisible, dashboardVisible, usernamePresent);
        } catch (Exception ex) {
            return "snapshot-error: " + ex.getMessage();
        }
    }

    private String captureAfterSnapshot(ExtensionContext context) {
        try {
            Object testInstance = context.getRequiredTestInstance();
            Field driverField = testInstance.getClass().getSuperclass().getDeclaredField("driver");
            driverField.setAccessible(true);
            WebDriver driver = (WebDriver) driverField.get(testInstance);

            if (driver == null) {
                return "driver=null";
            }

            return captureUISnapshot(driver);
        } catch (Exception ex) {
            return "snapshot-after-error: " + ex.getMessage();
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        String className = context.getRequiredTestClass().getName();
        Path dir = Paths.get("target", "self-healing");
        Files.createDirectories(dir);
        Path report = dir.resolve(context.getRequiredTestClass().getSimpleName() + "-report.md");

        List<TestRecord> classRecords = new ArrayList<>();
        for (TestRecord r : records.values()) {
            if (className.equals(r.className)) {
                classRecords.add(r);
            }
        }

        long passedCount = classRecords.stream().filter(r -> r.afterStatus.contains("PASS")).count();
        long failedCount = classRecords.stream().filter(r -> r.afterStatus.contains("FAIL")).count();

        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(report))) {
            out.println("# 🤖 Self-Healing Test Report");
            out.println();
            out.println("**Test Class:** " + context.getRequiredTestClass().getSimpleName());
            out.println();
            out.println("**Generated:** " + new Date());
            out.println();

            // Root cause section (if there are failures)
            if (failedCount > 0) {
                out.println("## 🔍 Root Cause");
                out.println();
                TestRecord firstFailure = classRecords.stream()
                    .filter(r -> r.afterStatus.contains("FAIL"))
                    .findFirst()
                    .orElse(null);
                
                if (firstFailure != null && firstFailure.suggestion != null && firstFailure.suggestion.rootCause != null) {
                    out.println(firstFailure.suggestion.rootCause);
                } else if (firstFailure != null) {
                    out.println("Test failure detected: " + firstFailure.failureMessage);
                }
                out.println();
            }

            // Files that would need changes
            if (failedCount > 0) {
                out.println("## 🔧 Suggested Fixes");
                out.println();
                out.println("| File | What to change | Why |");
                out.println("|------|---------------|-----|");
                
                for (TestRecord r : classRecords) {
                    if (r.afterStatus.contains("FAIL") && r.suggestion != null && r.suggestion.fixAction != null) {
                        String file = r.failureFileLine != null ? r.failureFileLine.split(":")[0] : "Test file";
                        out.printf("| %s | %s | %s |%n", file, r.suggestion.fixAction, 
                            r.suggestion.rootCause != null ? r.suggestion.rootCause : "Match UI");
                    }
                }
                out.println();
            }

            // Detailed test flow
            out.println("## 🔄 Detailed Test Flow");
            out.println();
            out.println("| # | Test Name | Expected | Actual Result | Status | Failure Details |");
            out.println("|---|-----------|----------|---------------|--------|-----------------|");

            int i = 1;
            for (TestRecord r : classRecords) {
                String expected = r.expected != null ? r.expected : "Test should pass";
                String actual = r.afterStatus.contains("PASS") ? "✓ Test passed" : "✗ " + truncate(r.failureMessage, 40);
                String failureDetails = "";
                
                if (r.afterStatus.contains("FAIL")) {
                    failureDetails = r.failureFileLine != null ? r.failureFileLine : "";
                    if (r.suggestion != null && r.suggestion.fixAction != null) {
                        failureDetails += " → " + truncate(r.suggestion.fixAction, 50);
                    }
                }
                
                out.printf("| %d | %s | %s | %s | %s | %s |%n",
                        i++, r.methodName, expected, actual, r.afterStatus, failureDetails);
            }
            out.println();

            // Summary
            out.println("## 🧪 Summary");
            out.println();
            out.println("- **Total tests:** " + classRecords.size());
            out.println("- **Passed:** " + passedCount + " ✅");
            out.println("- **Failed:** " + failedCount + " ❌");
            out.println("- **Errors:** 0");
            out.println("- **Skipped:** 0");
            out.println();
            
            if (failedCount > 0) {
                out.println("---");
                out.println();
                out.println("### 💡 Next Steps");
                out.println();
                out.println("1. Review the **Root Cause** section above");
                out.println("2. Apply the **Suggested Fixes** to test files");
                out.println("3. Re-run tests to verify fixes");
                out.println("4. The agent will NOT modify `app/index.html` - only test code is updated");
                out.println();
            }

            out.println("---");
            out.println("*Generated by Intelligent Self-Healing Agent*");
        } catch (IOException ex) {
            System.err.println("Failed to write self-healing report: " + ex);
        }
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() > maxLen ? s.substring(0, maxLen) + "..." : s;
    }
}
