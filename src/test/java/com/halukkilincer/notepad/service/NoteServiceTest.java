package com.halukkilincer.notepad.service;

import com.halukkilincer.notepad.model.Note;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoteServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void addAndPersistNotes() {
        Path saveFile = tempDir.resolve("notes.ser");
        NoteService service = new NoteService(saveFile);
        service.addNote(new Note("Ideas", "Write clean code"));
        service.addNote(new Note("Todo", "Ship the app"));
        service.saveAllNotes();

        NoteService reloaded = new NoteService(saveFile);
        reloaded.loadAllNotes();

        assertEquals(2, reloaded.getNoteCount());
        assertEquals("Ideas", reloaded.getNote(0).getTitle());
        assertEquals("Write clean code", reloaded.getNote(0).getContent());
        assertTrue(Files.exists(saveFile));
    }

    @Test
    void rejectsNullNote() {
        NoteService service = new NoteService(tempDir.resolve("notes.ser"));
        assertThrows(NullPointerException.class, () -> service.addNote(null));
    }
}
