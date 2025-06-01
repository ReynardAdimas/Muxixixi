package com.example.muxixixi.controller;

import com.example.muxixixi.koneksi.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Label loginLabel;

    @FXML
    private Button loginBtn;

    @FXML
    private Label loginMessage;

    @FXML
    public void handleLogin(ActionEvent e) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String hashedPassword = hashPassword(password);
        try(Connection connection = Database.connect()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                int userId = rs.getInt("id");
                AppSession.setCurrentUserId(userId);
                SceneManager.switchToMusic();
            } else {
                loginMessage    .setText("Username atau Password Salah");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for(byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    @FXML
    public void toRegister(MouseEvent e) {
        SceneManager.switchToRegister();
//        usernameTextField.clear();
//        passwordTextField.clear();
//        loginLabel.setText("");
//        loginBtn.setDisable(false);
    }
}
