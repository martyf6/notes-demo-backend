package com.jfahey.notes.repository;

import com.jfahey.notes.model.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {

    Optional<Note> findByIdAndUserId(UUID id, String userId);

    List<Note> findByUserId(String userId);

}
