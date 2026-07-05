package com.geekybyte.bmsgui.model;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDto {
    private long totalCustomers;
    private long activeCustomers;
    private long totalAccounts;
    private long activeAccounts;
    private long frozenAccounts;
    private long closedAccounts;
    private BigDecimal totalBalanceHeld;
    private long transactionsToday;
    private List<TransactionDto> recentActivity;

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

    public List<TransactionDto> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<TransactionDto> recentActivity) {
        this.recentActivity = recentActivity;
    }
}
