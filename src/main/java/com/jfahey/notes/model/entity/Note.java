package com.jfahey.notes.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "note")
@Data
@NoArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "title", nullable = false, length = 256)
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private OffsetDateTime created;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;

    /*
    to be added in the future...
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    */

    public Note(String userId, String title, String content){
        this.userId = userId;
        this.title = title;
        this.content = content;
    }
}
