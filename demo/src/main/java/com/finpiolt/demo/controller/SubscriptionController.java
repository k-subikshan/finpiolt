package com.finpiolt.demo.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private final JdbcTemplate jdbcTemplate;

    public SubscriptionController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/subscriptions")
    public List<Map<String, Object>> getSubscriptions(
            @RequestParam String user
    ) {

        String sql = """
                SELECT
                    id,
                    merchant,
                    amount,
                    frequency,
                    type,
                    confidence,
                    detected_at
                FROM subscriptions
                WHERE user_name = ?
                ORDER BY amount DESC
                """;

        return jdbcTemplate.queryForList(sql, "subi");
    }
}