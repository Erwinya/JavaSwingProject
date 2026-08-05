#!/bin/bash
echo "Installing NoteShelf..."
INSTALL_DIR="/Applications/NoteShelf"
JAR_NAME="noteshelf-1.0.0.jar"
mkdir -p "$INSTALL_DIR"
cp "$(dirname "$0")/../../target/$JAR_NAME" "$INSTALL_DIR/$JAR_NAME"
echo "#!/bin/bash" > ~/Desktop/NoteShelf.command
echo "java -jar $INSTALL_DIR/$JAR_NAME" >> ~/Desktop/NoteShelf.command
chmod +x ~/Desktop/NoteShelf.command
echo "NoteShelf installed. Shortcut created on Desktop."
