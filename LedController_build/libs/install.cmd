@echo off

:: Check for administrative privileges and request elevation if needed
:: This script uses a self-relaunch mechanism for elevation
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo This script requires administrative privileges. Attempting to relaunch with elevation...
    powershell -Command "Start-Process '%~f0' -Verb runAs"
    exit /b
)

:: Define variables for paths
set "MSI_PATH=C:\LEDController\jre\openjdk-jre-21.msi"
set "JAVA_PATH=C:\Program Files\OpenLogic\jre-21.0.5.11-hotspot\bin\java.exe"
set "JAR_PATH=C:\LEDController\libs\LedController-0.0.1-SNAPSHOT.jar"
set "APP_DIR=C:\LEDController"
set "NSSM_PATH=C:\LEDController\nssm\win64\nssm.exe"
set "LOG_DIR=C:\LEDController\logs"
set "SERVICE_NAME=LedController"

:: Run the MSI installer silently
echo Installing the MSI package...
msiexec /i "%MSI_PATH%" /quiet /norestart
if %errorlevel% neq 0 (
    echo MSI installation failed. Exiting...
    exit /b
)

:: Create logs directory if it doesn't exist
if not exist "%LOG_DIR%" (
    mkdir "%LOG_DIR%"
)

:: Install the service
echo Installing the LedController service...
"%NSSM_PATH%" install %SERVICE_NAME% "%JAVA_PATH%" -jar "%JAR_PATH%"

:: Configure the service directory
"%NSSM_PATH%" set %SERVICE_NAME% AppDirectory "%APP_DIR%"

:: Configure log files
"%NSSM_PATH%" set %SERVICE_NAME% AppStdout "%LOG_DIR%\out.log"
"%NSSM_PATH%" set %SERVICE_NAME% AppStderr "%LOG_DIR%\err.log"

:: Start the service
"%NSSM_PATH%" start %SERVICE_NAME%

:: Confirm success
echo Service %SERVICE_NAME% has been installed and started successfully.
pause
