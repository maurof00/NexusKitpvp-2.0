@echo off
setlocal
where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven non trovato. In IntelliJ importa il progetto come Maven oppure installa Maven.
  exit /b 1
)
echo Building XeonKitPvP...
mvn clean package
if errorlevel 1 exit /b 1
echo.
echo JAR creato: target\XeonKitPvP-2.0.0.jar
echo.
endlocal
