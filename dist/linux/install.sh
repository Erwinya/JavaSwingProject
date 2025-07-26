#!/bin/bash
set -e
INSTALL_DIR="/opt/notepadapp"
JAR_NAME="NotepadApp.jar"

sudo mkdir -p "$INSTALL_DIR"
sudo cp "$(dirname "$0")/../NotepadApp.jar" "$INSTALL_DIR/$JAR_NAME"
echo "[Desktop Entry]" > ~/Desktop/NotepadApp.desktop
echo "Type=Application" >> ~/Desktop/NotepadApp.desktop
echo "Name=NotepadApp" >> ~/Desktop/NotepadApp.desktop
echo "Exec=java -jar $INSTALL_DIR/$JAR_NAME" >> ~/Desktop/NotepadApp.desktop
echo "Icon=java" >> ~/Desktop/NotepadApp.desktop
echo "Terminal=false" >> ~/Desktop/NotepadApp.desktop
chmod +x ~/Desktop/NotepadApp.desktop
echo "NotepadApp installed. Shortcut created on Desktop." 