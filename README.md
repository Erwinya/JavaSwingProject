# Swing Notepad

A modular multi-note desktop Notepad built with **Java Swing**.

Repository: [Erwinya/swing-notepad](https://github.com/Erwinya/swing-notepad)

## Features

- Multi-note sidebar (add, remove, rename)
- Rich text editing toolbar (styles, fonts, colors, lists)
- File open/save and export (TXT, RTF, HTML)
- Undo / redo, find & replace
- Light / dark / high-contrast themes
- Notes auto-saved to your user profile folder

## Requirements

- Java 17+
- Maven 3.9+ (optional if you only run a packaged JAR)

## Run

```bash
mvn -q test
mvn -q package
java -jar target/swing-notepad-1.0.0.jar
```

On Windows PowerShell:

```powershell
mvn -q package
java -jar target\swing-notepad-1.0.0.jar
```

## Project structure

```text
src/main/java/com/halukkilincer/notepad/
├── Main.java
├── NotepadApp.java
├── NotepadFrame.java
├── model/Note.java
├── service/NoteService.java
└── ui/
    ├── MenuBarFactory.java
    ├── NoteListPanel.java
    └── TextEditorPanel.java
```

## Notes storage

Saved notes are stored at:

```text
~/.swing-notepad/notes.ser
```

## License

MIT
