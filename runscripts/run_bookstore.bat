@echo off
cd ../target

set JAR_FILE=bookstore-Ver.1.jar

if not exist %JAR_FILE% (
    echo The JAR file "%JAR_FILE%" does not exist!
    echo Please run mvn-package.bat to compile
    exit /b 1
)

echo Running the Spring Boot application...
java -jar %JAR_FILE%

if %ERRORLEVEL% neq 0 (
    echo Application failed to start!
    exit /b %ERRORLEVEL%
)

echo Application started successfully!
pause