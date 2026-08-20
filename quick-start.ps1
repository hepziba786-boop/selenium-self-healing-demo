# Quick Start Script for Selenium Self-Healing Demo
# This script helps you run tests and view reports

Write-Host "🤖 Selenium Self-Healing Demo - Quick Start" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

# Check if Maven is installed
$mavenInstalled = $null -ne (Get-Command mvn -ErrorAction SilentlyContinue)

if ($mavenInstalled) {
    Write-Host "✅ Maven detected" -ForegroundColor Green
    Write-Host ""
    
    Write-Host "📦 Installing dependencies..." -ForegroundColor Yellow
    mvn clean compile -DskipTests
    
    Write-Host ""
    Write-Host "🧪 Running tests..." -ForegroundColor Yellow
    mvn test
    
    Write-Host ""
    Write-Host "📊 Checking for reports..." -ForegroundColor Yellow
    
    $reportDir = Join-Path $projectRoot "target\self-healing"
    if (Test-Path $reportDir) {
        $reports = Get-ChildItem -Path $reportDir -Filter "*.md"
        
        if ($reports.Count -gt 0) {
            Write-Host "✅ Reports generated:" -ForegroundColor Green
            foreach ($report in $reports) {
                Write-Host "   - $($report.FullName)" -ForegroundColor White
            }
            
            Write-Host ""
            Write-Host "📖 Opening first report..." -ForegroundColor Yellow
            Start-Process notepad.exe $reports[0].FullName
        } else {
            Write-Host "⚠️  No reports found. Tests may have all passed or not run." -ForegroundColor Yellow
        }
    } else {
        Write-Host "⚠️  Report directory not found. Tests may not have run." -ForegroundColor Yellow
    }
} else {
    Write-Host "❌ Maven not found in PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "Options:" -ForegroundColor Yellow
    Write-Host "1. Use Eclipse IDE (recommended):" -ForegroundColor White
    Write-Host "   - Import project: File → Import → Maven → Existing Maven Projects" -ForegroundColor Gray
    Write-Host "   - Run tests: Right-click src/test/java → Run As → JUnit Test" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. Install Maven:" -ForegroundColor White
    Write-Host "   Download from: https://maven.apache.org/download.cgi" -ForegroundColor Gray
    Write-Host "   Or use Chocolatey: choco install maven" -ForegroundColor Gray
    Write-Host ""
    Write-Host "3. Read the setup guide:" -ForegroundColor White
    Write-Host "   See: ECLIPSE_SETUP.md" -ForegroundColor Gray
}

Write-Host ""
Write-Host "📚 Documentation:" -ForegroundColor Cyan
Write-Host "   - README.md - Project overview" -ForegroundColor Gray
Write-Host "   - ECLIPSE_SETUP.md - Complete Eclipse setup guide" -ForegroundColor Gray
Write-Host "   - RUNBOOK.md - CI/CD integration" -ForegroundColor Gray
Write-Host ""

Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
