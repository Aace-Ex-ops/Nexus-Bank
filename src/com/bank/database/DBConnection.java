package com.bank.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(2)) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // 🔥 HARD-CODED CREDENTIALS - NO FILE NEEDED
                String url  = "jdbc:mysql://localhost:3306/bank_db?autoReconnect=true";
                String user = "root";
                String pass = "Aace_ex@07"; 

                connection = DriverManager.getConnection(url, user, pass);
                System.out.println("Handshake successful. Connected to Database.");
                
            } catch (ClassNotFoundException e) {
                throw new SQLException("JDBC Driver not found. Check your lib folder.");
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection = null;
        }
    }
}