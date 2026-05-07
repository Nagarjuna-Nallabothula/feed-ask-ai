package com.rb.feed_ask_ai.controller;

import com.rb.feed_ask_ai.api.ApiApi;
import com.rb.feed_ask_ai.model.AskResponse;
import com.rb.feed_ask_ai.model.FeedUploadResponse;
import com.rb.feed_ask_ai.service.AskService;
import com.rb.feed_ask_ai.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for Feed Ask AI API endpoints.
 * Handles document upload and question answering operations.
 */
@RestController
@RequiredArgsConstructor
public class FeedAskAiController implements ApiApi {

    private final FeedService feedService;
    private final AskService askService;

    /**
     * Uploads and processes a PDF document.
     *
     * @param file the PDF file to upload
     * @return response containing upload details
     */
    @Override
    public ResponseEntity<FeedUploadResponse> uploadFeed(MultipartFile file) {
        FeedUploadResponse feedUploadResponse = feedService.feedContentFrom(file);
        return ResponseEntity.ok(feedUploadResponse);
    }

    /**
     * Answers a question based on uploaded documents.
     *
     * @param prompt the question to answer
     * @return response containing the answer
     */
    @Override
    public ResponseEntity<AskResponse> askQuestion(String prompt) {
        AskResponse answer = askService.ask(prompt);
        return ResponseEntity.ok(answer);
    }

}
