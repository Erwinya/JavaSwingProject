package notepad.service;

import notepad.model.Note;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class NoteService {
    private final List<Note> notes = new ArrayList<>();
    private static final String SAVE_FILE = "notes.ser";

    public List<Note> getNotes() {
        return notes;
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public void removeNote(Note note) {
        notes.remove(note);
    }

    public void updateNote(int index, Note note) {
        notes.set(index, note);
    }

    public Note getNote(int index) {
        return notes.get(index);
    }

    public int getNoteCount() {
        return notes.size();
    }

    public void saveAllNotes() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(new ArrayList<>(notes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAllNotes() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            notes.clear();
            notes.addAll((ArrayList<Note>) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
} 