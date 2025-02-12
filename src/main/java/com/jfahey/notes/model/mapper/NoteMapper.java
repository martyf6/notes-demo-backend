package com.jfahey.notes.model.mapper;

import com.jfahey.notes.model.api.NoteAPI;
import com.jfahey.notes.model.entity.Note;

public class NoteMapper {

    public static NoteAPI toAPI(Note note) {
        if (note == null) {
            return null;
        }

        return NoteAPI.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .lastUpdated(note.getLastUpdated())
                .created(note.getCreated())
                .build();
    }

    public static Note toEntity(NoteAPI noteApi, String userId) {
        if (noteApi == null) {
            return null;
        }

        return new Note(userId, noteApi.getTitle(), noteApi.getContent());
    }
}
