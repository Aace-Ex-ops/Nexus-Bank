package com.bank.ui;

import com.bank.database.DBConnection;
import com.bank.utils.ThemeManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.UUID;

public class TransferSubPage extends JPanel {
    private JTextField targetField;
    private JTextField amtField;
    private String transferType;

    public TransferSubPage(TransactionPanel parent, String type) {
        this.transferType = type;
        setLayout(new GridBagLayout());
        setOpaque(false);

        // --- GLASS CARD CONTAINER ---
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(ThemeManager.getBorderColor());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(450, 520));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- UI ELEMENTS ---
        JLabel title = new JLabel(type.equals("ACCOUNT") ? "Bank Transfer" : "Mobile Pay");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(ThemeManager.getForeground());
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("Securely move funds across Nexus");
        subTitle.setForeground(ThemeManager.getMutedForeground());
        subTitle.setAlignmentX(CENTER_ALIGNMENT);

        String targetLabelText = type.equals("ACCOUNT") ? "RECIPIENT'S ACCOUNT NUMBER" : "RECIPIENT'S MOBILE NUMBER";
        String targetPlaceholder = type.equals("ACCOUNT") ? "Ex: 1002" : "Ex: 9876543210";

        targetField = createStyledTextField(targetPlaceholder);
        amtField = createStyledTextField("Enter Amount ($)");

        JButton sendBtn = new JButton("Confirm Transfer");
        stylePrimaryButton(sendBtn);
        
        JButton backBtn = new JButton("← Back to Hub");
        styleSecondaryButton(backBtn);

        // --- ACTION LISTENERS ---
        backBtn.addActionListener(e -> parent.showMenu());
        sendBtn.addActionListener(e -> handleTransfer(parent));

        // --- ASSEMBLY ---
        card.add(title);
        card.add(subTitle);
        card.add(Box.createRigidArea(new Dimension(0, 40)));
        
        card.add(createLabel(targetLabelText)); 
        card.add(targetField);
        
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(createLabel("AMOUNT TO TRANSFER"));
        card.add(amtField);
        
        card.add(Box.createRigidArea(new Dimension(0, 40)));
        card.add(sendBtn);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(backBtn);

        add(card);
    }

    private void handleTransfer(TransactionPanel parent) {
        String target = targetField.getText().trim();
        String amountStr = amtField.getText().trim();

        if (target.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) throw new NumberFormatException();

            // Defaulting to Savings (true) for transfers
            double currentLiveBalance = parent.getMainDashboard().getCurrentBalance(true);

            if (amount > currentLiveBalance) {
                JOptionPane.showMessageDialog(this, "Insufficient balance in Savings!\nAvailable: $" + String.format("%.2f", currentLiveBalance));
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);

                String findSql = transferType.equals("ACCOUNT") ? 
                    "SELECT account_no, name FROM users WHERE account_no = ?" : 
                    "SELECT account_no, name FROM users WHERE mobile = ?";
                
                PreparedStatement psFind = conn.prepareStatement(findSql);
                psFind.setString(1, target);
                ResultSet rs = psFind.executeQuery();

                if (!rs.next()) {
                    throw new SQLException("The " + (transferType.equals("ACCOUNT") ? "account number" : "mobile number") + " provided is not registered.");
                }

                String receiverAcc = rs.getString("account_no");
                String receiverName = rs.getString("name");

                if (receiverAcc.equals(parent.getAccNo())) {
                    throw new SQLException("Self-transfers are not allowed.");
                }

                // Update Balances - true indicates Savings bucket
                updateBalance(conn, -amount, parent.getAccNo(), true); 
                updateBalance(conn, amount, receiverAcc, true); 

                // Log Transaction
                String baseTid = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                logTxn(conn, baseTid + "-SEN", parent.getAccNo(), "TRANSFER", -amount, receiverAcc);
                logTxn(conn, baseTid + "-REC", receiverAcc, "RECEIVED", amount, parent.getAccNo());

                conn.commit();
                
                JOptionPane.showMessageDialog(this, "Transfer Successful!\nSent $" + amount + " to " + receiverName);
                
                // --- FIX: Passing both amount and isSavings boolean ---
                parent.getMainDashboard().updateBalance(currentLiveBalance - amount, true);
                
                parent.reloadHistory();
                parent.showMenu();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "System Error: " + ex.getMessage());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
        }
    }

    private void updateBalance(Connection conn, double amt, String acc, boolean isSavings) throws SQLException {
        String column = isSavings ? "savings_balance" : "current_balance";
        String sql = "UPDATE users SET " + column + " = " + column + " + ? WHERE account_no = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDouble(1, amt);
        ps.setString(2, acc);
        ps.executeUpdate();
    }

    private void logTxn(Connection conn, String tid, String acc, String type, double amt, String ref) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_id, account_no, type, amount, related_account) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, tid);
        ps.setString(2, acc);
        ps.setString(3, type);
        ps.setDouble(4, amt);
        ps.setString(5, ref);
        ps.executeUpdate();
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setMaximumSize(new Dimension(400, 45));
        f.setBackground(ThemeManager.getCardBackground());
        f.setForeground(ThemeManager.getForeground());
        f.setCaretColor(ThemeManager.getCaretColor());
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
            new EmptyBorder(5, 10, 5, 10)
        ));
        f.setFont(new Font("SansSerif", Font.PLAIN, 15));
        
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (f.getText().equals(placeholder)) f.setText("");
            }
        });
        return f;
    }

    private JLabel createLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(ThemeManager.getMutedForeground());
        l.setAlignmentX(CENTER_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 5, 0));
        return l;
    }

    private void stylePrimaryButton(JButton b) {
        b.setMaximumSize(new Dimension(400, 45));
        b.setBackground(new Color(52, 152, 219));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(CENTER_ALIGNMENT);
    }

    private void styleSecondaryButton(JButton b) {
        b.setMaximumSize(new Dimension(400, 40));
        b.setContentAreaFilled(false);
        b.setForeground(ThemeManager.getMutedForeground());
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(CENTER_ALIGNMENT);
    }
}