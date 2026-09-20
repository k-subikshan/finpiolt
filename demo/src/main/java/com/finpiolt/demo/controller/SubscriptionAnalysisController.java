package com.finpiolt.demo.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionAnalysisController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String n8nUrl =
            "http://localhost:5678/webhook/finpilot/subscription";

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeSubscriptions(
            @RequestBody Map<String, Object> request
    ) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(request, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            n8nUrl,
                            entity,
                            String.class
                    );

            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error",
                            "Unable to connect to Subscription Detection workflow",
                            "message",
                            e.getMessage()
                    ));
        }
    }
}