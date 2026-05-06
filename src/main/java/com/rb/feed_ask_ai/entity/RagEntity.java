package com.rb.feed_ask_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Entity(name = "chunks")
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_document_id", columnList = "document_id"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
public class RagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long documentId; //

    @Lob
    @Column(nullable = false, length = 20000) // Adjust length as needed
    private String content; //

    @Column(columnDefinition = "json")
    private String embedding; // Stored as JSON array

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

}
