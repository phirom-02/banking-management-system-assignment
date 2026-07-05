package com.geekybyte.bmsgui.controller;

import com.geekybyte.bmsgui.api.AccountApi;
import com.geekybyte.bmsgui.api.CustomerApi;
import com.geekybyte.bmsgui.core.ApiException;
import com.geekybyte.bmsgui.model.AccountDto;
import com.geekybyte.bmsgui.model.ComboOption;
import com.geekybyte.bmsgui.model.CustomerComboMetadata;
import com.geekybyte.bmsgui.model.PageResponse;
import com.geekybyte.bmsgui.util.Alerts;
import com.geekybyte.bmsgui.util.Formatters;
import com.geekybyte.bmsgui.util.Navigator;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AccountsController {

    @FXML
    private ComboBox<ComboOption<CustomerComboMetadata>> customerFilterComboBox;
    @FXML
    private ChoiceBox<String> statusFilter;
    @FXML
    private TableView<AccountDto> table;
    @FXML
    private TableColumn<AccountDto, String> colAccountNumber;
    @FXML
    private TableColumn<AccountDto, String> colCustomer;
    @FXML
    private TableColumn<AccountDto, String> colType;
    @FXML
    private TableColumn<AccountDto, String> colBalance;
    @FXML
    private TableColumn<AccountDto, String> colCurrency;
    @FXML
    private TableColumn<AccountDto, String> colStatus;
    @FXML
    private TableColumn<AccountDto, Void> colActions;
    @FXML
    private Label pageLabel;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;

    private final AccountApi accountApi = new AccountApi();
    private final CustomerApi customerApi = new CustomerApi();

    private int currentPage = 0;
    private final int pageSize = 15;
    private int totalPages = 1;

    @FXML
    public void initialize() {
        statusFilter.setItems(FXCollections.observableArrayList("All", "ACTIVE", "FROZEN", "CLOSED"));
        statusFilter.getSelectionModel().selectFirst();

        setupCustomerFilterComboBox();

        colAccountNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colType.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        colCurrency.setCellValueFactory(new PropertyValueFactory<>("currency"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colBalance.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(Formatters.money(data.getValue().getBalance())));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button freezeBtn = new Button("Freeze");
            private final Button unfreezeBtn = new Button("Unfreeze");
            private final Button closeBtn = new Button("Close");
            private final HBox box = new HBox(6, freezeBtn, unfreezeBtn, closeBtn);

            {
                for (Button b : new Button[]{freezeBtn, unfreezeBtn, closeBtn}) {
                    b.getStyleClass().add("button-secondary");
                }
                freezeBtn.setOnAction(e -> changeStatus(getTableRow().getItem(), "FROZEN"));
                unfreezeBtn.setOnAction(e -> changeStatus(getTableRow().getItem(), "ACTIVE"));
                closeBtn.setOnAction(e -> changeStatus(getTableRow().getItem(), "CLOSED"));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                AccountDto account = getTableRow().getItem();
                boolean closed = "CLOSED".equals(account.getStatus());
                freezeBtn.setDisable(closed || "FROZEN".equals(account.getStatus()));
                unfreezeBtn.setDisable(closed || "ACTIVE".equals(account.getStatus()));
                closeBtn.setDisable(closed);
                setGraphic(box);
            }
        });

        loadPage();
    }

    private void setupCustomerFilterComboBox() {
        customerFilterComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(ComboOption<CustomerComboMetadata> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item == null ? "All customers" : item.getLabel()));
            }
        });
        customerFilterComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ComboOption<CustomerComboMetadata> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "All customers" : (item == null ? "All customers" : item.getLabel()));
            }
        });

        Task<List<ComboOption<CustomerComboMetadata>>> task = new Task<>() {
            @Override
            protected List<ComboOption<CustomerComboMetadata>> call() {
                return customerApi.combo("ALL");
            }
        };
        task.setOnSucceeded(e -> {
            var items = FXCollections.<ComboOption<CustomerComboMetadata>>observableArrayList();
            items.add(null); // represents "All customers"
            items.addAll(task.getValue());
            customerFilterComboBox.setItems(items);
            customerFilterComboBox.getSelectionModel().select(null);
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            Alerts.error("Failed to load customers", ex instanceof ApiException apiEx ? apiEx.getMessage() : String.valueOf(ex));
        });
        new Thread(task, "customers-combo-load").start();
    }

    @FXML
    private void onFilter() {
        currentPage = 0;
        loadPage();
    }

    @FXML
    private void onNew() {
        try {
            FXMLLoader loader = Navigator.loader("/com/geekybyte/bmsgui/fxml/account_dialog.fxml");
            Parent root = loader.load();
            AccountDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Open New Account");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            controller.setStage(dialogStage);
            controller.setOnSaved(this::loadPage);

            dialogStage.showAndWait();
        } catch (IOException e) {
            Alerts.error("Error", "Could not open account form: " + e.getMessage());
        }
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
        ComboOption<CustomerComboMetadata> selectedCustomer = customerFilterComboBox.getValue();
        Long customerId = selectedCustomer != null ? Long.parseLong(selectedCustomer.getValue()) : null;

        String status = statusFilter.getValue();
        String statusParam = (status == null || status.equals("All")) ? null : status;

        Task<PageResponse<AccountDto>> task = new Task<>() {
            @Override
            protected PageResponse<AccountDto> call() {
                return accountApi.list(customerId, statusParam, currentPage, pageSize);
            }
        };

        task.setOnSucceeded(e -> {
            PageResponse<AccountDto> page = task.getValue();
            table.setItems(FXCollections.observableArrayList(page.getContent()));
            totalPages = Math.max(page.getTotalPages(), 1);
            pageLabel.setText("Page " + (currentPage + 1) + " of " + totalPages + " (" + page.getTotalElements() + " accounts)");
            prevButton.setDisable(page.isFirst());
            nextButton.setDisable(page.isLast());
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            Alerts.error("Failed to load accounts", ex instanceof ApiException apiEx ? apiEx.getMessage() : String.valueOf(ex));
        });

        new Thread(task, "accounts-load").start();
    }

    private void changeStatus(AccountDto account, String newStatus) {
        if (account == null) return;
        if (!Alerts.confirm("Change account status", "Set account " + account.getAccountNumber() + " to " + newStatus + "?")) {
            return;
        }

        Task<AccountDto> task = new Task<>() {
            @Override
            protected AccountDto call() {
                return accountApi.updateStatus(account.getId(), newStatus, null);
            }
        };
        task.setOnSucceeded(e -> loadPage());
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            Alerts.error("Failed to update account status", ex instanceof ApiException apiEx ? apiEx.getMessage() : String.valueOf(ex));
        });
        new Thread(task, "account-status").start();
    }
}
