@ECHO OFF
:: ---------------------------------------------------------------------------
:: Set JAVA_HOME or JRE_HOME if not already set.
::
:: ---------------------------------------------------------------------------
IF "%OS%" == "Windows_NT" setlocal
SET BASEDIR=%~dp0..
SET SCRIPTDIR=%BASEDIR%\scripts


:: ---------------------------------------------------------------------------
:: Tool runner utility
::
:: Environment Variable Prerequisites
::
::   Do not set the variables in this script. Instead put them into a script
::   setenv.bat in scripts directory to keep your customizations separate.
::
::
::   JAVA_HOME       Must point at your Java Development Kit installation.
::
::
::   JRE_HOME        Must point at your Java Runtime installation.
::                   Defaults to JAVA_HOME if empty. If JRE_HOME and JAVA_HOME
::                   are both set, JRE_HOME is used.
::

:: ---------------------------------------------------------------------------
::     JRE_HOME setup

IF NOT "%JRE_HOME%" == "" GOTO gotJRE
SET JRE_HOME=%JAVA_HOME%

if "%JRE_HOME%" == "" (
    ECHO "Error: Cannot find a JRE or JDK. Please set JRE_HOME or JAVA_HOME"
    PAUSE
    EXIT 1
)

:gotJRE


:: ---------------------------------------------------------------------------
::     JAVA_CMD setup

:: check if we got JAVA_CMD set
IF NOT "%JAVA_CMD%" == "" GOTO gotJavaCmd
:: Trying to guess java command
SET JAVA_CMD=%JRE_HOME%\bin\java

:gotJavaCmd

IF EXIST "%JAVA_CMD%.exe" (
    rem OK
) ELSE (
    ECHO "Java command is not defined. Please set JAVA_CMD environment variable pointing to java executable"
    PAUSE
    EXIT 1
)

:: ---------------------------------------------------------------------------
::     CLASS PATH

SETLOCAL EnableDelayedExpansion

REM Get standard environment variables
IF EXIST "%HOMEDRIVE%%HOMEPATH%"/.opengamma/tools.bat (
    CALL "%HOMEDRIVE%%HOMEPATH%/.opengamma/tools.bat"
)


IF "%MEM_OPTS%" == "" (
SET MEM_OPTS=-Xms512m -Xmx1024m -XX:MaxPermSize=256M
)
IF "%GC_OPTS%" == "" (
SET GC_OPTS=-XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing
)

SET CLASSPATH=%BASEDIR%\config;%BASEDIR%\lib\override\*;%BASEDIR%\lib\%PROJECTJAR%;

ECHO "%JAVA_CMD%" %MEM_OPTS% %GC_OPTS% -cp "%CLASSPATH%" %*

"%JAVA_CMD%" %MEM_OPTS% %GC_OPTS% -cp "%CLASSPATH%" %*
