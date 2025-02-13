package com.jfahey.notes.service;

import com.jfahey.notes.model.entity.Note;
import com.jfahey.notes.repository.NoteRepository;
import com.jfahey.notes.repository.UserRepository;
import com.jfahey.notes.util.TestFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NoteServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private NoteService noteService;

    @AfterEach
    void afterEach() {
        noteRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void givenNote_whenGetNoteById_thenReturnNote() {
        Note existingNote = createTestNote();

        Optional<Note> optNote = noteService.getNoteByUserId(
                existingNote.getId(), existingNote.getUserId());
        assertThat(optNote).isPresent();
        assertThat(optNote.get()).isEqualTo(existingNote);
    }

    @Test
    void whenGetNoteById_thenReturnEmpty() {
        UUID noteId = UUID.randomUUID();
        String userId = "user123";

        Optional<Note> optNote = noteService.getNoteByUserId(noteId, userId);
        assertThat(optNote).isEmpty();
    }

    @Test
    void givenNote_whenGetNotesByUser_thenReturnNote() {
        Note existingNote = createTestNote();

        List<Note> notes = noteService.getNotesByUserId(existingNote.getUserId());
        assertThat(notes).hasSize(1);
        assertThat(notes).containsExactlyInAnyOrder(existingNote);
    }

    @Test
    void whenGetNotesByUser_thenReturnEmpty() {
        String userId = "user123";

        List<Note> notes = noteService.getNotesByUserId(userId);
        assertThat(notes).isEmpty();
    }

    @Test
    void whenSaveNote_thenReturnSavedNote() {
        Note note = new Note("alice1", "A Title", "Some Content");

        noteService.createNote(note);

        List<Note> savedNotes = noteRepository.findAll();
        assertThat(savedNotes).hasSize(1);
        Note savedNote = savedNotes.get(0);
        assertThat(savedNote.getUserId()).isEqualTo("alice1");
        assertThat(savedNote.getTitle()).isEqualTo("A Title");
        assertThat(savedNote.getContent()).isEqualTo("Some Content");
        assertThat(savedNote.getCreated()).isNotNull();
        assertThat(savedNote.getLastUpdated()).isNotNull();
    }

    @Test
    void givenNote_whenUpdateNote_thenReturnUpdatedNote() {
        Note existingNote = createTestNote();
        OffsetDateTime created = existingNote.getCreated();
        OffsetDateTime updated = existingNote.getLastUpdated();

        Note updateNote = new Note(existingNote.getUserId(), "Updated Title", "Updated Content");
        updateNote.setId(existingNote.getId());

        noteService.updateNote(updateNote);

        List<Note> savedNotes = noteRepository.findAll();
        assertThat(savedNotes).hasSize(1);
        Note savedNote = savedNotes.get(0);
        assertThat(savedNote.getUserId()).isEqualTo(existingNote.getUserId());
        assertThat(savedNote.getTitle()).isEqualTo("Updated Title");
        assertThat(savedNote.getContent()).isEqualTo("Updated Content");
        assertThat(savedNote.getCreated()).isEqualTo(created);
        assertThat(savedNote.getLastUpdated()).isAfter(updated);
    }

    @Test
    void givenNote_whenDeleteNote_thenNoteSaved() {
        Note existingNote = createTestNote();

        noteService.deleteNoteByUserId(existingNote.getId(), existingNote.getUserId());

        List<Note> savedNotes = noteRepository.findAll();
        assertThat(savedNotes).isEmpty();
    }

    private Note createTestNote() {
        Note note = TestFixture.getNewNote();
        return noteRepository.save(note);
    }
}
