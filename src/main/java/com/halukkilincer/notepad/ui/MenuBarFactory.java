package com.halukkilincer.notepad.ui;

import com.halukkilincer.notepad.AppBranding;
import com.halukkilincer.notepad.NotepadFrame;
import com.halukkilincer.notepad.model.Note;
import com.halukkilincer.notepad.service.NoteService;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

public final class MenuBarFactory {

    private MenuBarFactory() {
    }

    public static JMenuBar createMenuBar(
            TextEditorPanel editorPanel,
            NoteService noteService,
            NoteListPanel noteListPanel,
            JFrame parentFrame
    ) {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("New Note", e -> noteListPanel.onAddNote(null)));
        fileMenu.add(createMenuItem("Open...", e -> openNote(noteService, noteListPanel, parentFrame)));
        JMenuItem saveItem = createMenuItem("Save", e -> persist(parentFrame));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(saveItem);
        fileMenu.add(createMenuItem("Save As...", e -> saveNote(editorPanel, parentFrame)));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Export as TXT", e -> exportNote(editorPanel, parentFrame, "txt")));
        fileMenu.add(createMenuItem("Export as RTF", e -> exportNote(editorPanel, parentFrame, "rtf")));
        fileMenu.add(createMenuItem("Export as HTML", e -> exportNote(editorPanel, parentFrame, "html")));
        fileMenu.add(createMenuItem("Import Note", e -> importNote(noteService, noteListPanel, parentFrame)));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", e ->
                parentFrame.dispatchEvent(new WindowEvent(parentFrame, WindowEvent.WINDOW_CLOSING))));
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        editMenu.add(createMenuItem("Undo", e -> editorPanel.undo()));
        editMenu.add(createMenuItem("Redo", e -> editorPanel.redo()));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("Cut", e -> editorPanel.cut()));
        editMenu.add(createMenuItem("Copy", e -> editorPanel.copy()));
        editMenu.add(createMenuItem("Paste", e -> editorPanel.paste()));
        editMenu.add(createMenuItem("Select All", e -> editorPanel.selectAll()));
        editMenu.addSeparator();
        JMenuItem findItem = createMenuItem("Find/Replace", e -> editorPanel.findReplace());
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        editMenu.add(findItem);
        menuBar.add(editMenu);

        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem showToolbar = new JCheckBoxMenuItem("Show Toolbar", true);
        showToolbar.addActionListener(e -> editorPanel.setToolbarVisible(showToolbar.isSelected()));
        viewMenu.add(showToolbar);
        viewMenu.add(createMenuItem("Zoom In", e -> editorPanel.zoomIn()));
        viewMenu.add(createMenuItem("Zoom Out", e -> editorPanel.zoomOut()));
        viewMenu.add(createMenuItem("Font Size", e -> editorPanel.chooseFontSize()));
        viewMenu.add(createMenuItem("Full Screen", e -> parentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH)));
        menuBar.add(viewMenu);

        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.add(createMenuItem("Default Font...", e -> editorPanel.chooseFont()));
        settingsMenu.add(createMenuItem("Notes file location", e ->
                JOptionPane.showMessageDialog(
                        parentFrame,
                        "Notes are stored at:\n" + noteService.getSaveFile(),
                        "Storage",
                        JOptionPane.INFORMATION_MESSAGE
                )));
        menuBar.add(settingsMenu);

        JMenu themeMenu = new JMenu("Theme");
        themeMenu.add(createMenuItem("Light", e -> applyTheme(ThemeManager.Theme.LIGHT, parentFrame)));
        themeMenu.add(createMenuItem("Dark", e -> applyTheme(ThemeManager.Theme.DARK, parentFrame)));
        themeMenu.add(createMenuItem("System Default", e -> applyTheme(ThemeManager.Theme.SYSTEM, parentFrame)));
        themeMenu.add(createMenuItem("High Contrast", e -> applyTheme(ThemeManager.Theme.HIGH_CONTRAST, parentFrame)));
        menuBar.add(themeMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(createMenuItem("About", e -> showAboutDialog(parentFrame)));
        helpMenu.add(createMenuItem("Keyboard Shortcuts", e -> showShortcutsDialog(parentFrame)));
        helpMenu.add(createMenuItem("Help Contents", e -> showHelpDialog(parentFrame)));
        helpMenu.add(createMenuItem("Website", e -> openWebsite(parentFrame)));
        menuBar.add(helpMenu);

        return menuBar;
    }

    private static void persist(JFrame parentFrame) {
        if (parentFrame instanceof NotepadFrame frame) {
            frame.persistAndSave();
        }
    }

    private static JMenuItem createMenuItem(String text, java.awt.event.ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(action);
        return item;
    }

    private static void openNote(NoteService noteService, NoteListPanel noteListPanel, JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                Note note = new Note(file.getName(), sb.toString());
                noteService.addNote(note);
                noteListPanel.refresh();
                noteListPanel.getNoteJList().setSelectedValue(note, true);
            } catch (IOException ex) {
                showError(parent, "File could not be opened.");
            }
        }
    }

    private static void saveNote(TextEditorPanel editorPanel, JFrame parent) {
        if (editorPanel.getCurrentNote() == null) {
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(editorPanel.getTextPane().getText());
            } catch (IOException ex) {
                showError(parent, "File could not be saved.");
            }
        }
    }

    private static void exportNote(TextEditorPanel editorPanel, JFrame parent, String type) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                switch (type) {
                    case "txt" -> {
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                            writer.write(editorPanel.getTextPane().getText());
                        }
                    }
                    case "rtf" -> {
                        RTFEditorKit rtfKit = new RTFEditorKit();
                        try (FileWriter writer = new FileWriter(file)) {
                            rtfKit.write(writer, editorPanel.getTextPane().getDocument(), 0,
                                    editorPanel.getTextPane().getDocument().getLength());
                        }
                    }
                    case "html" -> {
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                            writer.write("<html><body><pre>");
                            writer.write(editorPanel.getTextPane().getText());
                            writer.write("</pre></body></html>");
                        }
                    }
                    default -> {
                    }
                }
                JOptionPane.showMessageDialog(parent, "Exported successfully.");
            } catch (Exception ex) {
                showError(parent, "Export failed.");
            }
        }
    }

    private static void importNote(NoteService noteService, NoteListPanel noteListPanel, JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String name = file.getName().toLowerCase();
            try {
                if (name.endsWith(".rtf")) {
                    RTFEditorKit rtfKit = new RTFEditorKit();
                    Document doc = rtfKit.createDefaultDocument();
                    rtfKit.read(new FileInputStream(file), doc, 0);
                    Note note = new Note(file.getName(), doc.getText(0, doc.getLength()));
                    noteService.addNote(note);
                } else {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append('\n');
                        }
                        noteService.addNote(new Note(file.getName(), sb.toString()));
                    }
                }
                noteListPanel.refresh();
                JOptionPane.showMessageDialog(parent, "Note imported as: " + file.getName());
            } catch (Exception ex) {
                showError(parent, "Import failed.");
            }
        }
    }

    private static void applyTheme(ThemeManager.Theme theme, JFrame parent) {
        try {
            ThemeManager.apply(theme, parent);
        } catch (Exception ex) {
            showError(parent, "Theme could not be applied.");
        }
    }

    private static void showAboutDialog(JFrame parent) {
        JOptionPane.showMessageDialog(
                parent,
                AppBranding.aboutText(),
                "About " + AppBranding.APP_NAME,
                JOptionPane.INFORMATION_MESSAGE,
                AppBranding.aboutIcon(64)
        );
    }

    private static void showShortcutsDialog(JFrame parent) {
        String shortcuts = """
                Shortcuts:
                Ctrl+S: Save notes
                Ctrl+B: Bold
                Ctrl+I: Italic
                Ctrl+U: Underline
                Ctrl+Z: Undo
                Ctrl+Y: Redo
                Ctrl+L: Align left
                Ctrl+E: Center
                Ctrl+R: Align right
                Ctrl+Shift+B: Bullet list
                Ctrl+F: Find/Replace
                """;
        JOptionPane.showMessageDialog(parent, shortcuts, "Keyboard Shortcuts", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showHelpDialog(JFrame parent) {
        String help = AppBranding.APP_NAME + """
                 guide:
                - Add, delete, or rename notes from the left panel (double-click to rename).
                - Use the toolbar to format text, align paragraphs, and build lists.
                - Notes auto-save under your user profile; Ctrl+S also saves immediately.
                - Themes are available from the Theme menu.
                """;
        JOptionPane.showMessageDialog(parent, help, "Help Contents", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void openWebsite(JFrame parent) {
        try {
            Desktop.getDesktop().browse(URI.create("https://halukkilincer.com"));
        } catch (Exception ex) {
            showError(parent, "Could not open website.");
        }
    }

    private static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
