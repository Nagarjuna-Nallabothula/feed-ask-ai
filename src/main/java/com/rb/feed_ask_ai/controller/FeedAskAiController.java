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

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeedAskAiController implements ApiApi {

    private final FeedService feedService;
    private final AskService askService;

    /**
     * Upload and process a PDF document
     */
    @Override
    public ResponseEntity<FeedUploadResponse> uploadFeed(MultipartFile file) {
        FeedUploadResponse feedUploadResponse = feedService.feedContentFrom(file);
        return ResponseEntity.ok(feedUploadResponse);
    }

    @Override
    public ResponseEntity<AskResponse> askQuestion(String prompt) {
        AskResponse answer = askService.ask(prompt);
        return ResponseEntity.ok(answer);
    }

}
