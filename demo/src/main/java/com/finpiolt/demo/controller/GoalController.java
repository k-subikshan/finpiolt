package com.finpiolt.demo.controller;



import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GoalController {

    private final JdbcTemplate jdbcTemplate;

    public GoalController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/goals")
    public List<Map<String, Object>> getGoals(
            @RequestParam String user
    ) {

        String sql = """
                SELECT
                    id,
                    goal_name,
                    target_amount,
                    current_amount,
                    target_date
                FROM goals
                WHERE user_name = ?
                ORDER BY target_date
                """;

        return jdbcTemplate.queryForList(sql, user);
    }
}
