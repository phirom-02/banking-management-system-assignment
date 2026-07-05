package com.geekybyte.bmsgui.controller;

import com.geekybyte.bmsgui.api.AccountApi;
import com.geekybyte.bmsgui.api.CustomerApi;
import com.geekybyte.bmsgui.core.ApiException;
import com.geekybyte.bmsgui.model.AccountDto;
import com.geekybyte.bmsgui.model.AccountRequest;
import com.geekybyte.bmsgui.model.ComboOption;
import com.geekybyte.bmsgui.model.CustomerComboMetadata;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;

public class AccountDialogController {

    @FXML
    private ComboBox<ComboOption<CustomerComboMetadata>> customerComboBox;
    @FXML
    private ChoiceBox<String> accountTypeChoice;
    @FXML
    private TextField openingBalanceField;
    @FXML
    private TextField currencyField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button saveButton;

    private final AccountApi accountApi = new AccountApi();
    private final CustomerApi customerApi = new CustomerApi();

    private Runnable onSaved;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    /**
     * Pre-select a customer, e.g. when opening this dialog from a customer's detail view.
     */
    public void presetCustomerId(Long customerId) {
        for (ComboOption<CustomerComboMetadata> option : customerComboBox.getItems()) {
            if (option != null && String.valueOf(customerId).equals(option.getValue())) {
                customerComboBox.getSelectionModel().select(option);
                break;
            }
        }
    }

    @FXML
    public void initialize() {
        accountTypeChoice.setItems(FXCollections.observableArrayList("SAVINGS", "CHECKING"));
        accountTypeChoice.getSelectionModel().selectFirst();

        customerComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(ComboOption<CustomerComboMetadata> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });
        customerComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ComboOption<CustomerComboMetadata> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });

        loadCustomers();
    }

    private void loadCustomers() {
        Task<List<ComboOption<CustomerComboMetadata>>> task = new Task<>() {
            @Override
            protected List<ComboOption<CustomerComboMetadata>> call() {
                // Only ACTIVE customers can have new accounts opened for them.
                return customerApi.combo("ACTIVE");
            }
        };
        task.setOnSucceeded(e -> customerComboBox.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            showError("Could not load customers: " + (ex instanceof ApiException apiEx ? apiEx.getMessage() : ex));
        });
        new Thread(task, "customers-combo-load").start();
    }

    @FXML
    private void onSave() {
        ComboOption<CustomerComboMetadata> selectedCustomer = customerComboBox.getValue();
        if (selectedCustomer == null) {
            showError("Please select a customer.");
            return;
        }

        String accountType = accountTypeChoice.getValue();
        if (accountType == null) {
            showError("Please select an account type.");
            return;
        }

        BigDecimal openingBalance = null;
        String balanceText = openingBalanceField.getText();
        if (balanceText != null && !balanceText.isBlank()) {
            try {
                openingBalance = new BigDecimal(balanceText.trim());
            } catch (NumberFormatException ex) {
                showError("Opening balance must be a valid number.");
                return;
            }
        }

        String currency = currencyField.getText() == null || currencyField.getText().isBlank()
                ? "USD" : currencyField.getText().trim().toUpperCase();

        AccountRequest request = new AccountRequest();
        request.setCustomerId(Long.parseLong(selectedCustomer.getValue()));
        request.setAccountType(accountType);
        request.setOpeningBalance(openingBalance);
        request.setCurrency(currency);

        saveButton.setDisable(true);

        Task<AccountDto> task = new Task<>() {
            @Override
            protected AccountDto call() {
                return accountApi.open(request);
            }
        };

        task.setOnSucceeded(e -> {
            if (onSaved != null) onSaved.run();
            if (stage != null) stage.close();
        });

        task.setOnFailed(e -> {
            saveButton.setDisable(false);
            Throwable ex = task.getException();
            showError(ex instanceof ApiException apiEx ? apiEx.getMessage() : "Failed to open account: " + ex);
        });

        new Thread(task, "account-open").start();
    }

    @FXML
    private void onCancel() {
        if (stage != null) stage.close();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
