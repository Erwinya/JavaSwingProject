package com.halukkilincer.notepad.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NoteTest {

    @Test
    void createsImmutableNote() {
        Note note = new Note("Title", "Body");
        assertEquals("Title", note.getTitle());
        assertEquals("Body", note.getContent());
    }

    @Test
    void rejectsNullFields() {
        assertThrows(NullPointerException.class, () -> new Note(null, "body"));
        assertThrows(NullPointerException.class, () -> new Note("title", null));
    }
}
