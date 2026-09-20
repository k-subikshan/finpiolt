package com.finpiolt.demo.controller;

import org.springframework.http.ResponseEntity;
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
                    b.id,
                    b.category,
                    b.monthly_limit,
                    COALESCE(SUM(
                        CASE
                            WHEN t.transaction_type = 'expense'
                            THEN t.amount
                            ELSE 0
                        END
                    ), 0) AS spent
                FROM budgets b
                LEFT JOIN transactions t
                    ON t.user_name = b.user_name
                    AND t.category = b.category
                WHERE b.user_name = ?
                GROUP BY
                    b.id,
                    b.category,
                    b.monthly_limit
                ORDER BY b.category
                """;

        return jdbcTemplate.queryForList(sql, user);
    }

    @PutMapping("/budgets/{id}")
    public ResponseEntity<?> updateBudget(
            @PathVariable int id,
            @RequestBody Map<String, Object> request
    ) {

        Object limitValue = request.get("monthly_limit");

        if (limitValue == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            "monthly_limit is required"
                    ));
        }

        double monthlyLimit;

        try {
            monthlyLimit = Double.parseDouble(
                    limitValue.toString()
            );
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            "monthly_limit must be a number"
                    ));
        }

        if (monthlyLimit < 0) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            "Budget cannot be negative"
                    ));
        }

        String sql = """
                UPDATE budgets
                SET monthly_limit = ?
                WHERE id = ?
                """;

        int updated = jdbcTemplate.update(
                sql,
                monthlyLimit,
                id
        );

        if (updated == 0) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Budget updated successfully"
                )
        );
    }
}