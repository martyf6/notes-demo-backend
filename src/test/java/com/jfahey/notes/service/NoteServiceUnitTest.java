package com.jfahey.notes.service;

import com.jfahey.notes.exception.NoteNotFoundException;
import com.jfahey.notes.model.entity.Note;
import com.jfahey.notes.repository.NoteRepository;
import com.jfahey.notes.util.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NoteServiceUnitTest {

    private NoteRepository noteRepository;

    private NoteService noteService;

    @BeforeEach
    void beforeEach() {
        noteRepository = mock(NoteRepository.class);
        noteService = new NoteService(noteRepository);
    }

    @Test
    void givenNote_whenGetNoteById_thenReturnNote() {
        Note note = TestFixture.getExistingNote();

        when(noteRepository.findByIdAndUserId(note.getId(), note.getUserId()))
                .thenReturn(Optional.of(note));

        Optional<Note> optNote = noteService.getNoteByUserId(note.getId(), note.getUserId());
        assertThat(optNote).isPresent();
        assertThat(optNote.get()).isEqualTo(note);
        verify(noteRepository).findByIdAndUserId(note.getId(), note.getUserId());
    }

    @Test
    void whenGetNoteById_thenReturnEmpty() {
        UUID noteId = UUID.randomUUID();
        String userId = "user123";

        when(noteRepository.findByIdAndUserId(noteId, userId))
                .thenReturn(Optional.empty());

        Optional<Note> optNote = noteService.getNoteByUserId(noteId, userId);
        assertThat(optNote).isEmpty();
        verify(noteRepository).findByIdAndUserId(noteId, userId);
    }

    @Test
    void givenNote_whenGetNotesByUser_thenReturnNote() {
        Note note = TestFixture.getExistingNote();

        when(noteRepository.findByUserId(any(String.class)))
                .thenReturn(List.of(note));

        List<Note> notes = noteService.getNotesByUserId(note.getUserId());
        assertThat(notes).hasSize(1);
        assertThat(notes).containsExactlyInAnyOrder(note);
        verify(noteRepository).findByUserId(note.getUserId());
    }

    @Test
    void whenGetNotesByUser_thenReturnEmpty() {
        String userId = "user123";

        when(noteRepository.findByUserId(any(String.class)))
                .thenReturn(List.of());

        List<Note> notes = noteService.getNotesByUserId(userId);
        assertThat(notes).isEmpty();
        verify(noteRepository).findByUserId(userId);
    }

    @Test
    void givenNote_whenSaveNote_thenReturnSavedNote() {
        Note note = TestFixture.getExistingNote();

        when(noteRepository.save(note)).thenReturn(note);

        Note savedNote = noteService.createNote(note);
        assertThat(savedNote).isEqualTo(note);
        verify(noteRepository).save(savedNote);
    }

    @Test
    void givenNote_whenUpdateNote_thenReturnUpdatedNote() {
        Note note = TestFixture.getExistingNote();

        when(noteRepository.findByIdAndUserId(note.getId(), note.getUserId()))
                .thenReturn(Optional.of(note));
        when(noteRepository.save(note)).thenReturn(note);

        Note updatedNote = noteService.updateNote(note);
        assertThat(updatedNote).isEqualTo(note);
        verify(noteRepository).findByIdAndUserId(note.getId(), note.getUserId());
        verify(noteRepository).save(updatedNote);
    }

    @Test
    void whenUpdateNote_thenNoNoteFoundException() {
        Note note = TestFixture.getExistingNote();

        when(noteRepository.findByIdAndUserId(note.getId(), note.getUserId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.updateNote(note))
                .isInstanceOf(NoteNotFoundException.class);
        verify(noteRepository).findByIdAndUserId(note.getId(), note.getUserId());
        verify(noteRepository, never()).save(note);
    }

    @Test
    void givenNote_whenDeleteNote_thenNoteSaved() {
        Note note = TestFixture.getExistingNote();

        when(noteRepository.findByIdAndUserId(note.getId(), note.getUserId()))
                .thenReturn(Optional.of(note));

        noteService.deleteNoteByUserId(note.getId(), note.getUserId());
        verify(noteRepository).findByIdAndUserId(note.getId(), note.getUserId());
        verify(noteRepository).deleteById(note.getId());
    }

    @Test
    void whenDeleteNote_thenNoNoteFoundException() {
        Note note = TestFixture.getExistingNote();

        when(noteRepository.findByIdAndUserId(note.getId(), note.getUserId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.deleteNoteByUserId(note.getId(), note.getUserId()))
                .isInstanceOf(NoteNotFoundException.class);
        verify(noteRepository).findByIdAndUserId(note.getId(), note.getUserId());
        verify(noteRepository, never()).deleteById(note.getId());
    }
}
