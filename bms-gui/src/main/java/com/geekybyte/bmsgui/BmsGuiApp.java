package com.geekybyte.bmsgui;

import com.geekybyte.bmsgui.util.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class BmsGuiApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        Navigator.init(primaryStage);
        Navigator.showLogin();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}