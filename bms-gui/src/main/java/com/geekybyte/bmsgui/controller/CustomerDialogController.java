package com.geekybyte.bmsgui.controller;

import com.geekybyte.bmsgui.api.CustomerApi;
import com.geekybyte.bmsgui.core.ApiException;
import com.geekybyte.bmsgui.model.CustomerDto;
import com.geekybyte.bmsgui.model.CustomerRequest;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class CustomerDialogController {

    @FXML
    private Label titleLabel;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField nationalIdField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private TextArea addressField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button saveButton;

    private final CustomerApi customerApi = new CustomerApi();

    private Long editingId; // null => creating
    private Runnable onSaved;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    /**
     * Call to switch the dialog into "edit" mode, prefilled with the given customer.
     */
    public void loadForEdit(CustomerDto customer) {
        this.editingId = customer.getId();
        titleLabel.setText("Edit Customer");
        fullNameField.setText(customer.getFullName());
        emailField.setText(customer.getEmail());
        phoneField.setText(customer.getPhone());
        nationalIdField.setText(customer.getNationalId());
        addressField.setText(customer.getAddress());
        if (customer.getDateOfBirth() != null) {
            dobPicker.setValue(customer.getDateOfBirth());
        }
    }

    @FXML
    private void onSave() {
        String fullName = trim(fullNameField.getText());
        String email = trim(emailField.getText());
        String phone = trim(phoneField.getText());
        String nationalId = trim(nationalIdField.getText());
        LocalDate dob = dobPicker.getValue();
        String address = trim(addressField.getText());

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || nationalId.isEmpty()) {
            showError("Full name, email, phone, and national ID are required.");
            return;
        }

        CustomerRequest request = new CustomerRequest();
        request.setFullName(fullName);
        request.setEmail(email);
        request.setPhone(phone);
        request.setNationalId(nationalId);
        request.setAddress(address);
        request.setDateOfBirth(dob);

        saveButton.setDisable(true);

        Task<CustomerDto> task = new Task<>() {
            @Override
            protected CustomerDto call() {
                return editingId != null ? customerApi.update(editingId, request) : customerApi.create(request);
            }
        };

        task.setOnSucceeded(e -> {
            if (onSaved != null) onSaved.run();
            if (stage != null) stage.close();
        });

        task.setOnFailed(e -> {
            saveButton.setDisable(false);
            Throwable ex = task.getException();
            System.out.println(ex instanceof ApiException apiEx ? apiEx.getMessage() : "Failed to save customer: " + ex);
            showError(ex instanceof ApiException apiEx ? apiEx.getMessage() : "Failed to save customer: " + ex);
        });

        new Thread(task, "customer-save").start();
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

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
