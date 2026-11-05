package com.bank.ui;

import com.bank.database.DBConnection;
import com.bank.utils.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.sql.*;

public class SignupPanel extends JPanel {

    private JTextField nameField, userField, accField, mobileField, emailField;
    private JPasswordField passField, pinField;
    private JComboBox<String> typeBox;
    private JTextArea addressField;
    private JLabel title; 
    private BankFrame parentFrame;

    public SignupPanel(BankFrame parent) {
    this.parentFrame = parent;
    
    // 🔥 FORCE GLOBAL PANEL TEXT COLOR
    UIManager.put("Label.foreground", ThemeManager.isDarkMode ? Color.WHITE : Color.BLACK);
    UIManager.put("ComboBox.foreground", ThemeManager.isDarkMode ? Color.WHITE : Color.BLACK);

    setLayout(new BorderLayout());
    setOpaque(false);

        // Wrapper to center the card inside the scrollable area
        JPanel scrollContent = new JPanel(new GridBagLayout());
        scrollContent.setOpaque(false);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                super.paintComponent(g);

                g2.setColor(ThemeManager.getBorderColor());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);

                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setLayout(new BorderLayout(0, 25));
        card.setPreferredSize(new Dimension(850, 1150)); 
        card.setBorder(new EmptyBorder(30, 60, 30, 60));

        // 🔥 FIXED TITLE: Forced White in Dark Mode
        title = new JLabel("Join Nexus Bank", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 42));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 30, 12, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 
        gbc.gridx = 0;

        nameField   = createStyledField();
        userField   = createStyledField();
        passField   = createStyledPasswordField();
        emailField  = createStyledField();
        accField    = createStyledField();
        mobileField = createStyledField();
        pinField    = createStyledPasswordField();

        typeBox = new JComboBox<>(new String[]{"Savings Account", "Current Account"});
        styleDropdown(typeBox);

        addFormItem(formGrid, "FULL NAME",       nameField,   gbc, 0);
        addFormItem(formGrid, "ACCOUNT TYPE",   typeBox,     gbc, 1);
        addFormItem(formGrid, "LOGIN USERNAME", userField,   gbc, 2);
        addFormItem(formGrid, "ACCOUNT NUMBER", accField,    gbc, 3);
        addFormItem(formGrid, "LOGIN PASSWORD", passField,   gbc, 4);
        addFormItem(formGrid, "MOBILE NUMBER",  mobileField, gbc, 5);
        addFormItem(formGrid, "EMAIL ADDRESS",  emailField,  gbc, 6);
        addFormItem(formGrid, "SECURITY PIN",   pinField,    gbc, 7);

        gbc.gridy = 8;
        gbc.weighty = 1;
        formGrid.add(Box.createVerticalGlue(), gbc);

        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);

        addressField = new JTextArea(2, 20);
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        addressField.setOpaque(false);
        addressField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        addressField.setMargin(new Insets(10, 20, 10, 20));

        addAddressSection(footer, "RESIDENTIAL ADDRESS", addressField);

        JButton signupBtn = new JButton("CREATE NEXUS ACCOUNT") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(46, 204, 113));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
                g2.dispose();
            }
        };

        styleBtn(signupBtn, true);
        signupBtn.addActionListener(e -> handleRegistration());

        JButton backBtn = new JButton("Already have an account? Login");
        styleBtn(backBtn, false);
        backBtn.addActionListener(e -> parentFrame.showLogin());

        footer.add(signupBtn);
        footer.add(Box.createRigidArea(new Dimension(0, 10)));
        footer.add(backBtn);

        card.add(title, BorderLayout.NORTH);
        card.add(formGrid, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        scrollContent.add(card);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); 

        add(scrollPane, BorderLayout.CENTER);
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.isDarkMode ? new Color(40,40,40) : new Color(245,245,245));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                super.paintComponent(g);
                g2.setColor(ThemeManager.getBorderColor());
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(10,15,10,15));
        f.setFont(new Font("SansSerif", Font.PLAIN, 15));
        f.setPreferredSize(new Dimension(600, 65)); 
        return f;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField f = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.isDarkMode ? new Color(40,40,40) : new Color(245,245,245));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                super.paintComponent(g);
                g2.setColor(ThemeManager.getBorderColor());
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(10,15,10,15));
        f.setFont(new Font("SansSerif", Font.PLAIN, 15));
        f.setPreferredSize(new Dimension(600, 65));
        return f;
    }

    private void styleDropdown(JComboBox<String> box) {
        box.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton();
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                return btn;
            }

            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(ThemeManager.isDarkMode ? new Color(40, 40, 40) : new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                
                // 🔥 FORCE HEAD TEXT WHITE HERE
                String text = box.getSelectedItem() != null ? box.getSelectedItem().toString() : "";
                g2.setColor(ThemeManager.isDarkMode ? Color.WHITE : Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
                FontMetrics fm = g2.getFontMetrics();
                int textY = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(text, 15, textY);
                
                g2.setColor(ThemeManager.getBorderColor());
                g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);
                g2.dispose();
            }

            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox);
                popup.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
                return popup;
            }
        });

        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setFont(new Font("SansSerif", Font.PLAIN, 15));
                
                // Force theme colors in the list
                l.setForeground(ThemeManager.isDarkMode ? Color.WHITE : Color.BLACK);

                if (index != -1) { 
                    l.setOpaque(true);
                    l.setBackground(isSelected ? ThemeManager.getBorderColor() : (ThemeManager.isDarkMode ? new Color(40, 40, 40) : new Color(245, 245, 245)));
                } else {
                    l.setOpaque(false);
                }
                l.setBorder(new EmptyBorder(10, 15, 10, 15));
                return l;
            }
        });

        box.setOpaque(false);
        box.setPreferredSize(new Dimension(600, 65)); 
    }

    private void addFormItem(JPanel panel, String labelText, JComponent comp, GridBagConstraints gbc, int y) {
        gbc.gridy = y;
        JPanel item = new JPanel(new BorderLayout(0,6));
        item.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(ThemeManager.getMutedForeground());
        item.add(label, BorderLayout.NORTH);
        item.add(comp, BorderLayout.CENTER);
        panel.add(item, gbc);
    }

    private void addAddressSection(JPanel panel, String label, JTextArea area) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(ThemeManager.getMutedForeground());
        l.setAlignmentX(CENTER_ALIGNMENT);
        JPanel wrapper = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.isDarkMode ? new Color(40,40,40) : new Color(245,245,245));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(ThemeManager.getBorderColor());
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(690,110));
        wrapper.add(area);
        panel.add(l);
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        panel.add(wrapper);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
    }

    private void styleBtn(JButton b, boolean main){
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, main ? 16 : 13));
        b.setForeground(main ? Color.WHITE : (ThemeManager.isDarkMode ? Color.WHITE : Color.BLACK));
    }

    private void handleRegistration() {
        String name    = nameField.getText().trim();
        String user    = userField.getText().trim();
        String pass    = new String(passField.getPassword()).trim();
        String email   = emailField.getText().trim();
        String acc     = accField.getText().trim();
        String pin     = new String(pinField.getPassword()).trim();
        String mobile  = mobileField.getText().trim();
        String addr    = addressField.getText().trim();
        String accType = (String) typeBox.getSelectedItem();

        if (name.isEmpty() || user.isEmpty() || pass.isEmpty() || acc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty.");
            return;
        }

        String sql = "INSERT INTO users (account_no, name, username, password, pin, email, mobile, address, account_type, savings_balance, current_balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, acc);    pstmt.setString(2, name);   pstmt.setString(3, user);
            pstmt.setString(4, pass);   pstmt.setString(5, pin);    pstmt.setString(6, email);
            pstmt.setString(7, mobile); pstmt.setString(8, addr);   pstmt.setString(9, accType);
            pstmt.setDouble(10, 0.0);   pstmt.setDouble(11, 0.0);
            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Success!");
                parentFrame.showLogin();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

   @Override
    public void addNotify() {
        super.addNotify();
        // Re-verify the theme on show
        Color fg = ThemeManager.isDarkMode ? Color.WHITE : Color.BLACK;
        title.setForeground(fg);
        typeBox.setForeground(fg);
        
        // Refresh the components
        SwingUtilities.updateComponentTreeUI(this); 
    }
}