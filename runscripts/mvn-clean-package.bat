@echo off
cd ..
echo Packaging the Maven project...
mvnw.cmd clean package
if %ERRORLEVEL% neq 0 (
    echo Maven package failed!
    exit /b %ERRORLEVEL%
)
echo Maven package successful!
pause