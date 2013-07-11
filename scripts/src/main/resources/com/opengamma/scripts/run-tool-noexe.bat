@ECHO OFF
:: ---------------------------------------------------------------------------
:: Set JAVA_HOME or JRE_HOME if not already set.
::
:: ---------------------------------------------------------------------------
if "%OS%" == "Windows_NT" setlocal
set BASEDIR=%~dp0..
set SCRIPTDIR=%BASEDIR%\scripts


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

if not "%JRE_HOME%" == "" goto gotJRE
set JRE_HOME=%JAVA_HOME%

if "%JRE_HOME%" == "" (
    echo "Error: Cannot find a JRE or JDK. Please set JRE_HOME or JAVA_HOME"
    pause
    exit 1
)

:gotJRE


:: ---------------------------------------------------------------------------
::     JAVA_CMD setup

:: check if we got JAVA_CMD set
if not "%JAVA_CMD%" == "" goto gotJavaCmd
:: Trying to guess java command
set JAVA_CMD=%JRE_HOME%\bin\java

:gotJavaCmd

if exist "%JAVA_CMD%.exe" (
    rem OK
) else (
    echo "Java command is not defined. Please set JAVA_CMD environment variable pointing to java executable"
    pause
    exit 1
)

:: ---------------------------------------------------------------------------
::     CLASS PATH

setLocal EnableDelayedExpansion

rem Get standard environment variables
if exist "%HOMEDRIVE%%HOMEPATH%"/.opengamma/tools.bat (
    CALL "%HOMEDRIVE%%HOMEPATH%/.opengamma/tools.bat"
)


if "%MEM_OPTS%" == "" (
SET MEM_OPTS=-Xms512m -Xmx1024m -XX:MaxPermSize=256M
)
if "%GC_OPTS%" == "" (
SET GC_OPTS=-XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing
)

SET CLASSPATH=%BASEDIR%\config;%BASEDIR%\lib\override\*;%BASEDIR%\lib\%PROJECTJAR%;

ECHO "%JAVA_CMD%" %MEM_OPTS% %GC_OPTS% -cp "%CLASSPATH%" %*

"%JAVA_CMD%" %MEM_OPTS% %GC_OPTS% -cp "%CLASSPATH%" %*

