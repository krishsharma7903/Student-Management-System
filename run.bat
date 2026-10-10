@echo off
TITLE Student Management System - Web Application
echo =======================================================================
echo    STUDENT MANAGEMENT SYSTEM - APACHE TOMCAT 10.1 WEB SERVER
echo =======================================================================
echo.

set MAVEN_BIN="C:\Users\krish\Downloads\Student Management System\apache-maven-3.9.9\bin\mvn.cmd"
if not exist %MAVEN_BIN% (
    set MAVEN_BIN=mvn
)

echo Compiling and starting Embedded Tomcat Server on http://localhost:8080/StudentManagementSystem
%MAVEN_BIN% compile exec:java "-Dexec.mainClass=com.sms.util.EmbeddedTomcatServer"
pause
