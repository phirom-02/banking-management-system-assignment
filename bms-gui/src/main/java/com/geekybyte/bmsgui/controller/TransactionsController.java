package com.geekybyte.bmsgui.controller;

import com.geekybyte.bmsgui.api.AccountApi;
import com.geekybyte.bmsgui.api.TransactionApi;
import com.geekybyte.bmsgui.core.ApiException;
import com.geekybyte.bmsgui.model.*;
import com.geekybyte.bmsgui.util.Alerts;
import com.geekybyte.bmsgui.util.Formatters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.List;

public class TransactionsController {

    // Deposit
    @FXML
    private ComboBox<ComboOption<AccountComboMetadata>> depositAccountComboBox;
    @FXML
    private TextField depositAmountField;
    @FXML
    private TextField depositDescField;

    // Withdrawal
    @FXML
    private ComboBox<ComboOption<AccountComboMetadata>> withdrawAccountComboBox;
    @FXML
    private TextField withdrawAmountField;
    @FXML
    private TextField withdrawDescField;

    // Transfer
    @FXML
    private ComboBox<ComboOption<AccountComboMetadata>> transferSourceComboBox;
    @FXML
    private ComboBox<ComboOption<AccountComboMetadata>> transferDestComboBox;
    @FXML
    private TextField transferAmountField;
    @FXML
    private TextField transferDescField;

    // History
    @FXML
    private ComboBox<ComboOption<AccountComboMetadata>> filterAccountComboBox;
    @FXML
    private TableView<TransactionDto> table;
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
    private TableColumn<TransactionDto, String> colPerformedBy;
    @FXML
    private TableColumn<TransactionDto, String> colWhen;
    @FXML
    private Label pageLabel;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;

    private final TransactionApi transactionApi = new TransactionApi();
    private final AccountApi accountApi = new AccountApi();

    private int currentPage = 0;
    private final int pageSize = 15;
    private int totalPages = 1;
    private Long filterAccountId = null;

    @FXML
    public void initialize() {
        colReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colFrom.setCellValueFactory(new PropertyValueFactory<>("sourceAccountNumber"));
        colTo.setCellValueFactory(new PropertyValueFactory<>("destinationAccountNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPerformedBy.setCellValueFactory(new PropertyValueFactory<>("performedBy"));

        colAmount.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(Formatters.money(data.getValue().getAmount())));
        colWhen.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCreatedAt() != null
                                ? data.getValue().getCreatedAt().format(Formatters.DATE_TIME)
                                : "-"));

        setupAccountComboBox(depositAccountComboBox, false);
        setupAccountComboBox(withdrawAccountComboBox, false);
        setupAccountComboBox(transferSourceComboBox, false);
        setupAccountComboBox(transferDestComboBox, false);
        setupAccountComboBox(filterAccountComboBox, true);

        // Only ACTIVE accounts can transact.
        loadAccountsInto(depositAccountComboBox, "ACTIVE", false);
        loadAccountsInto(withdrawAccountComboBox, "ACTIVE", false);
        loadAccountsInto(transferSourceComboBox, "ACTIVE", false);
        loadAccountsInto(transferDestComboBox, "ACTIVE", false);
        // History filter should be able to show any account regardless of status.
        loadAccountsInto(filterAccountComboBox, "ALL", true);

        loadHistory();
    }

    private void setupAccountComboBox(ComboBox<ComboOption<AccountComboMetadata>> comboBox, boolean withAllOption) {
        comboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(ComboOption<AccountComboMetadata> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item == null ? "All accounts" : item.getLabel()));
            }
        });
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ComboOption<AccountComboMetadata> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? (withAllOption ? "All accounts" : null) : (item == null ? "All accounts" : item.getLabel()));
            }
        });
    }

    private void loadAccountsInto(ComboBox<ComboOption<AccountComboMetadata>> comboBox, String status, boolean withAllOption) {
        Task<List<ComboOption<AccountComboMetadata>>> task = new Task<>() {
            @Override
            protected List<ComboOption<AccountComboMetadata>> call() {
                return accountApi.combo(null, status);
            }
        };
        task.setOnSucceeded(e -> {
            ObservableList<ComboOption<AccountComboMetadata>> items = FXCollections.observableArrayList();
            if (withAllOption) {
                items.add(null);
            }
            items.addAll(task.getValue());
            comboBox.setItems(items);
            if (withAllOption) {
                comboBox.getSelectionModel().select(null);
            }
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            Alerts.error("Failed to load accounts", ex instanceof ApiException apiEx ? apiEx.getMessage() : String.valueOf(ex));
        });
        new Thread(task, "accounts-combo-load").start();
    }

    @FXML
    private void onDeposit() {
        ComboOption<AccountComboMetadata> selected = depositAccountComboBox.getValue();
        if (selected == null) {
            Alerts.error("Missing account", "Please select an account to deposit into.");
            return;
        }
        BigDecimal amount = parseAmount(depositAmountField.getText());
        if (amount == null) return;

        DepositRequest req = new DepositRequest();
        req.setAccountNumber(selected.getValue());
        req.setAmount(amount);
        req.setDescription(emptyToNull(depositDescField.getText()));

        Task<TransactionDto> task = new Task<>() {
            @Override
            protected TransactionDto call() {
                return transactionApi.deposit(req);
            }
        };
        task.setOnSucceeded(e -> {
            Alerts.info("Deposit successful", "Reference: " + task.getValue().getReference());
            depositAmountField.clear();
            depositDescField.clear();
            refreshAccountCombos();
            loadHistory();
        });
        task.setOnFailed(e -> reportFailure(task, "Deposit failed"));
        new Thread(task, "deposit").start();
    }

    @FXML
    private void onWithdraw() {
        ComboOption<AccountComboMetadata> selected = withdrawAccountComboBox.getValue();
        if (selected == null) {
            Alerts.error("Missing account", "Please select an account to withdraw from.");
            return;
        }
        BigDecimal amount = parseAmount(withdrawAmountField.getText());
        if (amount == null) return;

        WithdrawalRequest req = new WithdrawalRequest();
        req.setAccountNumber(selected.getValue());
        req.setAmount(amount);
        req.setDescription(emptyToNull(withdrawDescField.getText()));

        Task<TransactionDto> task = new Task<>() {
            @Override
            protected TransactionDto call() {
                return transactionApi.withdraw(req);
            }
        };
        task.setOnSucceeded(e -> {
            Alerts.info("Withdrawal successful", "Reference: " + task.getValue().getReference());
            withdrawAmountField.clear();
            withdrawDescField.clear();
            refreshAccountCombos();
            loadHistory();
        });
        task.setOnFailed(e -> reportFailure(task, "Withdrawal failed"));
        new Thread(task, "withdraw").start();
    }

    @FXML
    private void onTransfer() {
        ComboOption<AccountComboMetadata> source = transferSourceComboBox.getValue();
        ComboOption<AccountComboMetadata> destination = transferDestComboBox.getValue();
        if (source == null || destination == null) {
            Alerts.error("Missing account", "Please select both a source and destination account.");
            return;
        }
        if (source.getValue().equals(destination.getValue())) {
            Alerts.error("Invalid transfer", "Source and destination accounts must differ.");
            return;
        }
        BigDecimal amount = parseAmount(transferAmountField.getText());
        if (amount == null) return;

        TransferRequest req = new TransferRequest();
        req.setSourceAccountNumber(source.getValue());
        req.setDestinationAccountNumber(destination.getValue());
        req.setAmount(amount);
        req.setDescription(emptyToNull(transferDescField.getText()));

        Task<TransactionDto> task = new Task<>() {
            @Override
            protected TransactionDto call() {
                return transactionApi.transfer(req);
            }
        };
        task.setOnSucceeded(e -> {
            Alerts.info("Transfer successful", "Reference: " + task.getValue().getReference());
            transferAmountField.clear();
            transferDescField.clear();
            refreshAccountCombos();
            loadHistory();
        });
        task.setOnFailed(e -> reportFailure(task, "Transfer failed"));
        new Thread(task, "transfer").start();
    }

    private void refreshAccountCombos() {
        // Balances just changed — reload so labels (which include balance) stay current.
        loadAccountsInto(depositAccountComboBox, "ACTIVE", false);
        loadAccountsInto(withdrawAccountComboBox, "ACTIVE", false);
        loadAccountsInto(transferSourceComboBox, "ACTIVE", false);
        loadAccountsInto(transferDestComboBox, "ACTIVE", false);
        loadAccountsInto(filterAccountComboBox, "ALL", true);
    }

    @FXML
    private void onFilterHistory() {
        ComboOption<AccountComboMetadata> selected = filterAccountComboBox.getValue();
        filterAccountId = selected != null ? selected.getMetadata().getId() : null;
        currentPage = 0;
        loadHistory();
    }

    @FXML
    private void onShowAll() {
        filterAccountComboBox.getSelectionModel().select(null);
        filterAccountId = null;
        currentPage = 0;
        loadHistory();
    }

    @FXML
    private void onPrevPage() {
        if (currentPage > 0) {
            currentPage--;
            loadHistory();
        }
    }

    @FXML
    private void onNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadHistory();
        }
    }

    private void loadHistory() {
        Task<PageResponse<TransactionDto>> task = new Task<>() {
            @Override
            protected PageResponse<TransactionDto> call() {
                return filterAccountId != null
                        ? transactionApi.listForAccount(filterAccountId, currentPage, pageSize)
                        : transactionApi.listAll(currentPage, pageSize);
            }
        };

        task.setOnSucceeded(e -> {
            PageResponse<TransactionDto> page = task.getValue();
            table.setItems(FXCollections.observableArrayList(page.getContent()));
            totalPages = Math.max(page.getTotalPages(), 1);
            pageLabel.setText("Page " + (currentPage + 1) + " of " + totalPages + " (" + page.getTotalElements() + " transactions)");
            prevButton.setDisable(page.isFirst());
            nextButton.setDisable(page.isLast());
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            Alerts.error("Failed to load transaction history", ex instanceof ApiException apiEx ? apiEx.getMessage() : String.valueOf(ex));
        });

        new Thread(task, "transactions-load").start();
    }

    private BigDecimal parseAmount(String text) {
        if (text == null || text.isBlank()) {
            Alerts.error("Invalid amount", "Please enter an amount.");
            return null;
        }
        try {
            BigDecimal amount = new BigDecimal(text.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                Alerts.error("Invalid amount", "Amount must be greater than zero.");
                return null;
            }
            return amount;
        } catch (NumberFormatException ex) {
            Alerts.error("Invalid amount", "Please enter a valid number.");
            return null;
        }
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private void reportFailure(Task<?> task, String title) {
        Throwable ex = task.getException();
        Alerts.error(title, ex instanceof ApiException apiEx ? apiEx.getMessage() : String.valueOf(ex));
    }
}
