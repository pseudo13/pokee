$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$backend = Join-Path $root "backend"
$frontend = Join-Path $root "frontend"

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "           Pokee" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# 1. Check prerequisites
Write-Host "[1/7] Checking prerequisites..." -ForegroundColor Yellow

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "Java is not installed or not available in PATH." -ForegroundColor Red
    exit 1
}

if (-not (Get-Command node -ErrorAction SilentlyContinue)) {
    Write-Host "Node.js is not installed or not available in PATH." -ForegroundColor Red
    exit 1
}

if (-not (Get-Command npm -ErrorAction SilentlyContinue)) {
    Write-Host "npm is not installed or not available in PATH." -ForegroundColor Red
    exit 1
}

Write-Host "Java, Node.js and npm found." -ForegroundColor Green


# 2. Install frontend dependencies if necessary
Write-Host ""
Write-Host "[2/7] Checking frontend dependencies..." -ForegroundColor Yellow

$nodeModules = Join-Path $frontend "node_modules"

if (-not (Test-Path $nodeModules)) {
    Write-Host "node_modules not found. Running npm install..." -ForegroundColor Yellow

    Push-Location $frontend

    try {
        npm install

        if ($LASTEXITCODE -ne 0) {
            throw "npm install failed."
        }
    }
    finally {
        Pop-Location
    }

    Write-Host "Frontend dependencies installed." -ForegroundColor Green
}
else {
    Write-Host "Frontend dependencies already installed." -ForegroundColor Green
}


# 3. Start backend (hidden process, output redirected to temp files)
Write-Host ""
Write-Host "[3/7] Starting Spring Boot backend..." -ForegroundColor Yellow

$backendLog = Join-Path $root "backend.log"
$backendProcess = Start-Process powershell `
    -ArgumentList "-Command", "Set-Location '$backend'; .\mvnw.cmd spring-boot:run 2>&1 | Tee-Object '$backendLog'" `
    -WorkingDirectory $backend `
    -WindowStyle Hidden `
    -PassThru

Write-Host "Backend starting on http://localhost:8088 (PID: $($backendProcess.Id))" -ForegroundColor Green


# 4. Start frontend
Write-Host ""
Write-Host "[4/7] Starting Angular frontend..." -ForegroundColor Yellow

$frontendLog = Join-Path $root "frontend.log"
$frontendProcess = Start-Process powershell `
    -ArgumentList "-Command", "Set-Location '$frontend'; npm start 2>&1 | Tee-Object '$frontendLog'" `
    -WorkingDirectory $frontend `
    -WindowStyle Hidden `
    -PassThru

Write-Host "Frontend starting on http://localhost:4200 (PID: $($frontendProcess.Id))" -ForegroundColor Green


# 5. Wait for services to be ready
Write-Host ""
Write-Host "[5/7] Waiting for services to be ready..." -ForegroundColor Yellow

$maxWait = 90
$elapsed = 0
while ($elapsed -lt $maxWait) {
    try {
        $conn = Get-NetTCPConnection -LocalPort 8088 -State Listen -ErrorAction SilentlyContinue
        if ($conn) {
            Write-Host "Backend is ready." -ForegroundColor Green
            break
        }
    } catch {}

    Start-Sleep -Milliseconds 500
    $elapsed++
}

if ($elapsed -ge $maxWait) {
    Write-Host "Timeout waiting for backend. Backend process still running: $($backendProcess.Id)" -ForegroundColor Yellow
}


# 6. Open browser
Write-Host ""
Write-Host "[6/7] Opening application..." -ForegroundColor Yellow

Start-Sleep -Seconds 2
Start-Process "http://localhost:4200"

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "Pokee is starting!" -ForegroundColor Green
Write-Host ""
Write-Host "Frontend: http://localhost:4200"
Write-Host "Backend:  http://localhost:8088"
Write-Host ""
Write-Host "Press Enter to stop all services and exit."
Read-Host
Write-Host "Stopping services..."

if ($backendProcess -and -not $backendProcess.HasExited) { Stop-Process -Id $backendProcess.Id -Force }
if ($frontendProcess -and -not $frontendProcess.HasExited) { Stop-Process -Id $frontendProcess.Id -Force }

Write-Host "================================" -ForegroundColor Cyan