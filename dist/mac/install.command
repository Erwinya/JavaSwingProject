#!/bin/bash
set -e
INSTALL_DIR="/Applications/NotepadApp"
JAR_NAME="NotepadApp.jar"

mkdir -p "$INSTALL_DIR"
cp "$(dirname "$0")/../NotepadApp.jar" "$INSTALL_DIR/$JAR_NAME"
echo "#!/bin/bash" > ~/Desktop/NotepadApp.command
echo "java -jar $INSTALL_DIR/$JAR_NAME" >> ~/Desktop/NotepadApp.command
chmod +x ~/Desktop/NotepadApp.command
echo "NotepadApp installed. Shortcut created on Desktop." 