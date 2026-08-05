#!/usr/bin/env bash
echo "Installing NoteShelf..."
INSTALL_DIR="$HOME/.local/share/noteshelf"
JAR_NAME="noteshelf-1.0.0.jar"
mkdir -p "$INSTALL_DIR"
cp "$(dirname "$0")/../../target/$JAR_NAME" "$INSTALL_DIR/$JAR_NAME"
cat > ~/Desktop/NoteShelf.desktop <<EOF
[Desktop Entry]
Type=Application
Name=NoteShelf
Exec=java -jar $INSTALL_DIR/$JAR_NAME
Icon=$INSTALL_DIR/$JAR_NAME
Terminal=false
EOF
chmod +x ~/Desktop/NoteShelf.desktop
echo "NoteShelf installed. Shortcut created on Desktop."
