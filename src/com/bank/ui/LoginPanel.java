package com.bank.ui;

import com.bank.database.DBConnection;
import com.bank.utils.ThemeManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;

public class LoginPanel extends JPanel {
    private JTextField userField; // Changed from accField to userField
    private JPasswordField passField; // Changed from pinField to passField

    public LoginPanel(BankFrame parent) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        
        // --- THE GLASS CARD ---
        JPanel loginCard = new JPanel() {
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
        loginCard.setOpaque(false);
        loginCard.setPreferredSize(new Dimension(450, 680));
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBorder(new EmptyBorder(50, 50, 50, 50));

        // --- HEADER ---
        JLabel title = new JLabel("Welcome Back") {
            @Override public void paint(Graphics g) { setForeground(ThemeManager.getForeground()); super.paint(g); }
        };
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("Securely access your Nexus account") {
            @Override public void paint(Graphics g) { setForeground(ThemeManager.getMutedForeground()); super.paint(g); }
        };
        subTitle.setAlignmentX(CENTER_ALIGNMENT);

        // --- INPUTS ---
        userField = createStyledTextField();
        passField = createStyledPasswordField();
        Dimension fieldSize = new Dimension(350, 50); 
        userField.setMaximumSize(fieldSize);
        passField.setMaximumSize(fieldSize);

        // --- LOGIN BUTTON ---
        JButton loginBtn = new JButton("Sign In to Portal") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.isDarkMode ? new Color(52, 152, 219) : new Color(41, 128, 185));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleMainButton(loginBtn);
        loginBtn.addActionListener(e -> handleLogin(parent));

        // --- REGISTRATION LINK ---
        JButton regBtn = new JButton("New user? Create an account") {
            @Override public void paint(Graphics g) {
                setForeground(ThemeManager.isDarkMode ? new Color(133, 193, 233) : new Color(41, 128, 185));
                super.paint(g);
            }
        };
        regBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        regBtn.setContentAreaFilled(false);
        regBtn.setBorderPainted(false);
        regBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regBtn.setAlignmentX(CENTER_ALIGNMENT);
        regBtn.addActionListener(e -> parent.showSignup());

        // --- BACK BUTTON ---
        JButton backBtn = new JButton("← Return to Homepage") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(ThemeManager.isDarkMode ? new Color(255, 255, 255, 40) : new Color(0, 0, 0, 15));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public void paint(Graphics g) {
                setForeground(ThemeManager.isDarkMode ? new Color(133, 193, 233) : new Color(52, 73, 94));
                super.paint(g);
            }
        };
        styleBackButton(backBtn);
        backBtn.addActionListener(e -> parent.showPage("WELCOME"));

        // --- ASSEMBLY ---
        loginCard.add(title);
        loginCard.add(Box.createRigidArea(new Dimension(0, 10)));
        loginCard.add(subTitle);
        loginCard.add(Box.createRigidArea(new Dimension(0, 60)));
        
        loginCard.add(createCenteredLabel("USERNAME"));
        loginCard.add(Box.createRigidArea(new Dimension(0, 10)));
        loginCard.add(userField);
        
        loginCard.add(Box.createRigidArea(new Dimension(0, 25)));
        
        loginCard.add(createCenteredLabel("PASSWORD"));
        loginCard.add(Box.createRigidArea(new Dimension(0, 10)));
        loginCard.add(passField);
        
        loginCard.add(Box.createRigidArea(new Dimension(0, 50)));
        loginCard.add(loginBtn);
        loginCard.add(Box.createRigidArea(new Dimension(0, 10)));
        loginCard.add(regBtn);
        loginCard.add(Box.createRigidArea(new Dimension(0, 30)));
        loginCard.add(backBtn);

        add(loginCard);
    }

    private JLabel createCenteredLabel(String text) {
        JLabel l = new JLabel(text) {
            @Override public void paint(Graphics g) { setForeground(ThemeManager.getForeground()); super.paint(g); }
        };
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.isDarkMode ? new Color(50, 50, 50) : new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (hasFocus()) {
                    g2.setColor(new Color(52, 152, 219));
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(ThemeManager.getBorderColor());
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        return setupField(field);
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.isDarkMode ? new Color(50, 50, 50) : new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (hasFocus()) {
                    g2.setColor(new Color(52, 152, 219));
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(ThemeManager.getBorderColor());
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        return (JPasswordField) setupField(field);
    }

    private JTextField setupField(JTextField f) {
        f.setOpaque(false); 
        f.setHorizontalAlignment(JTextField.CENTER);
        f.setFont(new Font("SansSerif", Font.PLAIN, 16));
        f.setBorder(new EmptyBorder(5, 15, 5, 15));
        f.setForeground(ThemeManager.getForeground());
        f.setCaretColor(ThemeManager.getCaretColor());
        f.setSelectionColor(ThemeManager.getSelectionColor());
        f.addFocusListener(new FocusListener() {
            @Override public void focusGained(FocusEvent e) { 
                f.repaint(); 
            }
            @Override public void focusLost(FocusEvent e) { f.repaint(); }
        });
        return f;
    }

    private void styleMainButton(JButton b) {
        b.setMaximumSize(new Dimension(350, 50));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(CENTER_ALIGNMENT);
    }

    private void styleBackButton(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(250, 40));
    }

    private void handleLogin(BankFrame parent) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if(username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Username and Password");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // UPDATED: Check against username and password columns
            String sql = "SELECT account_no, name FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String accNo = rs.getString("account_no");
                String name = rs.getString("name");
                new Dashboard(accNo, name, 0.0).setVisible(true);
                parent.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Login Error: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        setBackground(ThemeManager.getBackground());
        super.paintComponent(g);
    }
}