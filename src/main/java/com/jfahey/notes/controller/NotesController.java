package com.jfahey.notes.controller;

import com.jfahey.notes.exception.NoteNotFoundException;
import com.jfahey.notes.model.api.NoteAPI;
import com.jfahey.notes.model.entity.Note;
import com.jfahey.notes.model.mapper.NoteMapper;
import com.jfahey.notes.service.NoteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/notes")
@Slf4j
public class NotesController {

    private final NoteService noteService;

    public NotesController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteAPI> getNote(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable @RequestBody UUID id) {

        String userId = getUserIdFromToken(principal);
        log.info("Getting note: {} for user: {}", id, userId);

        Optional<NoteAPI> noteResponse = noteService.getNoteByUserId(id, userId)
                .map(NoteMapper::toAPI);

        return noteResponse.map(ResponseEntity.ok()::body)
                .orElseThrow(NoteNotFoundException::new);
    }

    @PostMapping
    public ResponseEntity<NoteAPI> create(
            @AuthenticationPrincipal Jwt principal,
            @RequestBody @Valid NoteAPI noteRequest) {

        String userId = getUserIdFromToken(principal);
        log.info("Creating new note {} for user: {}", noteRequest, userId);

        Note note = NoteMapper.toEntity(noteRequest, userId);
        Note savedNote = noteService.createNote(note);
        NoteAPI noteResponse = NoteMapper.toAPI(savedNote);
        URI noteURI = URI.create("/notes/" + noteResponse.getId());
        return ResponseEntity.created(noteURI).body(noteResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteAPI> updateNote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable @RequestBody UUID id,
            @RequestBody @Valid NoteAPI updateRequest) {

        String userId = getUserIdFromToken(jwt);
        log.info("Updating note: {} for user: {}", updateRequest, userId);

        Note note = NoteMapper.toEntity(updateRequest, userId);
        NoteAPI updateResponse = NoteMapper.toAPI(noteService.updateNote(note));
        return ResponseEntity.ok(updateResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable @RequestBody UUID id) {

        String userId = getUserIdFromToken(jwt);
        log.info("Deleting note: {} for user: {}", id, userId);

        noteService.deleteNoteByUserId(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping({"", "/", "/all"})
    public ResponseEntity<List<NoteAPI>> list(
            @AuthenticationPrincipal Jwt principal
    ) {

        String userId = getUserIdFromToken(principal);
        log.info("Getting all notes for user: {}", userId);
        List<NoteAPI> notesResponse = noteService.getNotesByUserId(userId).stream()
                .map(NoteMapper::toAPI)
                .toList();

        return ResponseEntity.ok(notesResponse);
    }

    private static final String USER_ID_CLAIM = "sub";

    private String getUserIdFromToken(Jwt accessToken) {
        if (accessToken == null) {
            throw new RuntimeException("No Valid Access Token.");
        }

        String userId = accessToken.getClaimAsString(USER_ID_CLAIM);
        if (StringUtils.hasText(userId)) {
            return userId;
        } else {
            throw new RuntimeException("Invalid Access Token.");
        }
    }
}
