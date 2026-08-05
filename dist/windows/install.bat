@echo off
echo Installing NoteShelf...
set JAR=noteshelf-1.0.0.jar
set INSTALLDIR=%ProgramFiles%\NoteShelf
mkdir "%INSTALLDIR%" 2>nul
copy /Y "..\..\target\%JAR%" "%INSTALLDIR%\%JAR%"
echo @echo off > "%USERPROFILE%\Desktop\NoteShelf.bat"
echo java -jar "%INSTALLDIR%\%JAR%" >> "%USERPROFILE%\Desktop\NoteShelf.bat"
echo NoteShelf installed. Shortcut created on Desktop.
