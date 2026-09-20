package com.finpiolt.demo.controller;


import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    private final RestTemplate restTemplate = new RestTemplate();

    // n8n Workflow 6 webhook
    private final String n8nUrl =
        "http://localhost:5678/webhook/finpilot/ask";

    @PostMapping("/ask")
    public ResponseEntity<?> askFinance(
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
                            "Unable to connect to FinPilot AI workflow",
                            "message",
                            e.getMessage()
                    ));
        }
    }
}