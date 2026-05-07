package com.rb.feed_ask_ai.service;

import com.rb.feed_ask_ai.model.RagChunk;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for retrieving relevant context using semantic similarity.
 * Uses cosine similarity on embedding vectors to find top-K relevant chunks.
 */
@Service
public class RagContextRetrievalService {

    private final EmbeddingModel embeddingModel;

    /**
     * Constructor for RagContextRetrievalService.
     *
     * @param embeddingModel the embedding model used for generating embeddings
     */
    public RagContextRetrievalService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * Retrieves the top-K relevant chunks based on query similarity.
     *
     * @param query the query string to find similar chunks for
     * @param storedChunks the list of all stored chunks to search from
     * @param topK the number of top results to return
     * @return a list of the top-K most similar chunks
     */
    public List<RagChunk> retrieveRelevantContext(
            String query,
            List<RagChunk> storedChunks,
            int topK) {

        float[] embed = embeddingModel.embed(query);
        List<Float> queryVector = new ArrayList<>();
        for (float f : embed) {
            queryVector.add(f);
        }

        return storedChunks.stream()
                .map(chunk -> Map.entry(
                        chunk,
                        cosineSimilarity(queryVector, chunk.getEmbedding())))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topK)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Calculates the cosine similarity between two vectors.
     *
     * @param v1 the first vector
     * @param v2 the second vector
     * @return the cosine similarity score between -1 and 1
     */
    private double cosineSimilarity(List<Float> v1, List<Float> v2) {
        double dot = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            norm1 += Math.pow(v1.get(i), 2);
            norm2 += Math.pow(v2.get(i), 2);
        }

        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}