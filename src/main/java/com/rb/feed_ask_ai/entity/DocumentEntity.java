package com.rb.feed_ask_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Document entity for storing PDF document metadata.
 * Tracks document details, processing status, and deduplication information.
 */
@Entity(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEntity {

    /**
     * Unique identifier for the document.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the document.
     */
    @Column(nullable = false, unique = true)
    private String documentName;

    /**
     * Number of pages in the document.
     */
    @Column
    private Integer pageCount;

    /**
     * Number of chunks created from the document.
     */
    @Column
    private Integer chunksCount;

    /**
     * Time taken to process the document in milliseconds.
     */
    @Column
    private Long processingTimeMs;

    /**
     * Timestamp when the document was uploaded.
     */
    @Column(nullable = false, updatable = false)
    private OffsetDateTime uploadedAt;

    /**
     * Timestamp when the document was last updated.
     */
    @Column
    private OffsetDateTime updatedAt;

    /**
     * SHA-256 hash of the file for deduplication.
     */
    @Column(length = 500, unique = true)
    private String fileHash;

    /**
     * Current processing status of the document.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessingStatus status = ProcessingStatus.PENDING;

    /**
     * Processing status enumeration for document lifecycle.
     */
    public enum ProcessingStatus {
        /** Document is pending processing. */
        PENDING,
        /** Document is currently being processed. */
        PROCESSING,
        /** Document processing is completed. */
        COMPLETED,
        /** Document processing failed. */
        FAILED
    }

}
