package com.finpiolt.demo.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BudgetController {

    private final JdbcTemplate jdbcTemplate;

    public BudgetController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/budgets")
    public List<Map<String, Object>> getBudgets(
            @RequestParam String user
    ) {

        String sql = """
                SELECT
                    category,
                    monthly_limit
                FROM budgets
                WHERE user_name = ?
                ORDER BY category
                """;

        return jdbcTemplate.queryForList(sql, user);
    }
}