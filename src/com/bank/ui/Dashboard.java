package com.bank.ui;

import com.bank.database.DBConnection;
import com.bank.database.DBOperations;
import com.bank.utils.ThemeManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JFrame {
    private String accountNo;
    private String userName;
    
    // Dual Balance Tracking
    private double savingsBalance;
    private double currentAccountBalance;
    
    private JLabel balLabel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;

    // Sub-navigation for the Secure Transaction Hub
    private CardLayout hubLayout;
    private JPanel hubContainer;

    public Dashboard(String acc, String name, double initialBal) {
        this.accountNo = acc;
        this.userName = name;
        
        // Initial fetch to sync both buckets from Database
        refreshBalancesFromDB();

        setTitle("Nexus Portal | " + userName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupSidebar();

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        // PAGE 1: Secure Transaction Hub
        contentPanel.add(createTransactionHub(), "TRANS");

        // PAGE 2: Transaction Navigation Hub
        contentPanel.add(new TransactionPanel(this, accountNo, 0), "HISTORY");

        // PAGE 3: Account Profile
        contentPanel.add(new ProfilePanel(this, accountNo, userName), "ACCOUNT");

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        applyTheme();
        setVisible(true);
    }

    /**
     * Fetches fresh values for both account types from the database.
     */
    private void refreshBalancesFromDB() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT savings_balance, current_balance FROM users WHERE account_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.savingsBalance = rs.getDouble("savings_balance");
                this.currentAccountBalance = rs.getDouble("current_balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Required by TransactionPanel/TransferSubPage logic.
     * Returns the balance of the currently viewed account type.
     */
    public double getCurrentBalance(boolean isSavings) {
        refreshBalancesFromDB();
        return isSavings ? savingsBalance : currentAccountBalance;
    }

    /**
     * FIX: The missing method that TransferSubPage calls.
     * Updates the local variable and syncs the UI.
     */
    public void updateBalance(double newBalance, boolean isSavings) {
        if (isSavings) {
            this.savingsBalance = newBalance;
        } else {
            this.currentAccountBalance = newBalance;
        }
        syncUI(isSavings);
    }

    public void syncUI(boolean isSavings) {
        refreshBalancesFromDB();
        if (balLabel != null) {
            double val = isSavings ? savingsBalance : currentAccountBalance;
            balLabel.setText("$" + String.format("%.2f", val));
        }
        
        // Refresh History Table
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof TransactionPanel) {
                ((TransactionPanel) comp).reloadHistory();
            }
        }
    }

    private JPanel createTransactionHub() {
        hubLayout = new CardLayout();
        hubContainer = new JPanel(hubLayout);
        hubContainer.setOpaque(false);

        hubContainer.add(createWelcomeView(), "WELCOME");
        hubContainer.add(createPinEntryView(), "PIN_ENTRY");
        hubContainer.add(createAccountActionView(), "ACTIONS");

        return hubContainer;
    }

    private JPanel createWelcomeView() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        JPanel card = createGlassCard(new Dimension(650, 400));
        
        JLabel title = new JLabel("Welcome to Nexus Bank");
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(ThemeManager.getForeground());
        title.setAlignmentX(CENTER_ALIGNMENT);

        JButton accessBtn = createActionButton("ACCESS SECURE PORTAL", new Color(52, 152, 219));
        accessBtn.addActionListener(e -> hubLayout.show(hubContainer, "PIN_ENTRY"));

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 60)));
        card.add(accessBtn);
        wrapper.add(card);
        return wrapper;
    }

    private JPanel createPinEntryView() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        JPanel card = createGlassCard(new Dimension(450, 450));
        
        JLabel l = new JLabel("Enter Security PIN");
        l.setFont(new Font("SansSerif", Font.BOLD, 20));
        l.setForeground(ThemeManager.getForeground());
        l.setAlignmentX(CENTER_ALIGNMENT);

        JPasswordField pinField = new JPasswordField();
        pinField.setMaximumSize(new Dimension(250, 45));
        pinField.setHorizontalAlignment(JTextField.CENTER);
        pinField.setFont(new Font("SansSerif", Font.BOLD, 24));
        pinField.setBackground(ThemeManager.getCardBackground());
        pinField.setForeground(ThemeManager.getForeground());

        JButton verifyBtn = createActionButton("VERIFY & CONTINUE", new Color(46, 204, 113));
        verifyBtn.addActionListener(e -> {
            hubLayout.show(hubContainer, "ACTIONS");
            pinField.setText("");
        });

        card.add(l);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(pinField);
        card.add(Box.createRigidArea(new Dimension(0, 40)));
        card.add(verifyBtn);
        wrapper.add(card);
        return wrapper;
    }

    private JPanel createAccountActionView() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        JPanel card = createGlassCard(new Dimension(700, 600));

        JLabel l = new JLabel("Secure Transaction Hub");
        l.setFont(new Font("SansSerif", Font.BOLD, 22));
        l.setForeground(ThemeManager.getForeground());
        l.setAlignmentX(CENTER_ALIGNMENT);

        JComboBox<String> accType = new JComboBox<>(new String[]{"Savings Account", "Current Account"});
        accType.setMaximumSize(new Dimension(300, 40));

        // Display savings by default
        balLabel = new JLabel("$" + String.format("%.2f", savingsBalance));
        balLabel.setFont(new Font("SansSerif", Font.BOLD, 55));
        balLabel.setForeground(ThemeManager.getForeground());
        balLabel.setAlignmentX(CENTER_ALIGNMENT);

        accType.addActionListener(e -> {
            boolean isSavings = accType.getSelectedIndex() == 0;
            double val = isSavings ? savingsBalance : currentAccountBalance;
            balLabel.setText("$" + String.format("%.2f", val));
        });

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(500, 60));

        JButton depBtn = createActionButton("DEPOSIT", new Color(46, 204, 113));
        JButton withBtn = createActionButton("WITHDRAW", new Color(231, 76, 60));

        DBOperations ops = new DBOperations();

        depBtn.addActionListener(e -> {
            String val = JOptionPane.showInputDialog(this, "Amount to Deposit:");
            if (val != null) {
                try {
                    double amt = Double.parseDouble(val);
                    boolean isSavings = accType.getSelectedIndex() == 0;
                    if (ops.deposit(accountNo, amt, isSavings)) {
                        syncUI(isSavings);
                    }
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Amount"); }
            }
        });

        withBtn.addActionListener(e -> {
            String val = JOptionPane.showInputDialog(this, "Amount to Withdraw:");
            if (val != null) {
                try {
                    double amt = Double.parseDouble(val);
                    boolean isSavings = accType.getSelectedIndex() == 0;
                    if (ops.withdraw(accountNo, amt, isSavings)) {
                        syncUI(isSavings);
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient Funds in " + accType.getSelectedItem());
                    }
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Amount"); }
            }
        });

        btnPanel.add(depBtn); btnPanel.add(withBtn);

        card.add(l);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(new JLabel("SELECT ACCOUNT TYPE:"));
        card.add(accType);
        card.add(Box.createRigidArea(new Dimension(0, 40)));
        card.add(balLabel);
        card.add(Box.createRigidArea(new Dimension(0, 50)));
        card.add(btnPanel);

        wrapper.add(card);
        return wrapper;
    }

    private JPanel createGlassCard(Dimension size) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
                g2.setColor(ThemeManager.getBorderColor());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 35, 35);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setPreferredSize(size);
        card.setBorder(new EmptyBorder(40, 50, 40, 50));
        return card;
    }

    private void setupSidebar() {
        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(50, 20, 30, 20));

        JLabel brand = new JLabel("NEXUS BANK");
        brand.setFont(new Font("SansSerif", Font.BOLD, 22));
        brand.setForeground(ThemeManager.getForeground());
        brand.setAlignmentX(CENTER_ALIGNMENT);

        sidebar.add(brand);
        sidebar.add(Box.createRigidArea(new Dimension(0, 60)));
        sidebar.add(createNavButton("🏠 Home", "TRANS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(createNavButton("📊 Transactions", "HISTORY"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(createNavButton("👤 My Account", "ACCOUNT"));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createNavButton("🚪 Logout", "LOGOUT"));
    }

    private JButton createNavButton(String text, String pageName) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(240, 50));
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setForeground(ThemeManager.getForeground());
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        b.addActionListener(e -> {
            if (pageName.equals("LOGOUT")) {
                DBConnection.closeConnection(); 
                new BankFrame().setVisible(true);
                dispose();
            } else {
                cardLayout.show(contentPanel, pageName);
                if (pageName.equals("TRANS")) hubLayout.show(hubContainer, "WELCOME");
            }
        });
        return b;
    }

    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(400, 50));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(CENTER_ALIGNMENT);
        return btn;
    }

    private void applyTheme() {
        getContentPane().setBackground(ThemeManager.getBackground());
        sidebar.setBackground(ThemeManager.isDarkMode ? new Color(30, 30, 30) : new Color(240, 240, 240));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.getBorderColor()));
        repaint();
    }
}