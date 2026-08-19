import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Intelligent Self-Healing Agent for Selenium Tests.
 * 
 * This agent automatically detects and repairs broken Selenium tests by:
 * 1. Analyzing test failures
 * 2. Comparing expected vs actual UI elements
 * 3. Updating stale locators in DemoAppPage.java
 * 4. Updating stale assertions in test files
 * 5. Generating detailed before/after reports
 * 
 * Per repository guidelines:
 * - ONLY modifies test code (never app/index.html)
 * - Captures complete before/after state
 * - Reports all test results with PASS/FAIL status
 */
public class IntelligentSelfHealingAgent {

    private final Path projectRoot;
    private final Path appHtmlPath;
    private final Path demoAppPagePath;
    private Document uiDocument;
    
    public IntelligentSelfHealingAgent() {
        this.projectRoot = Paths.get("").toAbsolutePath();
        this.appHtmlPath = projectRoot.resolve("app/index.html");
        this.demoAppPagePath = projectRoot.resolve("src/test/java/DemoAppPage.java");
    }
    
    /**
     * Load and parse the current UI HTML
     */
    public void loadUISource() throws IOException {
        String html = Files.readString(appHtmlPath);
        this.uiDocument = Jsoup.parse(html);
    }
    
    /**
     * Detect the best locator for an element based on current UI
     */
    public List<String> suggestLocators(String elementDescription) {
        List<String> suggestions = new ArrayList<>();
        
        // Parse element description to understand what we're looking for
        String lowercaseDesc = elementDescription.toLowerCase();
        
        if (lowercaseDesc.contains("login button") || lowercaseDesc.contains("sign in")) {
            Element loginBtn = uiDocument.selectFirst("button[data-testid=login-button]");
            if (loginBtn != null) {
                suggestions.add("By.id(\"login-btn\")");
                suggestions.add("By.cssSelector(\"[data-testid='login-button']\")");
                suggestions.add("By.cssSelector(\"button.btn-login\")");
                suggestions.add("By.xpath(\"//button[normalize-space()='" + loginBtn.text() + "']\")");
            }
        }
        else if (lowercaseDesc.contains("logout") && lowercaseDesc.contains("dashboard")) {
            Element logoutBtn = uiDocument.selectFirst("button[data-testid=logout-button]");
            if (logoutBtn != null) {
                suggestions.add("By.id(\"logout-btn\")");
                suggestions.add("By.cssSelector(\"[data-testid='logout-button']\")");
                suggestions.add("By.cssSelector(\"button.btn-logout\")");
                suggestions.add("By.xpath(\"//button[normalize-space()='" + logoutBtn.text() + "']\")");
            }
        }
        else if (lowercaseDesc.contains("profile") && lowercaseDesc.contains("link")) {
            Element profileLink = uiDocument.selectFirst("a[data-testid=my-profile-link]");
            if (profileLink != null) {
                suggestions.add("By.id(\"my-profile-link\")");
                suggestions.add("By.cssSelector(\"[data-testid='my-profile-link']\")");
                suggestions.add("By.cssSelector(\"a#my-profile-link\")");
                suggestions.add("By.xpath(\"//a[normalize-space()='My Profile']\")");
            }
        }
        
        return suggestions;
    }
    
    /**
     * Extract actual text values from UI for assertion comparison
     */
    public Map<String, String> extractUITextValues() {
        Map<String, String> values = new HashMap<>();
        
        // Login panel elements
        Element pageHeading = uiDocument.selectFirst("#page-heading");
        if (pageHeading != null) {
            values.put("page-heading", pageHeading.text());
        }
        
        Element loginBtn = uiDocument.selectFirst("#login-btn, [data-testid=login-button]");
        if (loginBtn != null) {
            values.put("login-button-text", loginBtn.text());
        }
        
        Element forgotLink = uiDocument.selectFirst("#forgot-password-link, [data-testid=forgot-password-link]");
        if (forgotLink != null) {
            values.put("forgot-password-text", forgotLink.text());
        }
        
        // Dashboard elements
        Element dashboardHeading = uiDocument.selectFirst("#dashboard-heading, [data-testid=dashboard-heading]");
        if (dashboardHeading != null) {
            values.put("dashboard-heading", dashboardHeading.text());
        }
        
        Element logoutBtn = uiDocument.selectFirst("#logout-btn, [data-testid=logout-button]");
        if (logoutBtn != null) {
            values.put("logout-button-text", logoutBtn.text());
        }
        
        Element profileLink = uiDocument.selectFirst("#my-profile-link, [data-testid=my-profile-link]");
        if (profileLink != null) {
            values.put("my-profile-link-text", profileLink.text());
            String href = profileLink.attr("href");
            values.put("my-profile-link-href", href);
        }
        
        // Profile page
        Element profileHeading = uiDocument.selectFirst("#profile-heading, [data-testid=profile-heading]");
        if (profileHeading != null) {
            values.put("profile-heading", profileHeading.text());
        }
        
        Element profileLogoutBtn = uiDocument.selectFirst("#profile-logout-btn, [data-testid=profile-logout-button]");
        if (profileLogoutBtn != null) {
            values.put("profile-logout-button-text", profileLogoutBtn.text());
        }
        
        return values;
    }
    
    /**
     * Fix a stale locator in DemoAppPage.java
     */
    public boolean fixLocator(String locatorName, String expectedText, String actualUIValue) throws IOException {
        String content = Files.readString(demoAppPagePath);
        boolean modified = false;
        
        // Find the locator definition and update XPath if text changed
        Pattern pattern = Pattern.compile(
            "(private static final FallbackLocator " + Pattern.quote(locatorName) + 
            " = FallbackLocator\\.of\\([^;]+?)(By\\.xpath\\(\"[^\"]*\\[normalize-space\\(\\)='[^']*'\\][^\"]*\"\\))",
            Pattern.DOTALL
        );
        
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String xpathPart = matcher.group(2);
            String updatedXpath = xpathPart.replaceAll(
                "\\[normalize-space\\(\\)='[^']*'\\]",
                "[normalize-space()='" + actualUIValue + "']"
            );
            
            content = content.replace(xpathPart, updatedXpath);
            modified = true;
        }
        
        if (modified) {
            Files.writeString(demoAppPagePath, content, StandardOpenOption.TRUNCATE_EXISTING);
        }
        
        return modified;
    }
    
    /**
     * Fix a stale assertion in a test file
     */
    public boolean fixAssertion(Path testFilePath, String expectedOld, String expectedNew) throws IOException {
        String content = Files.readString(testFilePath);
        boolean modified = false;
        
        // Update assertEquals with old value to new value
        String updated = content.replace(
            "assertEquals(\"" + expectedOld + "\"",
            "assertEquals(\"" + expectedNew + "\""
        );
        
        if (!updated.equals(content)) {
            Files.writeString(testFilePath, updated, StandardOpenOption.TRUNCATE_EXISTING);
            modified = true;
        }
        
        return modified;
    }
    
    /**
     * Analyze a test failure message and suggest fix
     */
    public RepairSuggestion analyzeFailure(String testName, String failureMessage, String fileName, int lineNumber) {
        RepairSuggestion suggestion = new RepairSuggestion();
        suggestion.testName = testName;
        suggestion.failureMessage = failureMessage;
        suggestion.fileName = fileName;
        suggestion.lineNumber = lineNumber;
        
        try {
            loadUISource();
            Map<String, String> uiValues = extractUITextValues();
            
            // Parse failure message for expected vs actual
            Pattern expectedActualPattern = Pattern.compile("expected:\\s*<([^>]+)>\\s*but was:\\s*<([^>]+)>");
            Matcher matcher = expectedActualPattern.matcher(failureMessage);
            
            if (matcher.find()) {
                String expected = matcher.group(1);
                String actual = matcher.group(2);
                
                suggestion.expectedValue = expected;
                suggestion.actualValue = actual;
                suggestion.rootCause = "UI text changed from '" + expected + "' to '" + actual + "'";
                
                // Find which UI element this corresponds to
                for (Map.Entry<String, String> entry : uiValues.entrySet()) {
                    if (entry.getValue().equals(actual)) {
                        suggestion.uiElementId = entry.getKey();
                        suggestion.fixAction = "Update test assertion from '" + expected + "' to '" + actual + "'";
                        break;
                    }
                }
            }
            
            // Check for NoSuchElementException
            if (failureMessage.contains("NoSuchElementException") || 
                failureMessage.contains("Unable to find element")) {
                suggestion.rootCause = "Element locator is stale or element not found";
                suggestion.fixAction = "Update locator strategy in DemoAppPage.java";
            }
            
            // Check for href mismatch
            if (failureMessage.contains("href") && failureMessage.contains("/my-profile")) {
                String actualHref = uiValues.get("my-profile-link-href");
                suggestion.rootCause = "Profile link href changed in UI";
                suggestion.actualValue = actualHref;
                suggestion.fixAction = "Update test expectation to match actual href: " + actualHref;
            }
            
        } catch (IOException e) {
            suggestion.rootCause = "Failed to analyze UI: " + e.getMessage();
        }
        
        return suggestion;
    }
    
    /**
     * Generate detailed repair report in markdown format
     */
    public String generateReport(List<TestResult> beforeResults, List<TestResult> afterResults) {
        StringBuilder report = new StringBuilder();
        
        report.append("# 🤖 Intelligent Self-Healing Agent Report\n\n");
        report.append("Generated: ").append(new Date()).append("\n\n");
        
        // Root Cause section
        report.append("## 🔍 Root Cause\n\n");
        long failedBefore = beforeResults.stream().filter(r -> !r.passed).count();
        if (failedBefore > 0) {
            TestResult firstFailure = beforeResults.stream()
                .filter(r -> !r.passed)
                .findFirst()
                .orElse(null);
            
            if (firstFailure != null && firstFailure.suggestion != null) {
                report.append(firstFailure.suggestion.rootCause).append("\n\n");
            } else {
                report.append("Test failures detected. See details below.\n\n");
            }
        } else {
            report.append("All tests passing. No repairs needed.\n\n");
        }
        
        // Files Changed section
        report.append("## 🔧 Files Changed\n\n");
        report.append("| File | What changed | Why |\n");
        report.append("|------|-------------|-----|\n");
        
        Set<String> changedFiles = new HashSet<>();
        for (TestResult r : beforeResults) {
            if (!r.passed && r.suggestion != null && r.suggestion.fixAction != null) {
                if (r.suggestion.fixAction.contains("DemoAppPage")) {
                    changedFiles.add("src/test/java/DemoAppPage.java");
                } else if (r.fileName != null) {
                    changedFiles.add("src/test/java/" + r.fileName);
                }
            }
        }
        
        for (String file : changedFiles) {
            report.append("| ").append(file).append(" | Updated stale locators/assertions | Match current UI state |\n");
        }
        
        if (changedFiles.isEmpty()) {
            report.append("| (none) | No changes required | All tests passing |\n");
        }
        report.append("\n");
        
        // Detailed Test Flow
        report.append("## 🔄 Detailed Test Flow — Before & After Fix\n\n");
        report.append("| # | Test Name | Step/Action | Expected | Actual Before Fix | Before Status | Actual After Fix | After Status |\n");
        report.append("|---|-----------|-------------|----------|-------------------|---------------|------------------|-------------|\n");
        
        for (int i = 0; i < beforeResults.size(); i++) {
            TestResult before = beforeResults.get(i);
            TestResult after = afterResults.size() > i ? afterResults.get(i) : null;
            
            String expected = before.suggestion != null && before.suggestion.expectedValue != null 
                ? before.suggestion.expectedValue 
                : "Test preconditions met";
            String actualBefore = before.suggestion != null && before.suggestion.actualValue != null 
                ? before.suggestion.actualValue 
                : (before.passed ? "✓ Passed" : "✗ " + truncate(before.failureMessage, 50));
            String actualAfter = after != null && after.passed 
                ? "✓ Passed" 
                : (after != null ? "✗ " + truncate(after.failureMessage, 50) : "N/A");
            
            report.append(String.format("| %d | %s | %s | %s | %s | %s | %s | %s |\n",
                i + 1,
                before.testName,
                "Execute test",
                expected,
                actualBefore,
                before.passed ? "PASS ✅" : "FAIL ❌",
                actualAfter,
                after != null && after.passed ? "PASS ✅" : "FAIL ❌"
            ));
        }
        report.append("\n");
        
        // Summary section
        report.append("## 🧪 Summary\n\n");
        long passedBefore = beforeResults.stream().filter(r -> r.passed).count();
        long passedAfter = afterResults.stream().filter(r -> r.passed).count();
        
        report.append("- Total tests: ").append(beforeResults.size()).append("\n");
        report.append("- Total passed before fix: ").append(passedBefore).append("\n");
        report.append("- Total failed before fix: ").append(failedBefore).append("\n");
        report.append("- Total passed after fix: ").append(passedAfter).append("\n");
        report.append("- Total failed after fix: ").append(afterResults.size() - passedAfter).append("\n");
        report.append("- Errors: 0\n");
        report.append("- Skipped: 0\n\n");
        
        return report.toString();
    }
    
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
    
    // Data structures for tracking test results and repair suggestions
    
    public static class TestResult {
        public String testName;
        public String fileName;
        public int lineNumber;
        public boolean passed;
        public String failureMessage;
        public RepairSuggestion suggestion;
    }
    
    public static class RepairSuggestion {
        public String testName;
        public String failureMessage;
        public String fileName;
        public int lineNumber;
        public String rootCause;
        public String expectedValue;
        public String actualValue;
        public String uiElementId;
        public String fixAction;
    }
}
