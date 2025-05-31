module com.example.muxixixi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.media;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.muxixixi to javafx.fxml;
    opens com.example.muxixixi.controller to javafx.fxml;
    opens com.example.muxixixi.model to javafx.base;
    exports com.example.muxixixi;
    exports com.example.muxixixi.controller;
}