@echo off
REM ============================================================
REM  Mini Shop Management - one-click build & run (Windows)
REM  Run from project root:  run.bat
REM  Requires: JDK 17+  and  PostgreSQL running + schema loaded
REM ============================================================
setlocal EnableDelayedExpansion

REM --- 1. Locate javac (JDK_BIN env wins, else PATH, else known IntelliJ JDK) ---
if defined JDK_BIN goto :have_jdk
where javac >nul 2>nul
if %ERRORLEVEL%==0 (
  for /f \"delims=\" %%i in ('where javac') do set \"JAVAC=%%i\"
  for /f \"delims=\" %%j in ('where java') do set \"JAVAEXE=%%j\"
  goto :have_jdk2
)
if exist \"C:\\Users\\Hp\\.jdks\\openjdk-23.0.2\\bin\\javac.exe\" (
  set \"JAVAC=C:\\Users\\Hp\\.jdks\\openjdk-23.0.2\\bin\\javac.exe\"
  set \"JAVAEXE=C:\\Users\\Hp\\.jdks\\openjdk-23.0.2\\bin\\java.exe\"
  goto :have_jdk2
)
echo [ERROR] javac not found. Install JDK 17+ or set JDK_BIN env var.
echo   Example: set JDK_BIN=C:\\Program Files\\Java\\jdk-21\\bin
pause
exit /b 1

:have_jdk
set \"JAVAC=%JDK_BIN%\\javac.exe\"
set \"JAVAEXE=%JDK_BIN%\\java.exe\"

:have_jdk2
echo Using: %JAVAC%
\"%JAVAC%\" -version || ( echo [ERROR] javac failed & pause & exit /b 1 )

REM --- 2. Compile all sources ---
if not exist out mkdir out
del /Q out\\*.class 2>nul
dir /s /b src\\*.java > sources.txt
\"%JAVAC%\" -encoding UTF-8 -cp \"lib\\postgresql-42.7.3.jar\" -d out @sources.txt
if %ERRORLEVEL% neq 0 ( echo [ERROR] Compilation failed. & del sources.txt & pause & exit /b 1 )
del sources.txt

REM --- 3. Copy resources next to classes ---
copy /Y src\\db.properties out\\db.properties >nul
if exist src\\gui\\money_bg.jpg (
  if not exist out\\gui mkdir out\\gui
  copy /Y src\\gui\\money_bg.jpg out\\gui\\money_bg.jpg >nul
)

REM --- 4. Launch ---
echo Starting Mini Shop Management ...
\"%JAVAEXE%\" -cp \"out;lib\\postgresql-42.7.3.jar\" gui.Main
endlocal

