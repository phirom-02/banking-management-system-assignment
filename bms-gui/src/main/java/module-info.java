module com.geekybyte.bmsgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens com.geekybyte.bmsgui to javafx.fxml;
    opens com.geekybyte.bmsgui.controller to javafx.fxml;
    opens com.geekybyte.bmsgui.model to javafx.base,
            com.fasterxml.jackson.databind;
    exports com.geekybyte.bmsgui;
}