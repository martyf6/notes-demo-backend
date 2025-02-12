package com.jfahey.notes;

import com.jfahey.notes.model.entity.Note;
import com.jfahey.notes.model.entity.User;
import com.jfahey.notes.repository.NoteRepository;
import com.jfahey.notes.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("demo")
public class NotesDemoTest {

    @Autowired
    private NoteRepository noteRepository;

    @Test
    @DisplayName("Test All Demo Data Successfully Loaded On Startup")
    void testDemoLoaded() {
        List<Note> notes = noteRepository.findAll();

        assertThat(notes).hasSize(1);
    }
}
