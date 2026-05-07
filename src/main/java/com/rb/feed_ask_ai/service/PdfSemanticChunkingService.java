package com.rb.feed_ask_ai.service;

import com.rb.feed_ask_ai.model.RagChunk;
import lombok.Getter;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service for semantic chunking of PDF documents with embedding generation.
 * Implements production-level semantic chunking that respects document structure
 * and generates embeddings for each chunk.
 */
@Service
public class PdfSemanticChunkingService {

    private static final int MAX_TOKENS = 350;
    private static final int OVERLAP_TOKENS = 60;
    @Getter
    private Integer numberOfPages = 0;

    private final EmbeddingModel embeddingModel;

    /**
     * Constructor for PdfSemanticChunkingService.
     *
     * @param embeddingModel the embedding model for generating chunk embeddings
     */
    public PdfSemanticChunkingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * Ingests a PDF file, performs semantic chunking, and generates embeddings.
     *
     * @param file the PDF file to process
     * @param documentId the ID of the document being processed
     * @return a list of RAG chunks with generated embeddings
     */
    public List<RagChunk> ingestPdfAndGenerateEmbeddings(
            MultipartFile file, Long documentId) {

        String text = extractText(file);
        List<String> semanticUnits = splitSemantically(text);
        List<String> chunks = buildTokenAwareChunks(semanticUnits);

        List<RagChunk> result = new ArrayList<>();

        for (String chunk : chunks) {
            float[] embed = embeddingModel.embed(chunk);
            List<Float> vector = new ArrayList<>();
            for (float f : embed) {
                vector.add(f);
            }
            result.add(new RagChunk(
                    documentId,
                    chunk,
                    vector
            ));
        }

        return result;
    }

    /**
     * Extracts text from a PDF file.
     *
     * @param file the PDF file to extract text from
     * @return the extracted text
     */
    private String extractText(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getInputStream().readAllBytes())) {
            numberOfPages = document.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            return stripper.getText(document)
                    .replaceAll("\\s+", " ")
                    .trim();

        } catch (Exception e) {
            throw new RuntimeException("PDF processing failed", e);
        }
    }

    /**
     * Splits text into semantic units respecting document structure.
     *
     * @param text the text to split semantically
     * @return a list of semantic units
     */
    private List<String> splitSemantically(String text) {

        List<String> units = new ArrayList<>();

        // Split by headings or logical breaks
        String[] sections = text.split("(?=\\n[A-Z][A-Za-z\\s]{3,}\\n)");

        for (String section : sections) {

            String[] paragraphs = section.split("\\n\\n+");

            for (String paragraph : paragraphs) {
                if (estimateTokens(paragraph) <= MAX_TOKENS) {
                    units.add(paragraph.trim());
                } else {
                    // fallback to sentence split
                    units.addAll(
                            Arrays.asList(paragraph.split("(?<=[.!?])\\s+"))
                    );
                }
            }
        }
        return units;
    }

    /**
     * Builds token-aware chunks from semantic units with overlap.
     *
     * @param units the semantic units to chunk
     * @return a list of token-aware chunks with overlap
     */
    private List<String> buildTokenAwareChunks(List<String> units) {

        List<String> chunks = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        int tokenCount = 0;

        for (String unit : units) {

            int tokens = estimateTokens(unit);

            if (tokenCount + tokens <= MAX_TOKENS) {
                buffer.append(unit).append(" ");
                tokenCount += tokens;
            } else {
                chunks.add(buffer.toString().trim());

                String overlap = lastNTokens(buffer.toString(), OVERLAP_TOKENS);
                buffer = new StringBuilder(overlap).append(" ").append(unit).append(" ");
                tokenCount = estimateTokens(buffer.toString());
            }
        }

        if (!buffer.isEmpty()) {
            chunks.add(buffer.toString().trim());
        }

        return chunks;
    }

    /**
     * Estimates the number of tokens in text.
     *
     * @param text the text to estimate tokens for
     * @return the estimated token count
     */
    private int estimateTokens(String text) {
        return text.split("\\s+").length;
    }

    /**
     * Extracts the last N tokens from text.
     *
     * @param text the text to extract from
     * @param tokens the number of tokens to extract
     * @return the last N tokens joined by spaces
     */
    private String lastNTokens(String text, int tokens) {
        String[] words = text.split("\\s+");
        int start = Math.max(words.length - tokens, 0);
        return String.join(" ", Arrays.copyOfRange(words, start, words.length));
    }
}
