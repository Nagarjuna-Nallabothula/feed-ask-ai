package com.rb.feed_ask_ai.service;

import com.rb.feed_ask_ai.model.RagChunk;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RagContextRetrievalService {

    private final EmbeddingModel embeddingModel;

    public RagContextRetrievalService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * ✅ Retrieves Top‑K relevant chunks
     */
    public List<RagChunk> retrieveRelevantContext(
            String query,
            List<RagChunk> storedChunks,
            int topK) {

        float[] embed = embeddingModel.embed(query);
        List<Float> queryVector = new ArrayList<>();
        for (float f : embed) queryVector.add(f);

        return storedChunks.stream()
                .map(chunk -> Map.entry(
                        chunk,
                        cosineSimilarity(queryVector, chunk.getEmbedding())))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topK)
                .map(Map.Entry::getKey)
                .toList();
    }

    private double cosineSimilarity(List<Float> v1, List<Float> v2) {

        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;

        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            norm1 += Math.pow(v1.get(i), 2);
            norm2 += Math.pow(v2.get(i), 2);
        }

        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}