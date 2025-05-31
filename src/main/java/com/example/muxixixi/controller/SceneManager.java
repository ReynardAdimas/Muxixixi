package com.example.muxixixi.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private static Stage stage;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static  void switchToMusic() {
        //switchScene("src/main/resources/com/example/muxixixi/mainUI.fxml");
        switchScene("/com/example/muxixixi/mainUI.fxml");
    }


    public static void switchToLogin() {
        switchScene("/com/example/muxixixi/loginUI.fxml");
    }

    public static void switchToRegister() {
        switchScene("/com/example/muxixixi/registerUI.fxml");
    }
    private static void switchScene(String fxmlPath) {
        try{
//            Parent root = javafx.fxml.FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
//            stage.setScene(new javafx.scene.Scene(root));
//            stage.show();
            FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            if(fxmlLoader.getLocation() == null) {
                System.out.println("Location is null");
                return;
            }
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(IOException | NullPointerException e) {
            System.out.println("Gagal load scene: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
