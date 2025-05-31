package com.example.muxixixi.koneksi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Database {
    public static final String url  = "jdbc:mysql://localhost:3306/muxixixi";
    public static final String user = "root";
    public static final String password = "";
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
