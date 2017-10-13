REM Simple batch file that invokes launch4j and builds an exe of the users game.
@echo off
"jre\bin\java.exe" -jar "launch4j\launch4j.jar" launch4j-config.xml
if ERRORLEVEL 1 echo Error
exit