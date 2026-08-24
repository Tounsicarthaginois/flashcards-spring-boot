@echo off
REM Lance le back-end Spring Boot via le wrapper Gradle.
REM JAVA_HOME n'est pas defini au niveau systeme : on le fixe ici,
REM sinon gradlew.bat echoue avec "-classpath requires class path specification".
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot"

cd /d "%~dp0flashcard-backend"
call gradlew.bat bootRun
pause
