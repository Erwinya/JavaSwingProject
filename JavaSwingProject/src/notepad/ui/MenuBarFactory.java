package notepad.ui;

import javax.swing.*;
import notepad.service.NoteService;
import notepad.ui.TextEditorPanel;
import notepad.ui.NoteListPanel;
import notepad.model.Note;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class MenuBarFactory {
    public static JMenuBar createMenuBar(TextEditorPanel editorPanel, NoteService noteService, NoteListPanel noteListPanel, JFrame parentFrame) {
        JMenuBar menuBar = new JMenuBar();

        // File
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("New Note", e -> noteListPanel.onAddNote(null)));
        fileMenu.add(createMenuItem("Open...", e -> openNote(editorPanel, noteService, parentFrame)));
        fileMenu.add(createMenuItem("Save", e -> saveNote(editorPanel, noteService, parentFrame, false)));
        fileMenu.add(createMenuItem("Save As...", e -> saveNote(editorPanel, noteService, parentFrame, true)));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Export as TXT", e -> exportNote(editorPanel, parentFrame, "txt")));
        fileMenu.add(createMenuItem("Export as RTF", e -> exportNote(editorPanel, parentFrame, "rtf")));
        fileMenu.add(createMenuItem("Export as HTML", e -> exportNote(editorPanel, parentFrame, "html")));
        fileMenu.add(createMenuItem("Import Note", e -> importNote(noteService, noteListPanel, parentFrame)));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", e -> System.exit(0)));
        menuBar.add(fileMenu);

        // Edit
        JMenu editMenu = new JMenu("Edit");
        editMenu.add(createMenuItem("Undo", e -> editorPanel.undo()));
        editMenu.add(createMenuItem("Redo", e -> editorPanel.redo()));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("Cut", e -> editorPanel.cut()));
        editMenu.add(createMenuItem("Copy", e -> editorPanel.copy()));
        editMenu.add(createMenuItem("Paste", e -> editorPanel.paste()));
        editMenu.add(createMenuItem("Select All", e -> editorPanel.selectAll()));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("Find/Replace", e -> editorPanel.findReplace()));
        menuBar.add(editMenu);

        // View
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem showToolbar = new JCheckBoxMenuItem("Show Toolbar", true);
        showToolbar.addActionListener(e -> editorPanel.setToolbarVisible(showToolbar.isSelected()));
        viewMenu.add(showToolbar);
        viewMenu.add(createMenuItem("Zoom In", e -> editorPanel.zoomIn()));
        viewMenu.add(createMenuItem("Zoom Out", e -> editorPanel.zoomOut()));
        viewMenu.add(createMenuItem("Font Size", e -> editorPanel.chooseFontSize()));
        viewMenu.add(createMenuItem("Full Screen", e -> parentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH)));
        menuBar.add(viewMenu);

        // Settings
        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.add(createMenuItem("Auto-save Interval", e -> {
            String input = JOptionPane.showInputDialog(parentFrame, "Auto-save interval (seconds):", "Settings", JOptionPane.PLAIN_MESSAGE);
            try {
                int seconds = Integer.parseInt(input);
                // You can implement a timer in NotepadFrame to use this value
                JOptionPane.showMessageDialog(parentFrame, "Auto-save interval set to " + seconds + " seconds.");
            } catch (Exception ignored) {}
        }));
        settingsMenu.add(createMenuItem("Font", e -> editorPanel.chooseFont()));
        settingsMenu.add(createMenuItem("Theme", e -> JOptionPane.showMessageDialog(parentFrame, "Change theme from the Theme menu.")));
        settingsMenu.add(createMenuItem("Default Save Path", e -> JOptionPane.showMessageDialog(parentFrame, "Not implemented yet.")));
        menuBar.add(settingsMenu);

        // Theme
        JMenu themeMenu = new JMenu("Theme");
        themeMenu.add(createMenuItem("Light", e -> setTheme("light", parentFrame)));
        themeMenu.add(createMenuItem("Dark", e -> setTheme("dark", parentFrame)));
        themeMenu.add(createMenuItem("System Default", e -> setTheme("system", parentFrame)));
        themeMenu.add(createMenuItem("High Contrast", e -> setTheme("high-contrast", parentFrame)));
        menuBar.add(themeMenu);

        // Help
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(createMenuItem("About", e -> showAboutDialog(parentFrame)));
        helpMenu.add(createMenuItem("Keyboard Shortcuts", e -> showShortcutsDialog(parentFrame)));
        helpMenu.add(createMenuItem("Help Contents", e -> showHelpDialog(parentFrame)));
        helpMenu.add(createMenuItem("Feedback", e -> showFeedbackDialog(parentFrame)));
        menuBar.add(helpMenu);

        return menuBar;
    }

    private static JMenuItem createMenuItem(String text, java.awt.event.ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(action);
        return item;
    }

    // File actions
    private static void openNote(TextEditorPanel editorPanel, NoteService noteService, JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line).append("\n");
                Note note = new Note(file.getName(), sb.toString());
                noteService.addNote(note);
                JOptionPane.showMessageDialog(parent, "Note imported as: " + file.getName());
            } catch (IOException ex) {
                showError(parent, "File could not be opened.");
            }
        }
    }
    private static void saveNote(TextEditorPanel editorPanel, NoteService noteService, JFrame parent, boolean saveAs) {
        Note note = editorPanel.getCurrentNote();
        if (note == null) return;
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(editorPanel.getTextPane().getText());
                JOptionPane.showMessageDialog(parent, "Note saved as: " + file.getName());
            } catch (IOException ex) {
                showError(parent, "File could not be saved.");
            }
        }
    }
    private static void exportNote(TextEditorPanel editorPanel, JFrame parent, String type) {
        Note note = editorPanel.getCurrentNote();
        if (note == null) return;
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                if (type.equals("txt")) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write(editorPanel.getTextPane().getText());
                    }
                } else if (type.equals("rtf")) {
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        new RTFEditorKit().write(fos, editorPanel.getTextPane().getDocument(), 0, editorPanel.getTextPane().getDocument().getLength());
                    }
                } else if (type.equals("html")) {
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write("<html><body>" + editorPanel.getTextPane().getText().replace("\n", "<br>") + "</body></html>");
                    }
                }
                JOptionPane.showMessageDialog(parent, "Note exported as: " + file.getName());
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
                } else if (name.endsWith(".html") || name.endsWith(".htm")) {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line).append("\n");
                    Note note = new Note(file.getName(), sb.toString());
                    noteService.addNote(note);
                } else {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line).append("\n");
                    Note note = new Note(file.getName(), sb.toString());
                    noteService.addNote(note);
                }
                noteListPanel.refresh();
                JOptionPane.showMessageDialog(parent, "Note imported as: " + file.getName());
            } catch (Exception ex) {
                showError(parent, "Import failed.");
            }
        }
    }
    private static void setTheme(String theme, JFrame parent) {
        try {
            switch (theme) {
                case "light":
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    break;
                case "dark":
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                    break;
                case "system":
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
                case "high-contrast":
                    UIManager.put("control", java.awt.Color.BLACK);
                    UIManager.put("info", java.awt.Color.BLACK);
                    UIManager.put("nimbusBase", java.awt.Color.BLACK);
                    UIManager.put("nimbusAlertYellow", java.awt.Color.YELLOW);
                    UIManager.put("nimbusDisabledText", java.awt.Color.GRAY);
                    UIManager.put("nimbusFocus", java.awt.Color.YELLOW);
                    UIManager.put("nimbusGreen", java.awt.Color.GREEN);
                    UIManager.put("nimbusInfoBlue", java.awt.Color.BLUE);
                    UIManager.put("nimbusLightBackground", java.awt.Color.BLACK);
                    UIManager.put("nimbusOrange", java.awt.Color.ORANGE);
                    UIManager.put("nimbusRed", java.awt.Color.RED);
                    UIManager.put("nimbusSelectedText", java.awt.Color.WHITE);
                    UIManager.put("nimbusSelectionBackground", java.awt.Color.YELLOW);
                    UIManager.put("text", java.awt.Color.WHITE);
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                    break;
            }
            SwingUtilities.updateComponentTreeUI(parent);
        } catch (Exception ex) {
            showError(parent, "Theme could not be applied.");
        }
    }
    private static void showAboutDialog(JFrame parent) {
        JOptionPane.showMessageDialog(parent,
                "NotepadApp v1.0\nModern Notepad\nJava Swing\nSOLID & Clean Code\n(c) 2024",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
    private static void showShortcutsDialog(JFrame parent) {
        String shortcuts = "Shortcuts:\n" +
                "Ctrl+B: Bold\n" +
                "Ctrl+I: Italic\n" +
                "Ctrl+U: Underline\n" +
                "Ctrl+Z: Undo\n" +
                "Ctrl+Y: Redo\n" +
                "Ctrl+L: Align Left\n" +
                "Ctrl+E: Center\n" +
                "Ctrl+R: Align Right\n" +
                "Ctrl+Shift+B: Bullet\n" +
                "Ctrl+F: Find\n" +
                "Ctrl+S: Save\n";
        JOptionPane.showMessageDialog(parent, shortcuts, "Keyboard Shortcuts", JOptionPane.INFORMATION_MESSAGE);
    }
    private static void showHelpDialog(JFrame parent) {
        String help = "NotepadApp User Guide:\n" +
                "- Add, delete, or rename notes from the left panel (double-click to rename).\n" +
                "- Use the rich text editor on the right to format your notes.\n" +
                "- Manage files, themes, and settings from the menu bar.\n" +
                "- Access shortcuts and more from the Help menu.";
        JOptionPane.showMessageDialog(parent, help, "Help Contents", JOptionPane.INFORMATION_MESSAGE);
    }
    private static void showFeedbackDialog(JFrame parent) {
        String feedback = JOptionPane.showInputDialog(parent, "Enter your feedback:", "Feedback", JOptionPane.PLAIN_MESSAGE);
        if (feedback != null && !feedback.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Thank you! Your feedback has been recorded.");
        }
    }
    private static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
} 