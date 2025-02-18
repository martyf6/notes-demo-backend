package com.jfahey.notes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfahey.notes.config.NotesConfiguration;
import com.jfahey.notes.exception.NoteNotFoundException;
import com.jfahey.notes.model.api.NoteAPI;
import com.jfahey.notes.model.entity.Note;
import com.jfahey.notes.security.WebSecurityConfig;
import com.jfahey.notes.service.NoteService;
import com.jfahey.notes.util.TestFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(NotesController.class)
@Import({NotesConfiguration.class, WebSecurityConfig.class})
public class NotesControllerIntegrationTest {

    private static final String BASE_PATH = "/notes";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    @Test
    void givenNote_whenGetNoteById_thenReturnNote() throws Exception {
        Note existingNote = TestFixture.getExistingNote();
        String expectedNoteJSON = TestFixture.getExistingNoteJSON();

        when(noteService.getNoteByUserId(existingNote.getId(), existingNote.getUserId()))
                .thenReturn(Optional.of(existingNote));

        mockMvc.perform(get(BASE_PATH + "/" + existingNote.getId().toString())
                        .with(jwt().jwt(jwt ->
                                jwt.subject(existingNote.getUserId()))))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedNoteJSON));
        verify(noteService).getNoteByUserId(existingNote.getId(), existingNote.getUserId());
    }

    @Test
    void whenGetNoteById_thenNotFoundProblemDetails() throws Exception {
        UUID noteId = UUID.randomUUID();
        String userId = "user123";

        when(noteService.getNoteByUserId(noteId, userId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_PATH + "/" + noteId)
                        .with(jwt().jwt(jwt ->
                                jwt.subject(userId))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Note not found"))
                .andExpect(jsonPath("$.detail").value("The requested note could not be found."));
        verify(noteService).getNoteByUserId(noteId, userId);
    }

    @Test
    void whenSaveNewNote_thenNoteSaved() throws Exception {
        NoteAPI noteAPI = TestFixture.getNewNoteApi();
        String expectedNoteJSON = TestFixture.getExistingNoteJSON();
        String userId = "user123";

        Note savedNote = TestFixture.getExistingNote();
        when(noteService.createNote(any(Note.class))).thenReturn(savedNote);

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().jwt(jwt ->
                                jwt.subject(userId)))
                        .content(objectMapper.writeValueAsString(noteAPI)))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedNoteJSON));
        verify(noteService).createNote(any(Note.class));
    }

    @Test
    void whenUpdateNote_thenNoteSaved() throws Exception {
        NoteAPI noteAPI = TestFixture.getExistingNoteApi();
        noteAPI.setContent("Updated content.");
        String userId = "user123";

        Note updatedNote = TestFixture.getExistingNote();
        updatedNote.setContent("Updated content.");
        when(noteService.updateNote(any(Note.class))).thenReturn(updatedNote);

        mockMvc.perform(put(BASE_PATH + "/" + noteAPI.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().jwt(jwt ->
                                jwt.subject(userId)))
                        .content(objectMapper.writeValueAsString(noteAPI)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content."));
        verify(noteService).updateNote(any(Note.class));
    }

    @Test
    void givenNoExistingNote_whenUpdateNote_thenNoteNotFoundException() throws Exception {
        NoteAPI noteAPI = TestFixture.getExistingNoteApi();
        noteAPI.setContent("Updated content.");
        String userId = "user123";

        when(noteService.updateNote(any(Note.class)))
                .thenThrow(NoteNotFoundException.class);

        mockMvc.perform(put(BASE_PATH + "/" + noteAPI.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().jwt(jwt ->
                                jwt.subject(userId)))
                        .content(objectMapper.writeValueAsString(noteAPI)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Note not found"))
                .andExpect(jsonPath("$.detail").value("The requested note could not be found."));
        verify(noteService).updateNote(any(Note.class));
    }

    @Test
    void whenDeleteNote_thenNoteDeleted() throws Exception {
        Note existingNote = TestFixture.getExistingNote();

        mockMvc.perform(delete(BASE_PATH + "/" + existingNote.getId())
                        .with(jwt().jwt(jwt ->
                                jwt.subject(existingNote.getUserId()))))
                .andExpect(status().isNoContent());
        verify(noteService).deleteNoteByUserId(existingNote.getId(), existingNote.getUserId());
    }

    @Test
    void givenNoExistingNote_whenDeleteNote_thenNoteNotFoundException() throws Exception {
        UUID noteId = UUID.randomUUID();
        String userId = "user123";

        doThrow(NoteNotFoundException.class)
                .when(noteService).deleteNoteByUserId(noteId, userId);

        mockMvc.perform(delete(BASE_PATH + "/" + noteId)
                        .with(jwt().jwt(jwt ->
                                jwt.subject(userId))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Note not found"))
                .andExpect(jsonPath("$.detail").value("The requested note could not be found."));
        verify(noteService).deleteNoteByUserId(noteId, userId);
    }
}
