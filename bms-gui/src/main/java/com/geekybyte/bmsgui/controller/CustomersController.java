package com.geekybyte.bmsgui.controller;

import com.geekybyte.bmsgui.api.CustomerApi;
import com.geekybyte.bmsgui.core.ApiException;
import com.geekybyte.bmsgui.model.CustomerDto;
import com.geekybyte.bmsgui.model.PageResponse;
import com.geekybyte.bmsgui.util.Alerts;
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

public class CustomersController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<CustomerDto> table;
    @FXML
    private TableColumn<CustomerDto, Long> colId;
    @FXML
    private TableColumn<CustomerDto, String> colName;
    @FXML
    private TableColumn<CustomerDto, String> colEmail;
    @FXML
    private TableColumn<CustomerDto, String> colPhone;
    @FXML
    private TableColumn<CustomerDto, String> colNationalId;
    @FXML
    private TableColumn<CustomerDto, String> colStatus;
    @FXML
    private TableColumn<CustomerDto, Void> colActions;
    @FXML
    private Label pageLabel;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;

    private final CustomerApi customerApi = new CustomerApi();

    private int currentPage = 0;
    private final int pageSize = 15;
    private int totalPages = 1;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colNationalId.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button statusBtn = new Button("Toggle Status");
            private final HBox box = new HBox(6, editBtn, statusBtn);

            {
                editBtn.getStyleClass().add("button-secondary");
                statusBtn.getStyleClass().add("button-secondary");
                editBtn.setOnAction(e -> openDialog(getTableRow().getItem()));
                statusBtn.setOnAction(e -> toggleStatus(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        loadPage();
    }

    @FXML
    private void onSearch() {
        currentPage = 0;
        loadPage();
    }

    @FXML
    private void onNew() {
        openDialog(null);
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
        String search = searchField.getText();

        Task<PageResponse<CustomerDto>> task = new Task<>() {
            @Override
            protected PageResponse<CustomerDto> call() {
                return customerApi.list(search, currentPage, pageSize);
            }
        };

        task.setOnSucceeded(e -> {
            PageResponse<CustomerDto> page = task.getValue();
            table.setItems(FXCollections.observableArrayList(page.getContent()));
            totalPages = Math.max(page.getTotalPages(), 1);
            pageLabel.setText("Page " + (currentPage + 1) + " of " + totalPages + " (" + page.getTotalElements() + " customers)");
            prevButton.setDisable(page.isFirst());
            nextButton.setDisable(page.isLast());
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            Alerts.error("Failed to load customers", ex instanceof ApiException apiEx ? apiEx.getMessage() : String.valueOf(ex));
        });

        new Thread(task, "customers-load").start();
    }

    private void openDialog(CustomerDto existing) {
        try {
            FXMLLoader loader = Navigator.loader("/com/geekybyte/bmsgui/fxml/customer_dialog.fxml");
            Parent root = loader.load();
            CustomerDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(existing == null ? "New Customer" : "Edit Customer");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            controller.setStage(dialogStage);
            controller.setOnSaved(this::loadPage);
            if (existing != null) {
                controller.loadForEdit(existing);
            }

            dialogStage.showAndWait();
        } catch (IOException e) {
            Alerts.error("Error", "Could not open customer form: " + e.getMessage());
        }
    }

    private void toggleStatus(CustomerDto customer) {
        if (customer == null) return;
        String newStatus = "ACTIVE".equals(customer.getStatus()) ? "INACTIVE" : "ACTIVE";
        if (!Alerts.confirm("Change status", "Set " + customer.getFullName() + " to " + newStatus + "?")) {
            return;
        }

        Task<CustomerDto> task = new Task<>() {
            @Override
            protected CustomerDto call() {
                return customerApi.updateStatus(customer.getId(), newStatus);
            }
        };
        task.setOnSucceeded(e -> loadPage());
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            Alerts.error("Failed to update status", ex instanceof ApiException apiEx ? apiEx.getMessage() : String.valueOf(ex));
        });
        new Thread(task, "customer-status").start();
    }
}
