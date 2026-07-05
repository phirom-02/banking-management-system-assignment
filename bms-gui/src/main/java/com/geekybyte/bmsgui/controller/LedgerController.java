package com.geekybyte.bmsgui.controller;

import com.geekybyte.bmsgui.api.AccountApi;
import com.geekybyte.bmsgui.api.LedgerApi;
import com.geekybyte.bmsgui.core.ApiException;
import com.geekybyte.bmsgui.model.AccountComboMetadata;
import com.geekybyte.bmsgui.model.ComboOption;
import com.geekybyte.bmsgui.model.LedgerEntryDto;
import com.geekybyte.bmsgui.model.PageResponse;
import com.geekybyte.bmsgui.util.Alerts;
import com.geekybyte.bmsgui.util.Formatters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class LedgerController {

    @FXML
    private ComboBox<ComboOption<AccountComboMetadata>> filterAccountComboBox;
    @FXML
    private TableView<LedgerEntryDto> table;
    @FXML
    private TableColumn<LedgerEntryDto, String> colTransactionRef;
    @FXML
    private TableColumn<LedgerEntryDto, String> colAccount;
    @FXML
    private TableColumn<LedgerEntryDto, String> colEntryType;
    @FXML
    private TableColumn<LedgerEntryDto, String> colAmount;
    @FXML
    private TableColumn<LedgerEntryDto, String> colBalanceAfter;
    @FXML
    private TableColumn<LedgerEntryDto, String> colWhen;
    @FXML
    private Label pageLabel;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;

    private final LedgerApi ledgerApi = new LedgerApi();
    private final AccountApi accountApi = new AccountApi();

    private int currentPage = 0;
    private final int pageSize = 20;
    private int totalPages = 1;
    private Long filterAccountId = null;

    @FXML
    public void initialize() {
        colTransactionRef.setCellValueFactory(new PropertyValueFactory<>("transactionReference"));
        colAccount.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colEntryType.setCellValueFactory(new PropertyValueFactory<>("entryType"));

        colAmount.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(Formatters.money(data.getValue().getAmount())));
        colBalanceAfter.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(Formatters.money(data.getValue().getBalanceAfter())));
        colWhen.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCreatedAt() != null
                                ? data.getValue().getCreatedAt().format(Formatters.DATE_TIME)
                                : "-"));

        setupAccountComboBox();
        loadAccounts();
        loadPage();
    }

    private void setupAccountComboBox() {
        filterAccountComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(ComboOption<AccountComboMetadata> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item == null ? "All accounts" : item.getLabel()));
            }
        });
        filterAccountComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ComboOption<AccountComboMetadata> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "All accounts" : (item == null ? "All accounts" : item.getLabel()));
            }
        });
    }

    private void loadAccounts() {
        Task<List<ComboOption<AccountComboMetadata>>> task = new Task<>() {
            @Override
            protected List<ComboOption<AccountComboMetadata>> call() {
                return accountApi.combo(null, "ALL");
            }
        };
        task.setOnSucceeded(e -> {
            ObservableList<ComboOption<AccountComboMetadata>> items = FXCollections.observableArrayList();
            items.add(null); // "All accounts"
            items.addAll(task.getValue());
            filterAccountComboBox.setItems(items);
            filterAccountComboBox.getSelectionModel().select(null);
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            Alerts.error("Failed to load accounts", ex instanceof ApiException apiEx ? apiEx.getMessage() : String.valueOf(ex));
        });
        new Thread(task, "ledger-accounts-load").start();
    }

    @FXML
    private void onFilter() {
        ComboOption<AccountComboMetadata> selected = filterAccountComboBox.getValue();
        filterAccountId = selected != null ? selected.getMetadata().getId() : null;
        currentPage = 0;
        loadPage();
    }

    @FXML
    private void onShowAll() {
        filterAccountComboBox.getSelectionModel().select(null);
        filterAccountId = null;
        currentPage = 0;
        loadPage();
    }

    @FXML
    private void onPrevPage() {
        if (currentPage > 0) {
            currentPage--;
            loadPage();
        }
    }

    @FXML
    private void onNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadPage();
        }
    }

    private void loadPage() {
        Task<PageResponse<LedgerEntryDto>> task = new Task<>() {
            @Override
            protected PageResponse<LedgerEntryDto> call() {
                return filterAccountId != null
                        ? ledgerApi.listForAccount(filterAccountId, currentPage, pageSize)
                        : ledgerApi.listAll(currentPage, pageSize);
            }
        };

        task.setOnSucceeded(e -> {
            PageResponse<LedgerEntryDto> page = task.getValue();
            table.setItems(FXCollections.observableArrayList(page.getContent()));
            totalPages = Math.max(page.getTotalPages(), 1);
            pageLabel.setText("Page " + (currentPage + 1) + " of " + totalPages + " (" + page.getTotalElements() + " entries)");
            prevButton.setDisable(page.isFirst());
            nextButton.setDisable(page.isLast());
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            Alerts.error("Failed to load ledger", ex instanceof ApiException apiEx ? apiEx.getMessage() : String.valueOf(ex));
        });

        new Thread(task, "ledger-load").start();
    }
}
