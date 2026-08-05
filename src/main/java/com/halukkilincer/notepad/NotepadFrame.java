package com.halukkilincer.notepad;

import com.halukkilincer.notepad.model.Note;
import com.halukkilincer.notepad.service.NoteService;
import com.halukkilincer.notepad.ui.NoteListPanel;
import com.halukkilincer.notepad.ui.TextEditorPanel;

import javax.swing.*;
import java.awt.*;

public class NotepadFrame extends JFrame {
    private final NoteService noteService = new NoteService();
    private final NoteListPanel noteListPanel = new NoteListPanel(noteService);
    private final TextEditorPanel textEditorPanel = new TextEditorPanel();

    public NotepadFrame() {
        super("Swing Notepad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        textEditorPanel.setNoteService(noteService);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, noteListPanel, textEditorPanel);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        JMenuBar menuBar = com.halukkilincer.notepad.ui.MenuBarFactory.createMenuBar(
            textEditorPanel, noteService, noteListPanel, this
        );
        setJMenuBar(menuBar);

        noteService.loadAllNotes();
        noteListPanel.refresh();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                noteService.saveAllNotes();
            }
        });

        noteListPanel.getNoteJList().addListSelectionListener(e -> {
            Note selected = noteListPanel.getNoteJList().getSelectedValue();
            textEditorPanel.displayNote(selected);
            noteService.saveAllNotes();
        });
        textEditorPanel.addDocumentChangeListener(() -> noteService.saveAllNotes());
    }

    // ... (menu creation and actions as previously implemented) ...
} 