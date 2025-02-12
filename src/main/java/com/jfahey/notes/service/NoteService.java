package com.jfahey.notes.service;

import com.jfahey.notes.exception.NoteNotFoundException;
import com.jfahey.notes.model.entity.Note;
import com.jfahey.notes.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository){
        this.noteRepository = noteRepository;
    }

    public Optional<Note> getNoteByUserId(UUID noteId, String userId) {
        return noteRepository.findByIdAndUserId(noteId, userId);
    }

    public List<Note> getNotesByUserId(String userId) {
        return noteRepository.findByUserId(userId);
    }

    public Note createNote(Note note) {
        return noteRepository.save(note);
    }

    public Note updateNote(Note note) {
        Optional<Note> existingNote = noteRepository.findByIdAndUserId(note.getId(), note.getUserId());
        if (existingNote.isEmpty()) {
            throw new NoteNotFoundException();
        }

        return noteRepository.save(note);
    }

    public void deleteNoteByUserId(UUID noteId, String userId) {
        Optional<Note> existingNote = noteRepository.findByIdAndUserId(noteId, userId);
        if (existingNote.isEmpty()) {
            throw new NoteNotFoundException();
        }
        noteRepository.deleteById(noteId);
    }
}
