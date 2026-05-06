package com.rb.feed_ask_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//

    @Column(nullable = false, unique = true)
    private String documentName;//

    @Column
    private Integer pageCount;//

    @Column
    private Integer chunksCount;//

    @Column
    private Long processingTimeMs;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime uploadedAt;//

    @Column
    private OffsetDateTime updatedAt;

    @Column(length = 500, unique = true)
    private String fileHash; // For deduplication

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessingStatus status = ProcessingStatus.PENDING;

    public enum ProcessingStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }

}
