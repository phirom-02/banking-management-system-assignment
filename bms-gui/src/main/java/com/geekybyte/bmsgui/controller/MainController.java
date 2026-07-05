package com.geekybyte.bmsgui.controller;

import com.geekybyte.bmsgui.core.Session;
import com.geekybyte.bmsgui.util.Alerts;
import com.geekybyte.bmsgui.util.Navigator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;
    @FXML
    private Label userLabel;

    @FXML
    private Button navDashboard;
    @FXML
    private Button navCustomers;
    @FXML
    private Button navAccounts;
    @FXML
    private Button navTransactions;
    @FXML
    private Button navLedger;

    @FXML
    public void initialize() {
        Session session = Session.getInstance();
        userLabel.setText("Signed in as " + session.getUsername() + " (" + session.getRole() + ")");
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        load("/com/geekybyte/bmsgui/fxml/dashboard.fxml", navDashboard);
    }

    @FXML
    private void showCustomers() {
        load("/com/geekybyte/bmsgui/fxml/customers.fxml", navCustomers);
    }

    @FXML
    private void showAccounts() {
        load("/com/geekybyte/bmsgui/fxml/accounts.fxml", navAccounts);
    }

    @FXML
    private void showTransactions() {
        load("/com/geekybyte/bmsgui/fxml/transactions.fxml", navTransactions);
    }

    @FXML
    private void showLedger() {
        load("/com/geekybyte/bmsgui/fxml/ledger.fxml", navLedger);
    }

    @FXML
    private void onLogout() {
        Session.getInstance().clear();
        Navigator.showLogin();
    }

    private void load(String fxmlPath, Button activeButton) {
        try {
            FXMLLoader loader = Navigator.loader(fxmlPath);
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
            setActiveNav(activeButton);
        } catch (IOException e) {
            Alerts.error("Navigation error", "Could not load screen: " + e.getMessage());
        }
    }

    private void setActiveNav(Button active) {
        for (Button b : new Button[]{navDashboard, navCustomers, navAccounts, navTransactions, navLedger}) {
            b.getStyleClass().remove("nav-button-active");
            if (b == active) {
                b.getStyleClass().add("nav-button-active");
            }
        }
    }
}
