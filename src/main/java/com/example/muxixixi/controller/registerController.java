package com.example.muxixixi.controller;

import com.example.muxixixi.koneksi.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class registerController {
    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private PasswordField confirmPasswordTextField;

    @FXML
    private Label messageLabel;

    @FXML
    public void handleRegister(ActionEvent e) {
        String username = usernameTextField.getText().trim();
        String password = passwordTextField.getText();
        String confirmPassword = confirmPasswordTextField.getText();

        if(username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText(("Username atau Password tidak boleh kosong"));
            return;
        }

        if(!password.equals((confirmPassword))) {
            messageLabel.setText(("Password tidak cocok"));
            return;
        }

        try(Connection conn = Database.connect()) {
            String checkQuery = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if(rs.next()) {
                messageLabel.setText("Username sudah digunakan");
                return;
            }
            String hashedPassword = hashPassword(password);
            // simpan user baru
            String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setString(2, hashedPassword);
            insertStmt.executeUpdate();
            messageLabel.setText("Registrasi Berhasil");
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Success");
//            alert.setHeaderText(null);
//            alert.setContentText("Registrasi Berhasil");
//            alert.showAndWait();
            SceneManager.switchToLogin();
        } catch(SQLException ex) {
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

    public void toLogin() {
        SceneManager.switchToLogin();
    }
}
