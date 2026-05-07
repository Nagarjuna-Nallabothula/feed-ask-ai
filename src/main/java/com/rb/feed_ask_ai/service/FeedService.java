package com.rb.feed_ask_ai.service;

import com.rb.feed_ask_ai.entity.DocumentEntity;
import com.rb.feed_ask_ai.entity.RagEntity;
import com.rb.feed_ask_ai.model.FeedUploadResponse;
import com.rb.feed_ask_ai.model.FeedUploadResponseData;
import com.rb.feed_ask_ai.model.RagChunk;
import com.rb.feed_ask_ai.repository.DocRepository;
import com.rb.feed_ask_ai.repository.RagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling PDF document upload and processing.
 * Manages the complete lifecycle of document ingestion including chunking,
 * embedding, and database persistence.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final PdfSemanticChunkingService pdfSemanticChunkingService;
    private final RagRepository ragRepository;
    private final DocRepository docRepository;
    private final ObjectMapper objectMapper;

    /**
     * Processes a PDF file and stores it with embeddings.
     *
     * @param file the PDF file to process
     * @return a response containing upload details
     */
    public FeedUploadResponse feedContentFrom(MultipartFile file) {
        OffsetDateTime startedProcessingAt = OffsetDateTime.now();
        DocumentEntity documentEntity = new DocumentEntity();
        processDocumentForPendingStatus(file, documentEntity);
        documentEntity = docRepository.save(documentEntity);
        List<RagChunk> chunks = pdfSemanticChunkingService.ingestPdfAndGenerateEmbeddings(file, documentEntity.getId());
        processDocumentForProcessingStatus(chunks.size(), documentEntity);
        documentEntity = docRepository.save(documentEntity);
        List<RagEntity> ragEntities = mapRagEntities(chunks, documentEntity.getId());
        ragRepository.saveAll(ragEntities);
        processDocumentForCompletedStatus(documentEntity, startedProcessingAt);
        documentEntity = docRepository.save(documentEntity);
        return getFeedUploadResponse(documentEntity);
    }

    private void processDocumentForCompletedStatus(DocumentEntity documentEntity, OffsetDateTime startedProcessingAt) {
        documentEntity.setStatus(DocumentEntity.ProcessingStatus.COMPLETED);
        documentEntity.setUpdatedAt(OffsetDateTime.now());
        documentEntity.setProcessingTimeMs(OffsetDateTime.now().toEpochSecond() - startedProcessingAt.toEpochSecond());
    }

    private void processDocumentForProcessingStatus(Integer chunksCount, DocumentEntity documentEntity) {
        documentEntity.setChunksCount(chunksCount);
        documentEntity.setPageCount(pdfSemanticChunkingService.getNumberOfPages());
        documentEntity.setStatus(DocumentEntity.ProcessingStatus.PROCESSING);
        documentEntity.setUpdatedAt(OffsetDateTime.now());
    }

    private void processDocumentForPendingStatus(MultipartFile file, DocumentEntity documentEntity) {
        documentEntity.setDocumentName(file.getOriginalFilename());
        documentEntity.setFileHash(calculateFileHash(file));
        documentEntity.setUploadedAt(OffsetDateTime.now());
        documentEntity.setStatus(DocumentEntity.ProcessingStatus.PENDING);
    }

    private List<RagEntity> mapRagEntities(List<RagChunk> chunks, Long documentId) {
        List<RagEntity> ragEntities = new ArrayList<>();
        for (RagChunk ragChunk : chunks) {
            final RagEntity ragEntity = new RagEntity();
            ragEntity.setContent(ragChunk.getContent());
            ragEntity.setDocumentId(documentId);
            ragEntity.setEmbedding(objectMapper.writeValueAsString(ragChunk.getEmbedding()));
            ragEntity.setCreatedAt(OffsetDateTime.now());
            ragEntity.setUpdatedAt(OffsetDateTime.now());
            ragEntities.add(ragEntity);
        }
        return ragEntities;
    }

    private FeedUploadResponse getFeedUploadResponse(DocumentEntity documentEntity) {
        FeedUploadResponseData data = new FeedUploadResponseData();
        data.setDocumentId(documentEntity.getId());
        data.setPageCount(documentEntity.getPageCount());
        data.setEmbeddingsCount(documentEntity.getChunksCount());
        data.setProcessingTimeMs(documentEntity.getProcessingTimeMs());
        data.setDocumentName(documentEntity.getDocumentName());
        data.setUploadedAt(documentEntity.getUploadedAt());
        FeedUploadResponse feedUploadResponse = new FeedUploadResponse();
        feedUploadResponse.setStatus(FeedUploadResponse.StatusEnum.SUCCESS);
        feedUploadResponse.setMessage("Document uploaded and processed successfully");
        feedUploadResponse.setData(data);
        return feedUploadResponse;
    }


    /**
     * Calculate SHA-256 hash of file for deduplication
     */
    private String calculateFileHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(file.getBytes());
            return bytesToHex(encodedhash);
        } catch (Exception e) {
            log.warn("Could not calculate file hash: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Convert bytes to hex string
     */
    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
