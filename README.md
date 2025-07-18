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
 └── src/
      ├── Main.java
      └── notepad/
           ├── NotepadApp.java
           ├── NotepadFrame.java
           ├── model/
           │    └── Note.java
           ├── service/
           │    └── NoteService.java
           └── ui/
                ├── NoteListPanel.java
                ├── TextEditorPanel.java
                └── MenuBarFactory.java
```

## SOLID & Clean Code Principles
- **Single Responsibility:** Each class has one clear responsibility (UI, model, service, etc.)
- **Open/Closed:** Easily extendable with new features without modifying existing code
- **Liskov Substitution:** Components can be replaced with their subtypes
- **Interface Segregation:** UI and service layers are loosely coupled
- **Dependency Inversion:** UI depends on abstractions, not concrete implementations
- **Clean Code:** Descriptive names, short and focused methods, no duplication, clear comments, no dead code

## Installation & Running
1. **Clone or download the project.**
2. **Navigate to the project root in your terminal.**
3. **Compile:**
   ```bash
   mkdir -p out
   javac -d out src/Main.java src/notepad/NotepadApp.java src/notepad/NotepadFrame.java src/notepad/model/Note.java src/notepad/service/NoteService.java src/notepad/ui/TextEditorPanel.java src/notepad/ui/MenuBarFactory.java src/notepad/ui/NoteListPanel.java
   ```
4. **Run:**
   ```bash
   java -cp out Main
   ```

## Usage
- Add, delete, or rename notes from the left panel (double-click to rename)
- Use the rich text editor on the right for formatting
- Manage files, themes, and settings from the menu bar
- Access shortcuts and help from the Help menu

## Contribution & License
- Open source and contributions are welcome
- Please follow SOLID and Clean Code principles for any contributions 