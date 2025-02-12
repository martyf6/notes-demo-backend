package com.jfahey.notes.repository;

import com.jfahey.notes.model.entity.Note;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @AfterEach
    void afterEach() {
        noteRepository.deleteAll();
    }

    @Test
    @DisplayName("Given a Saved Note, Find Note By User ID Successful")
    public void whenSaved_thenFindByUserId() {
        Note note = new Note("user123", "My Title", "My Content");
        Note savedNote = noteRepository.save(note);

        assertThat(savedNote).isNotNull();
        assertThat(savedNote.getId()).isNotNull();

        assertThat(noteRepository.findByIdAndUserId(savedNote.getId(), "user123"))
                .isPresent();
    }

    @Test
    @DisplayName("Given No Saved Notes, Find Note By User ID Empty")
    public void whenNoneSaved_thenFindByUserIdEmpty() {
        UUID noteId = UUID.randomUUID();
        assertThat(noteRepository.findByIdAndUserId(noteId, "user123"))
                .isEmpty();
    }

    @Test
    @DisplayName("Given a Saved Note, Find All By User ID Successful")
    public void whenSaved_thenFindAllByUserId() {
        Note note = new Note("user123", "My Title", "My Content");
        Note savedNote = noteRepository.save(note);

        assertThat(savedNote).isNotNull();
        assertThat(savedNote.getId()).isNotNull();

        assertThat(noteRepository.findByUserId("user123")).hasSize(1);
    }

    @Test
    @DisplayName("Given No Saved Notes, Find All By User ID Empty")
    public void whenNoneSaved_thenFindAllByUserIdEmpty() {
        assertThat(noteRepository.findByUserId("user123")).hasSize(0);
    }
}