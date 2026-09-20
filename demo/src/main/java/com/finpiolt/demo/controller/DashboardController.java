package com.finpiolt.demo.controller;

import com.finpiolt.demo.dto.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final JdbcTemplate jdbcTemplate;

    public DashboardController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard(
            @RequestParam String user,
            @RequestParam String month
    ) {

        String incomeSql = """
                SELECT COALESCE(SUM(amount), 0)
                FROM transactions
                WHERE user_name = ?
                AND transaction_type = 'income'
                AND transaction_date >= ?
                AND transaction_date < DATE_ADD(?, INTERVAL 1 MONTH)
                """;

        BigDecimal income =
                jdbcTemplate.queryForObject(
                        incomeSql,
                        BigDecimal.class,
                        user,
                        month + "-01",
                        month + "-01"
                );


        String expenseSql = """
                SELECT COALESCE(SUM(amount), 0)
                FROM transactions
                WHERE user_name = ?
                AND transaction_type = 'expense'
                AND transaction_date >= ?
                AND transaction_date < DATE_ADD(?, INTERVAL 1 MONTH)
                """;

        BigDecimal expenses =
                jdbcTemplate.queryForObject(
                        expenseSql,
                        BigDecimal.class,
                        user,
                        month + "-01",
                        month + "-01"
                );


        String countSql = """
                SELECT COUNT(*)
                FROM transactions
                WHERE user_name = ?
                AND transaction_date >= ?
                AND transaction_date < DATE_ADD(?, INTERVAL 1 MONTH)
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        countSql,
                        Integer.class,
                        user,
                        month + "-01",
                        month + "-01"
                );


        String categorySql = """
                SELECT
                    category,
                    COALESCE(SUM(amount), 0) AS amount
                FROM transactions
                WHERE user_name = ?
                AND transaction_type = 'expense'
                AND transaction_date >= ?
                AND transaction_date < DATE_ADD(?, INTERVAL 1 MONTH)
                GROUP BY category
                ORDER BY amount DESC
                """;

        List<DashboardResponse.CategorySpending> categories =
                jdbcTemplate.query(
                        categorySql,
                        (rs, rowNum) ->
                                new DashboardResponse.CategorySpending(
                                        rs.getString("category"),
                                        rs.getBigDecimal("amount")
                                ),
                        user,
                        month + "-01",
                        month + "-01"
                );


        BigDecimal savings =
                income.subtract(expenses);


        return new DashboardResponse(
                income,
                expenses,
                savings,
                count,
                categories
        );
    }
}
