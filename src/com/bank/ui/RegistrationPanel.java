package com.bank.ui;

import com.bank.database.DBConnection;
import com.bank.utils.Security;
import com.bank.utils.ThemeManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;

public class RegistrationPanel extends JPanel {
    private JTextField nameField, accField, emailField;
    private JPasswordField pinField;
    private JComboBox<String> typeBox;

    public RegistrationPanel(BankFrame parent) {
        setLayout(new GridBagLayout());

        JPanel regCard = new JPanel() {
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
        regCard.setOpaque(false);
        regCard.setPreferredSize(new Dimension(550, 850)); 
        regCard.setLayout(new BoxLayout(regCard, BoxLayout.Y_AXIS));
        regCard.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("Create Account") {
            @Override public void paint(Graphics g) { setForeground(ThemeManager.getForeground()); super.paint(g); }
        };
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(CENTER_ALIGNMENT);

        // --- INPUTS ---
        nameField = createStyledTextField();
        emailField = createStyledTextField();
        accField = createStyledTextField();
        pinField = createStyledPasswordField();
        
        // --- BEAUTIFIED COMBOBOX ---
        String[] types = {"Savings Account", "Current Account", "Business Account"};
        typeBox = new JComboBox<>(types);
        styleComboBox(typeBox);

        JButton regBtn = new JButton("Open My Account") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(46, 204, 113));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleMainButton(regBtn);
        regBtn.addActionListener(e -> handleRegistration(parent));

        JButton backBtn = new JButton("Already have an account? Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(ThemeManager.isDarkMode ? new Color(255, 255, 255, 30) : new Color(0, 0, 0, 15));
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
        styleSecondaryButton(backBtn);
        backBtn.addActionListener(e -> parent.showPage("LOGIN"));

        regCard.add(title);
        regCard.add(Box.createRigidArea(new Dimension(0, 30)));
        addLabelAndField(regCard, "FULL NAME", nameField);
        addLabelAndField(regCard, "EMAIL ADDRESS", emailField);
        addLabelAndField(regCard, "DESIRED ACCOUNT NUMBER", accField);
        addLabelAndField(regCard, "SET 4-DIGIT PIN", pinField);
        
        regCard.add(createCenteredLabel("ACCOUNT TYPE"));
        regCard.add(Box.createRigidArea(new Dimension(0, 8)));
        regCard.add(typeBox);

        regCard.add(Box.createRigidArea(new Dimension(0, 40)));
        regCard.add(regBtn);
        regCard.add(Box.createRigidArea(new Dimension(0, 15)));
        regCard.add(backBtn);

        add(regCard);
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setMaximumSize(new Dimension(400, 45));
        combo.setAlignmentX(CENTER_ALIGNMENT);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        combo.setFocusable(false);
        combo.setOpaque(false); 

        // 1. Style the Dropdown Popup List
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setBorder(new EmptyBorder(8, 20, 8, 20));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setOpaque(true);
                if (isSelected) {
                    l.setBackground(new Color(52, 152, 219));
                    l.setForeground(Color.WHITE);
                } else {
                    l.setBackground(ThemeManager.isDarkMode ? new Color(45, 45, 45) : Color.WHITE);
                    l.setForeground(ThemeManager.getForeground());
                }
                return l;
            }
        });

        // 2. Override UI to remove default background and white header
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("▼");
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);
                btn.setForeground(ThemeManager.getMutedForeground());
                return btn;
            }

            @Override
            public void paint(Graphics g, JComponent c) {
                // DO NOT CALL super.paint() - this is the key to removing the white box
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background matching text fields
                g2.setColor(ThemeManager.isDarkMode ? new Color(50, 50, 50) : new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                
                // Border
                g2.setColor(ThemeManager.getBorderColor());
                g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);
                g2.dispose();

                // Manually Render the Text
                if (combo.getSelectedItem() != null) {
                    String text = combo.getSelectedItem().toString();
                    g.setFont(combo.getFont());
                    g.setColor(ThemeManager.getForeground());
                    FontMetrics fm = g.getFontMetrics();
                    int x = (c.getWidth() - 30 - fm.stringWidth(text)) / 2; // Subtracting 30 for arrow space
                    int y = (c.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g.drawString(text, x, y);
                }
            }
            
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = (BasicComboPopup) super.createPopup();
                popup.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
                return popup;
            }
        });
        combo.setBorder(new EmptyBorder(0, 10, 0, 10));
    }

    private void addLabelAndField(JPanel panel, String labelStr, JComponent field) {
        panel.add(createCenteredLabel(labelStr));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private JLabel createCenteredLabel(String text) {
        JLabel l = new JLabel(text) {
            @Override public void paint(Graphics g) { setForeground(ThemeManager.getForeground()); super.paint(g); }
        };
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
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
                    g2.setColor(new Color(52, 152, 219)); g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(ThemeManager.getBorderColor()); g2.setStroke(new BasicStroke(1));
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
                    g2.setColor(new Color(52, 152, 219)); g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(ThemeManager.getBorderColor()); g2.setStroke(new BasicStroke(1));
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
        f.setFont(new Font("SansSerif", Font.PLAIN, 15));
        f.setMaximumSize(new Dimension(400, 45));
        f.setCaretColor(new Color(52, 152, 219));
        f.setBorder(new EmptyBorder(5, 15, 5, 15));
        f.setForeground(ThemeManager.getForeground());
        f.addFocusListener(new FocusListener() {
            @Override public void focusGained(FocusEvent e) { f.repaint(); }
            @Override public void focusLost(FocusEvent e) { f.repaint(); }
        });
        return f;
    }

    private void styleMainButton(JButton b) {
        b.setMaximumSize(new Dimension(400, 50));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(CENTER_ALIGNMENT);
    }

    private void styleSecondaryButton(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(300, 40));
    }

    private void handleRegistration(BankFrame parent) {
        String name = nameField.getText();
        String acc = accField.getText();
        String pin = new String(pinField.getPassword());
        if (name.isEmpty() || acc.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return;
        }
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO users (account_no, name, pin, balance) VALUES (?, ?, ?, 0.0)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, acc);
            pstmt.setString(2, name);
            pstmt.setString(3, Security.hashPIN(pin));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Success! Your Nexus account is ready.");
            parent.showPage("LOGIN");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Registration Failed: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        setBackground(ThemeManager.getBackground());
        super.paintComponent(g);
    }
}