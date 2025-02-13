package com.jfahey.notes.util;

import com.jfahey.notes.model.api.NoteAPI;
import com.jfahey.notes.model.entity.Note;
import com.jfahey.notes.model.entity.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class TestFixture {

    private static final OffsetDateTime TS =
            OffsetDateTime.of(LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0, 0), ZoneOffset.UTC);

    public static User getNewUser() {
        return new User("user","user@domain.com");
    }

    public static User getExistingUser() {
        User user = getNewUser();
        user.setId(123L);
        return user;
    }

    public static Note getNewNote() {
        return new Note("user123", "My Title", "My Content");
    }

    public static Note getExistingNote() {
        Note note = getNewNote();
        note.setId(UUID.fromString("f474f326-ecfe-4382-ba00-d7c5d8344d1b"));
        note.setCreated(TS);
        note.setLastUpdated(TS);
        return note;
    }

    public static NoteAPI getNewNoteApi() {
        return NoteAPI.builder()
                .title("My Title")
                .content("My Content")
                .build();
    }

    public static NoteAPI getExistingNoteApi() {
        return NoteAPI.builder()
                .id(UUID.fromString("f474f326-ecfe-4382-ba00-d7c5d8344d1b"))
                .title("My Title")
                .content("My Content")
                .created(TS)
                .lastUpdated(TS)
                .build();
    }

    public static String getExistingNoteJSON() {
        return """
            {
                "id": "f474f326-ecfe-4382-ba00-d7c5d8344d1b",
                "title": "My Title",
                "content": "My Content",
                "created": "2024-01-01T00:00:00Z",
                "lastUpdated": "2024-01-01T00:00:00Z"
            }
            """;
    }
}
