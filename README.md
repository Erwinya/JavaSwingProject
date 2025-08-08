# Java Swing Notepad

A modern, modular, and extensible Notepad application built with Java Swing, following SOLID and Clean Code principles.

## Features
- Multi-note support (add, remove, rename notes)
- Rich text editing: bold, italic, underline, strikethrough, headings, title, subtitle, quote, code, highlight, lists, tables, images, links
- Font family and size selection (popular and system fonts)
- Undo/Redo, cut/copy/paste, select all, find/replace
- File operations: New, Open, Save, Save As, Export (TXT/RTF/HTML), Import
- Auto-save with configurable interval
- Theme support: Light, Dark, System, High Contrast
- Settings menu (font, theme, auto-save, default save path)
- View menu (show/hide toolbar, zoom, font size, full screen)
- Help menu (About, Keyboard Shortcuts, User Guide, Feedback)
- SOLID and Clean Code architecture for easy maintenance and extension

## Project Structure
```
JavaSwingProject/
 ├── src/
 │    ├── Main.java
 │    └── notepad/
 │         ├── NotepadApp.java
 │         ├── NotepadFrame.java
 │         ├── model/
 │         │    └── Note.java
 │         ├── service/
 │         │    └── NoteService.java
 │         └── ui/
 │              ├── NoteListPanel.java
 │              ├── TextEditorPanel.java
 │              └── MenuBarFactory.java
 ├── dist/
 │    ├── NotepadApp.jar
 │    ├── windows/
 │    │    ├── README.txt
 │    │    └── NotepadApp.exe (opsiyonel)
 │    ├── mac/
 │    │    ├── README.txt
 │    │    └── NotepadApp.command
 │    └── linux/
 │         ├── README.txt
 │         └── NotepadApp.sh
 └── notes.ser
```

- **Clean Code:** Descriptive names, short and focused methods, no duplication, clear comments, no dead code

## SOLID & Clean Code Principles
...existing code...
   ```

## Usage
- Add, delete, or rename notes from the left panel (double-click to rename)
- Use the rich text editor on the right for formatting
- Manage files, themes, and settings from the menu bar
- Access shortcuts and help from the Help menu

## Contribution & License
- Open source and contributions are welcome
- Please follow SOLID and Clean Code principles for any contributions 