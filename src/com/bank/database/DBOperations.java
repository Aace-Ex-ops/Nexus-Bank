package com.bank.database;

import java.sql.*;
import java.util.UUID;

public class DBOperations {

    public boolean deposit(String acc, double amt, boolean isSavings) {
        String column = isSavings ? "savings_balance" : "current_balance";
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            String sql = "UPDATE users SET " + column + " = " + column + " + ? WHERE account_no = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, amt);
            pstmt.setString(2, acc);

            if (pstmt.executeUpdate() > 0) {
                String tid = "DEP-" + generateID();
                String type = isSavings ? "SAVINGS DEPOSIT" : "CURRENT DEPOSIT";
                
                logTransaction(conn, tid, acc, type, amt, "Self Deposit");
                
                conn.commit();
                return true;
            }
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean withdraw(String acc, double amt, boolean isSavings) {
        String column = isSavings ? "savings_balance" : "current_balance";
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            String checkSql = "SELECT " + column + " FROM users WHERE account_no = ?";
            PreparedStatement cp = conn.prepareStatement(checkSql);
            cp.setString(1, acc);
            ResultSet rs = cp.executeQuery();
            
            if (rs.next() && rs.getDouble(column) >= amt) {

                String sql = "UPDATE users SET " + column + " = " + column + " - ? WHERE account_no = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, amt);
                pstmt.setString(2, acc);
                
                if (pstmt.executeUpdate() > 0) {
                    String tid = "WD-" + generateID();
                    String type = isSavings ? "SAVINGS WITHDRAW" : "CURRENT WITHDRAW";
                    
                    logTransaction(conn, tid, acc, type, -amt, "ATM / Counter");
                    
                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void logTransaction(Connection conn, String tid, String acc, String type, double amt, String ref) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_id, account_no, type, amount, related_account) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, tid);
        ps.setString(2, acc);
        ps.setString(3, type);
        ps.setDouble(4, amt);
        ps.setString(5, ref);
        ps.executeUpdate();
    }

    private String generateID() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}