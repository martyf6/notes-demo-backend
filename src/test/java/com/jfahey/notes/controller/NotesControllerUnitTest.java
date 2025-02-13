package com.jfahey.notes.controller;

import com.jfahey.notes.exception.NoteNotFoundException;
import com.jfahey.notes.model.api.NoteAPI;
import com.jfahey.notes.model.entity.Note;
import com.jfahey.notes.service.NoteService;
import com.jfahey.notes.util.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotesControllerUnitTest {

    private NoteService noteService;

    private NotesController notesController;

    @BeforeEach
    void beforeEach() {
        noteService = mock(NoteService.class);
        notesController = new NotesController(noteService);
    }

    @Test
    void givenNote_whenGetNoteById_thenReturnNote() {
        Note existingNote = TestFixture.getExistingNote();
        NoteAPI expectedNote = TestFixture.getExistingNoteApi();
        Jwt userAuth = createUserAuth(existingNote.getUserId());

        when(noteService.getNoteByUserId(existingNote.getId(), existingNote.getUserId()))
                .thenReturn(Optional.of(existingNote));

        ResponseEntity<NoteAPI> noteResponse = notesController.getNote(userAuth, existingNote.getId());
        assertThat(noteResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
        assertThat(noteResponse.getBody()).isEqualTo(expectedNote);
    }

    @Test
    void whenGetNoteById_thenNoteNotFoundException() {
        UUID noteId = UUID.randomUUID();
        String userId = "user123";
        Jwt userAuth = createUserAuth(userId);

        when(noteService.getNoteByUserId(noteId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> notesController.getNote(userAuth, noteId))
                .isInstanceOf(NoteNotFoundException.class);
    }

    private Jwt createUserAuth(String subject) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(subject);
        return jwt;
    }
}
