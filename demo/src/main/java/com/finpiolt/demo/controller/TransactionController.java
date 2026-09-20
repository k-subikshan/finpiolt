package com.finpiolt.demo.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final JdbcTemplate jdbcTemplate;

    public TransactionController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/transactions")
    public List<Map<String, Object>> getTransactions(
            @RequestParam String user
    ) {

        String sql = """
                SELECT
                    id,
                    transaction_date,
                    merchant,
                    amount,
                    transaction_type,
                    category,
                    subcategory
                FROM transactions
                WHERE user_name = ?
                ORDER BY transaction_date DESC
                """;

        return jdbcTemplate.queryForList(sql, user);
    }
}