package com.bank.ui;

import com.bank.utils.ThemeManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BankFrame extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel navBar;
    private JLabel logo;
    private JPanel menuPanel;
    private JButton loginNavBtn; 

    public BankFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Nexus Bank | Digital Financial Services");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        setupNavBar();

        cardPanel.add(new WelcomePanel(this), "WELCOME");
        cardPanel.add(new LoginPanel(this), "LOGIN");
        cardPanel.add(new SignupPanel(this), "SIGNUP");

        add(navBar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        applyCurrentTheme();
        setVisible(true);
    }

    public void showLogin() {
        cardLayout.show(cardPanel, "LOGIN");
    }

    public void showSignup() {
        cardLayout.show(cardPanel, "SIGNUP");
    }

    public void showPage(String pageName) {
        cardLayout.show(cardPanel, pageName);
    }

    private void setupNavBar() {
        navBar = new JPanel(new BorderLayout());
        navBar.setPreferredSize(new Dimension(getWidth(), 80));
        
        logo = new JLabel(" NEXUS BANK");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setBorder(new EmptyBorder(0, 50, 0, 0));

        menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 28));
        menuPanel.setOpaque(false);
        String[] menus = {"Home", "About", "Services", "Contact"};
        for (String m : menus) {
            JLabel item = new JLabel(m);
            item.setFont(new Font("SansSerif", Font.PLAIN, 15));
            item.setCursor(new Cursor(Cursor.HAND_CURSOR));
            menuPanel.add(item);
        }

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(0, 0, 0, 50));

        JButton themeBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isRollover()) {
                    g2.setColor(ThemeManager.isDarkMode ? new Color(255, 255, 255, 30) : new Color(0, 0, 0, 20));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                }
                
                g2.setColor(ThemeManager.getBorderColor());
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);

                g2.setColor(ThemeManager.getForeground());
                g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
                FontMetrics fm = g2.getFontMetrics();
                String icon = ThemeManager.isDarkMode ? "☀️" : "🌙";
                int x = (getWidth() - fm.stringWidth(icon)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(icon, x, y);

                g2.dispose();
            }
        };

        themeBtn.setPreferredSize(new Dimension(45, 45));
        themeBtn.setContentAreaFilled(false);
        themeBtn.setBorderPainted(false);
        themeBtn.setFocusPainted(false);
        themeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeBtn.addActionListener(e -> toggleTheme());

        loginNavBtn = new JButton("PORTAL LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color btnBg = ThemeManager.isDarkMode ? new Color(52, 152, 219) : new Color(41, 128, 185);
                g2.setColor(btnBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        loginNavBtn.setContentAreaFilled(false);
        loginNavBtn.setFocusPainted(false);
        loginNavBtn.setBorderPainted(false);
        loginNavBtn.setForeground(Color.WHITE);
        loginNavBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginNavBtn.setPreferredSize(new Dimension(140, 42));
        loginNavBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginNavBtn.addActionListener(e -> showPage("LOGIN"));

        actionPanel.add(themeBtn);
        actionPanel.add(loginNavBtn);

        navBar.add(logo, BorderLayout.WEST);
        navBar.add(menuPanel, BorderLayout.CENTER);
        navBar.add(actionPanel, BorderLayout.EAST);
    }

    private void toggleTheme() {
        ThemeManager.isDarkMode = !ThemeManager.isDarkMode;
        applyCurrentTheme();
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void applyCurrentTheme() {
        Color fg = ThemeManager.getForeground();
        Color navBg = ThemeManager.isDarkMode ? new Color(25, 25, 25) : Color.WHITE;
        Color borderCol = ThemeManager.isDarkMode ? new Color(50, 50, 50) : new Color(230, 230, 230);

        navBar.setBackground(navBg);
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderCol));
        logo.setForeground(fg);
        
        for (Component c : menuPanel.getComponents()) {
            if (c instanceof JLabel) {
                c.setForeground(fg);
            }
        }
        
        cardPanel.setBackground(ThemeManager.getBackground());
        navBar.repaint();
    }
}