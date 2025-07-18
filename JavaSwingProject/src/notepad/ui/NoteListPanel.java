package notepad.ui;

import notepad.model.Note;
import notepad.service.NoteService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.event.MouseInputAdapter;

public class NoteListPanel extends JPanel {
    private final DefaultListModel<Note> listModel = new DefaultListModel<>();
    private final JList<Note> noteJList = new JList<>(listModel);
    private final NoteService noteService;

    public NoteListPanel(NoteService noteService) {
        this.noteService = noteService;
        setLayout(new BorderLayout());
        noteJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noteJList.setCellRenderer(new NoteCellRenderer());
        add(new JScrollPane(noteJList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Note");
        JButton removeButton = new JButton("Remove Note");
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(this::onAddNote);
        removeButton.addActionListener(this::onRemoveNote);
        noteJList.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = noteJList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Note note = listModel.getElementAt(index);
                        String newTitle = JOptionPane.showInputDialog(NoteListPanel.this, "Edit note title:", note.getTitle());
                        if (newTitle != null && !newTitle.trim().isEmpty()) {
                            note.setTitle(newTitle.trim());
                            noteService.updateNote(index, note);
                            refresh();
                            noteJList.setSelectedIndex(index);
                        }
                    }
                }
            }
        });
    }

    public void onAddNote(ActionEvent e) {
        Note note = new Note("New Note", "");
        noteService.addNote(note);
        listModel.addElement(note);
        noteJList.setSelectedValue(note, true);
    }

    private void onRemoveNote(ActionEvent e) {
        Note selected = noteJList.getSelectedValue();
        if (selected != null) {
            noteService.removeNote(selected);
            listModel.removeElement(selected);
        }
    }

    public void refresh() {
        listModel.clear();
        for (Note note : noteService.getNotes()) {
            listModel.addElement(note);
        }
    }

    public JList<Note> getNoteJList() {
        return noteJList;
    }

    private static class NoteCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Note) {
                label.setText(((Note) value).getTitle());
            }
            return label;
        }
    }
} 