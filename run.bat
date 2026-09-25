@echo off
setlocal EnableDelayedExpansion
REM ============================================================
REM  Mini Shop Management - one-click build and run (Windows)
REM  Usage:   run.bat          (from the project root)
REM  Needs:   JDK 17+  and  PostgreSQL running with schema loaded
REM  Optional: set JDK_BIN=C:\path\to\jdk\bin  to force a JDK
REM ============================================================

REM Always operate from the folder that holds this script
cd /d "%~dp0"

set "JAVAC="
set "JAVAEXE="

REM --- 1. Locate a JDK ------------------------------------------------
REM   a) %JDK_BIN%       explicit override, wins over everything
REM   b) javac on PATH   java.exe is taken from javac's OWN folder
REM   c) known installs
if defined JDK_BIN (
    set "JAVAC=%JDK_BIN%\javac.exe"
    set "JAVAEXE=%JDK_BIN%\java.exe"
    goto :jdk_ready
)

for /f "delims=" %%i in ('where javac 2^>nul') do (
    if not defined JAVAC set "JAVAC=%%i"
)
if defined JAVAC (
    REM Derive java.exe from javac.exe's own folder. Never search PATH for
    REM java separately: an older JDK (e.g. Java 8) earlier on PATH would be
    REM picked up and every class would fail with UnsupportedClassVersionError.
    set "JAVAEXE=!JAVAC:javac.exe=java.exe!"
    goto :jdk_ready
)

for %%p in (
    "%USERPROFILE%\.jdks\openjdk-23.0.2\bin"
    "C:\Program Files\Java\jdk-21\bin"
    "C:\Program Files\Java\jdk-17\bin"
) do (
    if not defined JAVAC if exist "%%~p\javac.exe" (
        set "JAVAC=%%~p\javac.exe"
        set "JAVAEXE=%%~p\java.exe"
    )
)

:jdk_ready
if not defined JAVAC (
    echo.
    echo [ERROR] No JDK found. Install JDK 17+ or point this script at one:
    echo         set JDK_BIN=C:\Program Files\Java\jdk-21\bin
    echo.
    pause
    exit /b 1
)
if not exist "%JAVAEXE%" (
    echo [ERROR] javac found at "%JAVAC%" but java.exe is missing next to it.
    pause
    exit /b 1
)

echo [1/4] Using:
"%JAVAC%" -version
if errorlevel 1 (
    echo [ERROR] javac could not be executed.
    pause
    exit /b 1
)

if not exist "lib\postgresql-42.7.3.jar" (
    echo [ERROR] Missing JDBC driver: lib\postgresql-42.7.3.jar
    pause
    exit /b 1
)

REM --- 2. Compile every source file -----------------------------------
echo [2/4] Compiling sources ...
if exist out rmdir /s /q out
mkdir out
REM Quote every path: the project folder may contain spaces.
set "SRCS="
for /r "src" %%f in (*.java) do set SRCS=!SRCS! "%%f"
"%JAVAC%" -encoding UTF-8 -cp "lib\postgresql-42.7.3.jar" -d out !SRCS!
set "JAVAC_ERR=%ERRORLEVEL%"
if not "%JAVAC_ERR%"=="0" (
    echo [ERROR] Compilation failed.
    pause
    exit /b 1
)

REM --- 3. Copy runtime resources next to the classes -------------------
echo [3/4] Copying resources ...
copy /y src\db.properties out\db.properties >nul
if exist src\gui\money_bg.jpg (
    if not exist out\gui mkdir out\gui
    copy /y src\gui\money_bg.jpg out\gui\money_bg.jpg >nul
)

REM --- 4. Launch --------------------------------------------------------
echo [4/4] Starting Mini Shop Management ...
"%JAVAEXE%" -Dfile.encoding=UTF-8 -cp "out;lib\postgresql-42.7.3.jar" gui.Main
