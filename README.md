# NoteShelf

A focused multi-note desktop editor built with **Java Swing**.

Repository: [Erwinya/noteshelf](https://github.com/Erwinya/noteshelf)

## Features

- Multi-note sidebar (add, remove, rename)
- Modern formatting toolbar (fonts, styles, colors, alignment, lists)
- File open/save and export (TXT, RTF, HTML)
- Undo / redo, find & replace
- Light / dark / high-contrast themes
- HK brand icon; notes auto-saved under your user profile

## Requirements

- Java 17+
- Maven 3.9+ (optional if you only run a packaged JAR)

## Run

```bash
mvn -q test
mvn -q package
java -jar target/noteshelf-1.0.0.jar
```

On Windows PowerShell:

```powershell
mvn -q package
java -jar target\noteshelf-1.0.0.jar
```

## Project structure

```text
src/main/java/com/halukkilincer/notepad/
├── AppBranding.java
├── Main.java
├── NotepadApp.java
├── NotepadFrame.java
├── model/Note.java
├── service/NoteService.java
└── ui/
    ├── MenuBarFactory.java
    ├── NoteListPanel.java
    ├── TextEditorPanel.java
    ├── ThemeManager.java
    └── ToolbarIcons.java
src/main/resources/
└── logo-hk.png
```

## Notes storage

```text
~/.noteshelf/notes.ser
```

Existing data from `~/.swing-notepad/` is loaded automatically on first run, then rewritten to the new location on save.

## License

MIT
