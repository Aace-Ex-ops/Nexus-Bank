package com.bank.ui;

import com.bank.database.DBConnection;
import com.bank.utils.ThemeManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class ProfilePanel extends JPanel {
    private String accountNo;
    private JTextField nameField, mobileField, addressField, typeField, emailField;
    private JLabel avatarLabel;
    private String[] avatars = {"👤", "👨‍💻", "👩‍💼", "🏦", "🛡️", "😎"};
    private int currentAvatarIdx = 0;

    // Navigation Components
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private Dashboard dashboard;

    public ProfilePanel(Dashboard parent, String acc, String initialName) {
        this.dashboard = parent;
        this.accountNo = acc;
        
        setLayout(new BorderLayout());
        setOpaque(false);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setOpaque(false);

        // --- REGISTER PAGES ---
        mainContainer.add(createProfileMainView(parent, initialName), "MAIN");
        mainContainer.add(new PictureSelectionPage(this), "PIC_SELECT");

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createProfileMainView(Dashboard parent, String initialName) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setColor(ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(550, 800)); // Increased height for the Email field
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- 1. TOP SECTION (Avatar) ---
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);

        avatarLabel = new JLabel(avatars[currentAvatarIdx]);
        avatarLabel.setFont(new Font("SansSerif", Font.PLAIN, 80));
        avatarLabel.setAlignmentX(CENTER_ALIGNMENT);
        avatarLabel.setPreferredSize(new Dimension(120, 120));
        
        JButton changePicBtn = new JButton("Change Profile Picture");
        styleGhostButton(changePicBtn);
        changePicBtn.addActionListener(e -> cardLayout.show(mainContainer, "PIC_SELECT"));

        topSection.add(avatarLabel);
        topSection.add(changePicBtn);
        topSection.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- 2. CENTER SECTION (The Aligned Form) ---
        JPanel formPanel = new JPanel(new GridLayout(6, 1, 0, 10)); // 6 rows for 6 fields
        formPanel.setOpaque(false);

        nameField = createStyledEditField(initialName);
        emailField = createStyledEditField("");
        mobileField = createStyledEditField("");
        addressField = createStyledEditField("");
        
        // Permanent Account Type (Read-Only)
        typeField = createStyledEditField("");
        typeField.setEditable(false);
        typeField.setForeground(ThemeManager.getMutedForeground());
        
        // Permanent Account Number
        JTextField accDisplay = new JTextField(accountNo);
        accDisplay.setEditable(false);
        accDisplay.setOpaque(false);
        accDisplay.setBorder(null);
        accDisplay.setForeground(ThemeManager.getMutedForeground());
        accDisplay.setFont(new Font("SansSerif", Font.BOLD, 15));

        formPanel.add(createInputGroup("FULL NAME", nameField));
        formPanel.add(createInputGroup("EMAIL ID", emailField));
        formPanel.add(createInputGroup("ACCOUNT TYPE (PERMANENT)", typeField));
        formPanel.add(createInputGroup("MOBILE NUMBER", mobileField));
        formPanel.add(createInputGroup("RESIDENTIAL ADDRESS", addressField));
        formPanel.add(createInputGroup("ACCOUNT NUMBER", accDisplay));

        fetchAndUpdateData();

        // --- 3. BOTTOM SECTION (Actions) ---
        JPanel bottomSection = new JPanel();
        bottomSection.setLayout(new BoxLayout(bottomSection, BoxLayout.Y_AXIS));
        bottomSection.setOpaque(false);

        JButton saveBtn = new JButton("Save Profile Changes");
        stylePrimaryButton(saveBtn);
        saveBtn.addActionListener(e -> handleUpdate());

        JButton deleteBtn = new JButton("Delete Nexus Account");
        styleDeleteButton(deleteBtn);
        deleteBtn.addActionListener(e -> handleDeleteAccount(parent));

        bottomSection.add(saveBtn);
        bottomSection.add(Box.createRigidArea(new Dimension(0, 12)));
        bottomSection.add(deleteBtn);

        card.add(topSection, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        card.add(bottomSection, BorderLayout.SOUTH);

        wrapper.add(card);
        return wrapper;
    }

    public void showProfileMain() { cardLayout.show(mainContainer, "MAIN"); }

    public void updateProfilePicture(String path) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE users SET profile_pic_path = ? WHERE account_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, path);
            ps.setString(2, accountNo);
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Profile Picture Linked Successfully!");
                loadAvatar(path);
                showProfileMain();
            }
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "System Error: " + e.getMessage()); }
    }

    private void loadAvatar(String path) {
        if (path != null && !path.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    avatarLabel.setIcon(new ImageIcon(img));
                    avatarLabel.setText("");
                    return;
                }
            } catch (Exception e) { System.err.println("Load error: " + e.getMessage()); }
        }
        avatarLabel.setIcon(null);
        avatarLabel.setText(avatars[currentAvatarIdx]);
    }

    private void fetchAndUpdateData() {
        try (Connection conn = DBConnection.getConnection()) {
            // Updated to fetch Email and Account Type
            String sql = "SELECT name, email, mobile, address, account_type, profile_pic_path FROM users WHERE account_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email") != null ? rs.getString("email") : "");
                mobileField.setText(rs.getString("mobile") != null ? rs.getString("mobile") : "");
                addressField.setText(rs.getString("address") != null ? rs.getString("address") : "");
                typeField.setText(rs.getString("account_type") != null ? rs.getString("account_type") : "Savings Account");
                
                loadAvatar(rs.getString("profile_pic_path"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void handleUpdate() {
        try (Connection conn = DBConnection.getConnection()) {
            // Note: account_type is NOT included in the update to keep it permanent
            String sql = "UPDATE users SET name = ?, email = ?, mobile = ?, address = ? WHERE account_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nameField.getText().trim());
            ps.setString(2, emailField.getText().trim());
            ps.setString(3, mobileField.getText().trim());
            ps.setString(4, addressField.getText().trim());
            ps.setString(5, accountNo);
            
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Nexus Profile Synced Successfully!");
            }
        } catch (SQLException e) { 
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); 
        }
    }

    private void handleDeleteAccount(Dashboard parent) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you absolutely sure? This will erase all transaction history and balances forever.", 
            "Critical Security Warning", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);
                PreparedStatement ps1 = conn.prepareStatement("DELETE FROM transactions WHERE account_no = ?");
                ps1.setString(1, accountNo); 
                ps1.executeUpdate();
                
                PreparedStatement ps2 = conn.prepareStatement("DELETE FROM users WHERE account_no = ?");
                ps2.setString(1, accountNo);
                
                if (ps2.executeUpdate() > 0) {
                    conn.commit();
                    DBConnection.closeConnection();
                    new BankFrame().setVisible(true);
                    parent.dispose();
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private JPanel createInputGroup(String labelText, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setForeground(ThemeManager.getMutedForeground());
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JTextField createStyledEditField(String val) {
        JTextField f = new JTextField(val);
        f.setFont(new Font("SansSerif", Font.PLAIN, 15));
        f.setBackground(ThemeManager.getCardBackground());
        f.setForeground(ThemeManager.getForeground());
        f.setCaretColor(ThemeManager.getCaretColor());
        f.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()));
        return f;
    }

    private void stylePrimaryButton(JButton b) {
        b.setPreferredSize(new Dimension(500, 45));
        b.setMaximumSize(new Dimension(Short.MAX_VALUE, 45));
        b.setBackground(new Color(46, 204, 113));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleDeleteButton(JButton b) {
        b.setPreferredSize(new Dimension(500, 45));
        b.setMaximumSize(new Dimension(Short.MAX_VALUE, 45));
        b.setOpaque(true);
        b.setBackground(new Color(231, 76, 60));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false); b.setBorderPainted(false);
    }

    private void styleGhostButton(JButton b) {
        b.setContentAreaFilled(false);
        b.setForeground(ThemeManager.getMutedForeground());
        b.setFont(new Font("SansSerif", Font.BOLD, 11));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(CENTER_ALIGNMENT);
    }
}