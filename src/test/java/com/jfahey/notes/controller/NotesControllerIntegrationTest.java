package com.jfahey.notes.controller;

import com.jfahey.notes.config.NotesConfiguration;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(NotesController.class)
@Import({NotesConfiguration.class, WebSecurityConfig.class})
public class NotesControllerIntegrationTest {

    private static final String BASE_PATH = "/notes";

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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedNoteJSON));
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
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Note not found"))
                .andExpect(jsonPath("$.detail").value("The requested note could not be found."));
    }

}
