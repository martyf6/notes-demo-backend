package com.jfahey.notes.model.api;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteAPI {
    private UUID id;

    private String title;

    private String content;

    private OffsetDateTime lastUpdated;

    private OffsetDateTime created;
}
