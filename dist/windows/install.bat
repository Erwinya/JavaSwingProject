@echo off
setlocal
echo Installing NotepadApp...
set JAR=NotepadApp.jar
set INSTALLDIR=%ProgramFiles%\NotepadApp
if not exist "%INSTALLDIR%" mkdir "%INSTALLDIR%"
copy "%~dp0..\%JAR%" "%INSTALLDIR%\%JAR%"
echo @echo off > "%USERPROFILE%\Desktop\NotepadApp.bat"
echo java -jar "%INSTALLDIR%\%JAR%" >> "%USERPROFILE%\Desktop\NotepadApp.bat"
echo NotepadApp installed. Shortcut created on Desktop.
endlocal 