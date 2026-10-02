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
Write-Host "[1/9] Checking prerequisites..." -ForegroundColor Yellow

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


# 2. Ensure JAVA_HOME is set (mvnw.cmd needs it)
Write-Host ""
Write-Host "[2/9] Ensuring JAVA_HOME ... " -ForegroundColor Yellow

if (-not $env:JAVA_HOME) {
    # Try registry fallback on Windows
    $javaKey = Get-ChildItem -Path "HKLM:\SOFTWARE\JavaSoft\JRE Runtime Environments" -ErrorAction SilentlyContinue | Sort-Object PSChildName -Descending | Select-Object -First 1
    if ($javaKey) {
        $homedir = (Get-ItemPropertyValue -Path $javaKey.PSPath -Name "JavaHome" -ErrorAction SilentlyContinue)
        if ($homedir -and (Test-Path (Join-Path $homedir "bin\java.exe"))) {
            $env:JAVA_HOME = $homedir
        }
    }

    # Second try: JDK runtime key
    if (-not $env:JAVA_HOME) {
        $jdkKey = Get-ChildItem -Path "HKLM:\SOFTWARE\JavaSoft\JDK" -ErrorAction SilentlyContinue | Sort-Object PSChildName -Descending | Select-Object -First 1
        if ($jdkKey) {
            $homedir = (Get-ItemPropertyValue -Path $jdkKey.PSPath -Name "JavaHome" -ErrorAction SilentlyContinue)
            if ($homedir -and (Test-Path (Join-Path $homedir "bin\java.exe"))) {
                $env:JAVA_HOME = $homedir
            }
        }
    }

    # Final try: find java in PATH and derive JAVA_HOME from it
    if (-not $env:JAVA_HOME) {
        $javaExe = Get-Command java -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source
        if ($javaExe) {
            $inferredHome = Split-Path (Split-Path $javaExe -Parent) -Parent
            if (Test-Path (Join-Path $inferredHome "lib\tools.jar")) {
                $env:JAVA_HOME = $inferredHome
            } elseif ((Get-Item $inferredHome).Name -match "jdk" -or (Get-Item $inferredHome).Name -match "java") {
                $env:JAVA_HOME = $inferredHome
            } else {
                # Assume top-level Java dir — covers some installers
                $env:JAVA_HOME = Split-Path $inferredHome -Parent
            }
        }
    }

    if (-not $env:JAVA_HOME) {
        Write-Host "`n" -NoNewline
        Write-Host "JAVA_HOME is not set in your environment." -ForegroundColor Red
        Write-Host "mvnw.cmd needs it to locate the JDK. Either:" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "  1. Set it permanently: system properties → Environment Variables → add JAVA_HOME" -ForegroundColor White
        Write-Host "     Point it at your JDK folder (e.g. C:\Program Files\Java\jdk-21)" -ForegroundColor DarkGray
        Write-Host "  2. Set it for this session:   `$env:JAVA_HOME='C:\path\to\jdk'`n" -ForegroundColor DarkGray
        exit 1
    } else {
        Write-Host " (auto-detected from registry / PATH → $($env:JAVA_HOME))" -ForegroundColor DarkGray
    }
}

Write-Host "done." -ForegroundColor Green


# 3. Check backend and frontend directories exist
Write-Host ""
Write-Host "[3/9] Checking for backend/frontend directories..." -ForegroundColor Yellow

foreach ($dirName in @("backend", "frontend")) {
    $dir = Join-Path $root $dirName
    if (Test-Path $dir) {
        Write-Host "$dirName found." -ForegroundColor Green
    } else {
        Write-Host "`nERROR: $dirName directory not found in $root" -ForegroundColor Red
        Write-Host "Run 'git pull' to fetch the latest code, or clone this repo." -ForegroundColor Yellow
        exit 1
    }
}


# 4. Install frontend dependencies if necessary
Write-Host ""
Write-Host "[4/9] Checking frontend dependencies..." -ForegroundColor Yellow

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


# 5. Ensure Maven wrapper is bootstrapped (required before starting backend)
Write-Host ""
Write-Host "[5/9] Ensuring Maven wrapper ... " -ForegroundColor Yellow

$wrapperPropsPath = Join-Path $backend ".mvn\wrapper\maven-wrapper.properties"
$wrapperJarPath   = Join-Path $backend ".mvn\wrapper\maven-wrapper.jar"

if (-not (Test-Path $wrapperJarPath)) {
    # Create the .mvn/wrapper directory structure if missing
    $wrapperDir = Split-Path $wrapperPropsPath -Parent
    if (-not (Test-Path $wrapperDir)) {
        New-Item -ItemType Directory -Path $wrapperDir -Force | Out-Null
    }

    # Write maven-wrapper.properties (default Maven 3.9.9 wrapper)
    if (-not (Test-Path $wrapperPropsPath)) {
        @"
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar
wrapperChecksum=04a789185a3001959bc42e0e7b0c4f7c
"@ | Set-Content -Path $wrapperPropsPath -Encoding UTF8
    }

    # Download the wrapper JAR using PowerShell (TLS 1.2 is default in PS 5+)
    try {
        Write-Host "bootstrapping ..." -ForegroundColor DarkGray
        $ProgressPreference = 'SilentlyContinue'   # suppress PS download progress bar
        Invoke-WebRequest -Uri "https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar" `
            -OutFile $wrapperJarPath -UseBasicParsing | Out-Null
        $ProgressPreference = 'Continue'
        Write-Host "done." -ForegroundColor Green
    } catch {
        Write-Host "`nFailed to download Maven wrapper JAR. Install Maven manually or check your internet connection." -ForegroundColor Red
        if ($backendProcess -and -not $backendProcess.HasExited)   { Stop-Process -Id $backendProcess.Id -Force }
        if ($frontendProcess -and -not $frontendProcess.HasExited) { Stop-Process -Id $frontendProcess.Id -Force }
        exit 1
    }
} else {
    Write-Host "OK (wrapper JAR exists)." -ForegroundColor Green
}


# 6. Start backend (hidden process, output redirected to temp files)
Write-Host ""
Write-Host "[6/9] Starting Spring Boot backend..." -ForegroundColor Yellow

if (-not (Test-Path (Join-Path $backend "pom.xml"))) {
    Write-Host "ERROR: pom.xml not found in $backend" -ForegroundColor Red
    if ($backendProcess -and -not $backendProcess.HasExited) { Stop-Process -Id $backendProcess.Id -Force }
    if ($frontendProcess -and -not $frontendProcess.HasExited)   { Stop-Process -Id $frontendProcess.Id   -Force }
    exit 1
}

$backendLog = Join-Path $root "backend.log"
Write-Host "(Logs written to $backendLog)`n" -ForegroundColor DarkGray

# Propagate JAVA_HOME into the child process (mvnw.cmd needs it)
$JAVA_HOME_escaped = ""
if ($env:JAVA_HOME) {
    $path = $env:JAVA_HOME.Replace('\','\\')   # escape backslashes for cmd line
    if ($path -match "^[^' ]*$") {
        $JAVA_HOME_escaped = "'`$env:JAVA_HOME=$path'"
    } else {
        $JAVA_HOME_escaped = """`$env:JAVA_HOME=`"$path`""""
    }
}
$backendProcess = Start-Process powershell `
    -ArgumentList "-Command", "Set-Location '$backend'; $JAVA_HOME_escaped; .\mvnw.cmd spring-boot:run 2>&1 | Tee-Object '$backendLog'" `
    -WorkingDirectory $backend `
    -WindowStyle Hidden `
    -PassThru

Write-Host "Backend starting on http://localhost:8088 (PID: $($backendProcess.Id))" -ForegroundColor Green


# 7. Start frontend
Write-Host ""
Write-Host "[7/9] Starting Angular frontend..." -ForegroundColor Yellow

$frontendLog = Join-Path $root "frontend.log"
$frontendProcess = Start-Process powershell `
    -ArgumentList "-Command", "Set-Location '$frontend'; npm start 2>&1 | Tee-Object '$frontendLog'" `
    -WorkingDirectory $frontend `
    -WindowStyle Hidden `
    -PassThru

Write-Host "Frontend starting on http://localhost:4200 (PID: $($frontendProcess.Id))" -ForegroundColor Green


# 8. Wait for services to be ready
Write-Host ""
Write-Host "[8/9] Waiting for services to be ready..." -ForegroundColor Yellow

$maxWait = 150
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
    Write-Host "`nTimeout waiting for backend to start (took $(([math]::Round($elapsed / 2 / 60)))-min wait)." -ForegroundColor Red

    # Check if the process is still alive — if not, it likely failed on startup
    if ($backendProcess -and $backendProcess.HasExited) {
        Write-Host "  Backend process exited with code $($backendProcess.ExitCode).`n" -ForegroundColor Yellow
        if (Test-Path $backendLog) {
            Write-Host "Last lines of backend.log:" -ForegroundColor DarkGray
            Get-Content $backendLog | Select-Object -Last 20 | ForEach-Object { "    $_" }
        }
    } else {
        Write-Host "  Backend is still running but not responding on port 8088." -ForegroundColor Yellow
        Write-Host "  Check $backendLog for errors.`n" -ForegroundColor DarkGray
    }

    if ($frontendProcess -and -not $frontendProcess.HasExited)   { Stop-Process -Id $frontendProcess.Id   -Force }
    exit 1
}


# 9. Open browser
Write-Host ""
Write-Host "[9/9] Opening application..." -ForegroundColor Yellow

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