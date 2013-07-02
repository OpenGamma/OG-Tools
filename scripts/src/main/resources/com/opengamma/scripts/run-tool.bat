@echo off
if not exist "%~dp0\..\bin\runtool.exe" goto noexe
"%~dp0\..\bin\runtool.exe" -elevate -plib\%PROJECTJAR% %*
goto done

:noexe
call "%~dp0\run-tool-deprecated.bat" %*

:done
