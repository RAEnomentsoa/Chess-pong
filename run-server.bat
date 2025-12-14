@echo off
title Chess-Pong Server (EJB + GUI)

REM ==== CONFIG ====
set WILDFLY_HOME=C:\wildfly-38.0.1.Final
set CP=out;EJBout;libs\jakarta.jakartaee-api-10.0.0.jar;%WILDFLY_HOME%\bin\client\jboss-client.jar

REM ==== RUN SERVER ====
echo Starting Chess-Pong Server...
echo.

java -cp "%CP%" net.server.ServerLauncher

pause
