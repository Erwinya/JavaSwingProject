package com.halukkilincer.notepad;

import com.halukkilincer.notepad.model.Note;
import com.halukkilincer.notepad.service.NoteService;
import com.halukkilincer.notepad.ui.NoteListPanel;
import com.halukkilincer.notepad.ui.TextEditorPanel;
import com.halukkilincer.notepad.ui.ThemeManager;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class NotepadFrame extends JFrame {
    private final NoteService noteService = new NoteService();
    private final NoteListPanel noteListPanel = new NoteListPanel(noteService);
    private final TextEditorPanel textEditorPanel = new TextEditorPanel();

    public NotepadFrame() {
        super(AppBranding.windowTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        List<Image> icons = AppBranding.windowIcons();
        if (!icons.isEmpty()) {
            setIconImages(icons);
            try {
                if (java.awt.Taskbar.isTaskbarSupported()) {
                    java.awt.Taskbar.getTaskbar().setIconImage(icons.get(Math.min(3, icons.size() - 1)));
                }
            } catch (UnsupportedOperationException | SecurityException ignored) {
                // ignore
            }
        }

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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                persistAndSave();
            }
        });

        noteListPanel.getNoteJList().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int previousIndex = textEditorPanel.getCurrentIndex();
            Note snapshot = textEditorPanel.snapshotNote();
            if (previousIndex >= 0 && snapshot != null) {
                noteListPanel.replaceAt(previousIndex, snapshot);
            }
            Note selected = noteListPanel.getNoteJList().getSelectedValue();
            int index = noteListPanel.getNoteJList().getSelectedIndex();
            textEditorPanel.displayNote(selected, index);
            noteService.saveAllNotes();
        });

        textEditorPanel.addDocumentChangeListener(this::persistAndSave);
        ThemeManager.applyDefault(this);
    }

    public void persistAndSave() {
        int index = textEditorPanel.getCurrentIndex();
        Note snapshot = textEditorPanel.snapshotNote();
        if (index >= 0 && snapshot != null) {
            noteListPanel.replaceAt(index, snapshot);
            textEditorPanel.bindCurrentNote(snapshot, index);
        }
        noteService.saveAllNotes();
    }
}
