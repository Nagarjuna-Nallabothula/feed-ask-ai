package com.rb.feed_ask_ai.service;

import com.rb.feed_ask_ai.entity.DocumentEntity;
import com.rb.feed_ask_ai.entity.RagEntity;
import com.rb.feed_ask_ai.model.AskResponse;
import com.rb.feed_ask_ai.model.RagChunk;
import com.rb.feed_ask_ai.repository.DocRepository;
import com.rb.feed_ask_ai.repository.RagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for answering questions based on stored document context.
 * Retrieves relevant chunks using semantic similarity and generates
 * answers using the LLM.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AskService {

    private final ChatClient ollamaChatClient;
    private final RagContextRetrievalService ragContextRetrievalService;
    private final RagRepository ragRepository;
    private final DocRepository docRepository;
    private final ObjectMapper objectMapper;
    @Value("${top.match.max-results:3}")
    private Integer topK;

    /**
     * Answers a question using stored document context.
     *
     * @param prompt the question to answer
     * @return a response containing the answer and sources
     */
    public AskResponse ask(String prompt) {
        List<RagChunk> allChunks = ragRepository.findAll().stream()
                .map(this::convertRagEntityToChunk)
                .toList();
        List<RagChunk> topKRagChunks = ragContextRetrievalService.retrieveRelevantContext(prompt, allChunks, topK);
        List<String> documentNames = getDocumentsOfContext(topKRagChunks);
        String context = getContextFrom(topKRagChunks);
        String completePrompt = formPrompt(prompt, context);
        log.info("Complete Prompt sent to ChatClient: {}", completePrompt);
        String answerFromChatClient = ollamaChatClient.prompt(completePrompt)
                .call()
                .content();
        return getAskResponse(prompt, answerFromChatClient, documentNames);
    }

    private RagChunk convertRagEntityToChunk(RagEntity entity) {
        try {
            RagChunk ragChunk = new RagChunk();
            ragChunk.setDocumentId(entity.getDocumentId());
            ragChunk.setContent(entity.getContent());

            // Manually deserialize the embedding JSON string to List<Float>
            if (entity.getEmbedding() != null && !entity.getEmbedding().isEmpty()) {
                List<Float> embedding = objectMapper.readValue(entity.getEmbedding(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Float.class));
                ragChunk.setEmbedding(embedding);
            } else {
                ragChunk.setEmbedding(new ArrayList<>());
            }

            return ragChunk;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert RagEntity to RagChunk", e);
        }
    }

    private List<String> getDocumentsOfContext(List<RagChunk> topKRagChunks) {
        List<Long> documentIds = topKRagChunks.stream().map(RagChunk::getDocumentId).distinct().toList();
        return documentIds.stream()
                .map(id -> {
                    DocumentEntity document = docRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found for id: " + id));
                    return document.getDocumentName();
                })
                .toList();
    }

    private AskResponse getAskResponse(String prompt, String answerFromChatClient, List<String> documentNames) {
        AskResponse askResponse = new AskResponse();
        askResponse.setStatus(AskResponse.StatusEnum.SUCCESS);
        askResponse.setPrompt(prompt);
        askResponse.setAnswer(answerFromChatClient);
        askResponse.setReferredFrom(documentNames);
        return askResponse;
    }

    private String formPrompt(String prompt, String context) {
        return """
                You are an assistant that answers questions based on the provided context.
                If the answer is not found in the context, say "I don't know".
                
                Context: %s
                
                Question: %s
                """.formatted(context, prompt);
    }

    private String getContextFrom(List<RagChunk> topKRagChunks) {
        return topKRagChunks.stream().map(RagChunk::getContent).collect(Collectors.joining(" "));
    }
}
