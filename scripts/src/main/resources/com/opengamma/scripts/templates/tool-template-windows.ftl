@ECHO OFF

set PROJECT=${projectName}
set PROJECTJAR=${projectJar}

CALL "%~dp0\run-tool.bat" ${className} %* -l com/opengamma/util/warn-logback.xml
