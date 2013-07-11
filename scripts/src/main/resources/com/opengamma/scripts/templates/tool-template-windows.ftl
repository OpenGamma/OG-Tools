@ECHO OFF

CALL project-utils.bat
CALL "%~dp0\run-tool.bat" ${className} %* -l com/opengamma/util/warn-logback.xml
