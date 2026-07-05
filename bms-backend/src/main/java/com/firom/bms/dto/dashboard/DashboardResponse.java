package com.firom.bms.dto.dashboard;

import com.firom.bms.dto.transaction.TransactionResponse;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("all")
public class DashboardResponse {
    private long totalCustomers;
    private long activeCustomers;
    private long totalAccounts;
    private long activeAccounts;
    private long frozenAccounts;
    private long closedAccounts;
    private BigDecimal totalBalanceHeld;
    private long transactionsToday;
    private List<TransactionResponse> recentActivity;

    public DashboardResponse() {
    }

    public DashboardResponse(long totalCustomers, long activeCustomers, long totalAccounts, long activeAccounts, long frozenAccounts, long closedAccounts, BigDecimal totalBalanceHeld, long transactionsToday, List<TransactionResponse> recentActivity) {
        this.totalCustomers = totalCustomers;
        this.activeCustomers = activeCustomers;
        this.totalAccounts = totalAccounts;
        this.activeAccounts = activeAccounts;
        this.frozenAccounts = frozenAccounts;
        this.closedAccounts = closedAccounts;
        this.totalBalanceHeld = totalBalanceHeld;
        this.transactionsToday = transactionsToday;
        this.recentActivity = recentActivity;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getActiveCustomers() {
        return activeCustomers;
    }

    public void setActiveCustomers(long activeCustomers) {
        this.activeCustomers = activeCustomers;
    }

    public long getTotalAccounts() {
        return totalAccounts;
    }

    public void setTotalAccounts(long totalAccounts) {
        this.totalAccounts = totalAccounts;
    }

    public long getActiveAccounts() {
        return activeAccounts;
    }

    public void setActiveAccounts(long activeAccounts) {
        this.activeAccounts = activeAccounts;
    }

    public long getFrozenAccounts() {
        return frozenAccounts;
    }

    public void setFrozenAccounts(long frozenAccounts) {
        this.frozenAccounts = frozenAccounts;
    }

    public long getClosedAccounts() {
        return closedAccounts;
    }

    public void setClosedAccounts(long closedAccounts) {
        this.closedAccounts = closedAccounts;
    }

    public BigDecimal getTotalBalanceHeld() {
        return totalBalanceHeld;
    }

    public void setTotalBalanceHeld(BigDecimal totalBalanceHeld) {
        this.totalBalanceHeld = totalBalanceHeld;
    }

    public long getTransactionsToday() {
        return transactionsToday;
    }

    public void setTransactionsToday(long transactionsToday) {
        this.transactionsToday = transactionsToday;
    }

    public List<TransactionResponse> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<TransactionResponse> recentActivity) {
        this.recentActivity = recentActivity;
    }
}
