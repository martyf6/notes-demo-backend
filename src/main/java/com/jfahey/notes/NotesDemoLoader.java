package com.jfahey.notes;

import com.jfahey.notes.model.entity.Note;
import com.jfahey.notes.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("demo")
public class NotesDemoLoader implements CommandLineRunner {

    private final NoteRepository noteRepository;

    public NotesDemoLoader(NoteRepository noteRepository) {

        this.noteRepository = noteRepository;
    }

    @Override
    public void run(String... strings) throws Exception {
        noteRepository.save(
                new Note("admin123", "Welcome Greeting", "Hello! This is a note."));
    }
}
