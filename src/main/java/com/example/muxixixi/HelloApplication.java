package com.example.muxixixi;

import com.example.muxixixi.controller.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.setStage(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Muxixixi");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}