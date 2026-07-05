package com.geekybyte.bmsgui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Handles top-level scene switching (login <-> main shell) on the primary stage.
 */
public final class Navigator {

    private static Stage primaryStage;

    private Navigator() {
    }

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void showLogin() {
        try {
            Parent root = load("/com/geekybyte/bmsgui/fxml/login.fxml");
            Scene scene = new Scene(root, 480, 420);
            scene.getStylesheets().add(resource("/com/geekybyte/bmsgui/css/style.css"));
            primaryStage.setTitle("Core Banking — Sign in");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load login screen", e);
        }
    }

    public static void showMain() {
        try {
            Parent root = load("/com/geekybyte/bmsgui/fxml/main.fxml");
            Scene scene = new Scene(root, 1180, 760);
            scene.getStylesheets().add(resource("/com/geekybyte/bmsgui/css/style.css"));
            primaryStage.setTitle("Core Banking Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load main screen", e);
        }
    }

    public static Parent load(String fxmlPath) throws IOException {
        URL url = Navigator.class.getResource(fxmlPath);
        if (url == null) {
            throw new IOException("FXML not found on classpath: " + fxmlPath);
        }
        return FXMLLoader.load(url);
    }

    public static FXMLLoader loader(String fxmlPath) {
        URL url = Navigator.class.getResource(fxmlPath);
        if (url == null) {
            throw new RuntimeException("FXML not found on classpath: " + fxmlPath);
        }
        return new FXMLLoader(url);
    }

    private static String resource(String path) {
        URL url = Navigator.class.getResource(path);
        return url != null ? url.toExternalForm() : "";
    }
}
