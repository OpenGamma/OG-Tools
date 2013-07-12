@echo off
IF NOT EXIST "%~dp0\..\bin\runtool.exe" GOTO noexe
"%~dp0\..\bin\runtool.exe" -elevate -plib\%PROJECTJAR% %*
GOTO done

:noexe
CALL "%~dp0\run-tool-noexe.bat" %*

:done
