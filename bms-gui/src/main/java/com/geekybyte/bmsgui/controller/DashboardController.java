package com.geekybyte.bmsgui.controller;

import com.geekybyte.bmsgui.api.DashboardApi;
import com.geekybyte.bmsgui.core.ApiException;
import com.geekybyte.bmsgui.model.DashboardDto;
import com.geekybyte.bmsgui.model.TransactionDto;
import com.geekybyte.bmsgui.util.Alerts;
import com.geekybyte.bmsgui.util.Formatters;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController {

    @FXML
    private Label totalCustomersLabel;
    @FXML
    private Label activeCustomersLabel;
    @FXML
    private Label totalAccountsLabel;
    @FXML
    private Label accountBreakdownLabel;
    @FXML
    private Label totalBalanceLabel;
    @FXML
    private Label transactionsTodayLabel;

    @FXML
    private TableView<TransactionDto> activityTable;
    @FXML
    private TableColumn<TransactionDto, String> colReference;
    @FXML
    private TableColumn<TransactionDto, String> colType;
    @FXML
    private TableColumn<TransactionDto, String> colFrom;
    @FXML
    private TableColumn<TransactionDto, String> colTo;
    @FXML
    private TableColumn<TransactionDto, String> colAmount;
    @FXML
    private TableColumn<TransactionDto, String> colStatus;
    @FXML
    private TableColumn<TransactionDto, String> colWhen;

    private final DashboardApi dashboardApi = new DashboardApi();

    @FXML
    public void initialize() {
        colReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colFrom.setCellValueFactory(new PropertyValueFactory<>("sourceAccountNumber"));
        colTo.setCellValueFactory(new PropertyValueFactory<>("destinationAccountNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colAmount.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(Formatters.money(data.getValue().getAmount())));
        colWhen.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCreatedAt() != null
                                ? data.getValue().getCreatedAt().format(Formatters.DATE_TIME)
                                : "-"));

        onRefresh();
    }

    @FXML
    private void onRefresh() {
        Task<DashboardDto> task = new Task<>() {
            @Override
            protected DashboardDto call() {
                return dashboardApi.getSummary();
            }
        };

        task.setOnSucceeded(e -> populate(task.getValue()));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            Alerts.error("Failed to load dashboard", ex instanceof ApiException apiEx ? apiEx.getMessage() : String.valueOf(ex));
        });

        new Thread(task, "dashboard-load").start();
    }

    private void populate(DashboardDto d) {
        totalCustomersLabel.setText(String.valueOf(d.getTotalCustomers()));
        activeCustomersLabel.setText(d.getActiveCustomers() + " active");

        totalAccountsLabel.setText(String.valueOf(d.getTotalAccounts()));
        accountBreakdownLabel.setText(d.getActiveAccounts() + " active · " + d.getFrozenAccounts() + " frozen · " + d.getClosedAccounts() + " closed");

        totalBalanceLabel.setText(Formatters.money(d.getTotalBalanceHeld()));
        transactionsTodayLabel.setText(String.valueOf(d.getTransactionsToday()));

        activityTable.setItems(FXCollections.observableArrayList(
                d.getRecentActivity() != null ? d.getRecentActivity() : java.util.List.of()));
    }
}
