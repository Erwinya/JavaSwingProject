package com.halukkilincer.notepad.service;

import com.halukkilincer.notepad.model.Note;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * In-memory note collection with simple file persistence.
 */
public class NoteService {

    private static final Logger LOGGER = Logger.getLogger(NoteService.class.getName());
    private static final String SAVE_FILE_NAME = "notes.ser";

    private final List<Note> notes = new ArrayList<>();
    private final Path saveFile;

    public NoteService() {
        this(defaultSaveFile());
    }

    public NoteService(Path saveFile) {
        this.saveFile = Objects.requireNonNull(saveFile, "saveFile must not be null");
    }

    public List<Note> getNotes() {
        return Collections.unmodifiableList(notes);
    }

    public void addNote(Note note) {
        notes.add(Objects.requireNonNull(note, "note must not be null"));
    }

    public void removeNote(Note note) {
        notes.remove(note);
    }

    public void updateNote(int index, Note note) {
        notes.set(index, Objects.requireNonNull(note, "note must not be null"));
    }

    public Note getNote(int index) {
        return notes.get(index);
    }

    public int getNoteCount() {
        return notes.size();
    }

    public void saveAllNotes() {
        try {
            Path parent = saveFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(saveFile))) {
                out.writeObject(new ArrayList<>(notes));
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save notes to " + saveFile, e);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadAllNotes() {
        if (!Files.exists(saveFile)) {
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(saveFile))) {
            Object raw = in.readObject();
            if (raw instanceof List<?> loaded) {
                notes.clear();
                for (Object item : loaded) {
                    if (item instanceof Note note) {
                        notes.add(note);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Failed to load notes from " + saveFile, e);
        }
    }

    public Path getSaveFile() {
        return saveFile;
    }

    private static Path defaultSaveFile() {
        return Path.of(System.getProperty("user.home"), ".swing-notepad", SAVE_FILE_NAME);
    }
}
