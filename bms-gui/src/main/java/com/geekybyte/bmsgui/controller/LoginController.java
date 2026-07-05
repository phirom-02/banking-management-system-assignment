package com.geekybyte.bmsgui.controller;

import com.geekybyte.bmsgui.api.AuthApi;
import com.geekybyte.bmsgui.core.ApiException;
import com.geekybyte.bmsgui.core.Session;
import com.geekybyte.bmsgui.model.LoginResponse;
import com.geekybyte.bmsgui.util.Navigator;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private ProgressIndicator loadingIndicator;

    private final AuthApi authApi = new AuthApi();

    @FXML
    public void initialize() {
        usernameField.setText("admin");
        passwordField.setText("Admin@12345");
    }

    @FXML
    private void onLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        setLoading(true);

        Task<LoginResponse> task = new Task<>() {
            @Override
            protected LoginResponse call() {
                return authApi.login(username, password);
            }
        };

        task.setOnSucceeded(e -> {
            setLoading(false);
            LoginResponse response = task.getValue();
            Session.getInstance().set(response.getToken(), response.getUsername(), response.getRole());
            Navigator.showMain();
        });

        task.setOnFailed(e -> {
            setLoading(false);
            Throwable ex = task.getException();
            if (ex instanceof ApiException apiEx) {
                showError(apiEx.getStatusCode() == 0
                        ? "Could not reach the server. Is the backend running?"
                        : apiEx.getMessage());
            } else {
                System.out.println("Unexpected error: " + (ex != null ? ex.getMessage() : "unknown"));
                showError("Unexpected error: " + (ex != null ? ex.getMessage() : "unknown"));
            }
        });

        new Thread(task, "login-task").start();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
        usernameField.setDisable(loading);
        passwordField.setDisable(loading);
        if (!loading) {
            // no-op, kept for symmetry / future disable-button logic
        }
    }
}
