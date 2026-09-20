package com.finpiolt.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal savings;
    private int transactionCount;
    private List<CategorySpending> categoryBreakdown;

    public DashboardResponse() {
    }

    public DashboardResponse(
            BigDecimal totalIncome,
            BigDecimal totalExpenses,
            BigDecimal savings,
            int transactionCount,
            List<CategorySpending> categoryBreakdown
    ) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.savings = savings;
        this.transactionCount = transactionCount;
        this.categoryBreakdown = categoryBreakdown;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public void setSavings(BigDecimal savings) {
        this.savings = savings;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public List<CategorySpending> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(
            List<CategorySpending> categoryBreakdown
    ) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public static class CategorySpending {

        private String category;
        private BigDecimal amount;

        public CategorySpending() {
        }

        public CategorySpending(
                String category,
                BigDecimal amount
        ) {
            this.category = category;
            this.amount = amount;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}