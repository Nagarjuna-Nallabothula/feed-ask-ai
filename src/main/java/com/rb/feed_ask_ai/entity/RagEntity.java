package com.rb.feed_ask_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * RAG chunk entity for storing semantic chunks of documents.
 * Each chunk contains content and its embedding representation.
 */
@Data
@Entity(name = "chunks")
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_document_id", columnList = "document_id"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
public class RagEntity {

    /**
     * Unique identifier for the chunk.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to the parent document ID.
     */
    @Column(nullable = false)
    private Long documentId;

    /**
     * Semantic content of the chunk.
     */
    @Lob
    @Column(nullable = false, length = 20000)
    private String content;

    /**
     * Embedding vector stored as JSON array.
     */
    @Column(columnDefinition = "json")
    private String embedding;

    /**
     * Timestamp when the chunk was created.
     */
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Timestamp when the chunk was last updated.
     */
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

}
