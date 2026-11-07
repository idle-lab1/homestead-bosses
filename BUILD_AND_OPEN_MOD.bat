@echo off
cd /d "%~dp0"

echo ================================================================
echo   Looking for a Java 17 install on this PC...
echo ================================================================

set "JDKPATH="
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do set "JDKPATH=%%D"
if "%JDKPATH%"=="" for /d %%D in ("C:\Program Files\Microsoft\jdk-17*") do set "JDKPATH=%%D"
if "%JDKPATH%"=="" for /d %%D in ("C:\Program Files\Java\jdk-17*") do set "JDKPATH=%%D"
if "%JDKPATH%"=="" for /d %%D in ("C:\Program Files\Zulu\zulu-17*") do set "JDKPATH=%%D"
if "%JDKPATH%"=="" for /d %%D in ("C:\Program Files\Amazon Corretto\jdk17*") do set "JDKPATH=%%D"

if "%JDKPATH%"=="" (
    echo.
    echo   Didn't find Java 17 in any of the usual install locations.
    echo.
    echo   Install it here: https://adoptium.net/temurin/releases/?version=17
    echo   Grab the Windows x64 .msi, run it, click through with the
    echo   default options checked, then double-click this file again.
    echo.
    pause
    exit /b 1
)

echo   Found Java 17 at: %JDKPATH%

rem Pin Gradle straight to this Java install so it never needs to phone
rem home to fetch one itself - fixes "Requesting vendor list failed" errors.
set "JDKPATH_FWD=%JDKPATH:\=/%"
findstr /C:"org.gradle.java.home" gradle.properties >nul 2>&1
if errorlevel 1 (
    echo org.gradle.java.home=%JDKPATH_FWD%>> gradle.properties
    echo   Pinned Gradle to use it.
)

echo.
echo ================================================================
echo   Building Homestead Bosses...
echo.
echo   First time only: this downloads Minecraft + Forge and can
echo   take 15-30+ minutes depending on your internet. The window
echo   may look "stuck" - that's normal, just let it run.
echo ================================================================
echo.
call gradlew.bat build
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ================================================================
    echo   Something went wrong. Scroll up to see the error message.
    echo ================================================================
    pause
    exit /b 1
)
echo.
echo ================================================================
echo   Done! Opening the folder with your mod jar now.
echo   Drag the .jar file into your .minecraft\mods folder.
echo ================================================================
explorer "%~dp0build\libs"
pause
