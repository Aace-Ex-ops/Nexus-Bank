package com.bank.ui;

import com.bank.utils.ThemeManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WelcomePanel extends JPanel {
    
    public WelcomePanel(BankFrame parent) {
        setLayout(new BorderLayout());
        
        // --- HERO SECTION ---
        JPanel heroSection = new JPanel(new GridBagLayout());
        heroSection.setOpaque(false);
        heroSection.setBorder(new EmptyBorder(80, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel headline = new JLabel("Banking Reimagined.");
        headline.setFont(new Font("SansSerif", Font.BOLD, 72));
        headline.setForeground(new Color(41, 128, 185));
        
        JLabel subheadline = new JLabel("<html>Experience the next generation of secure, seamless, and<br>intelligent financial management at your fingertips.</html>") {
            @Override
            public void paint(Graphics g) {
                setForeground(ThemeManager.getMutedForeground());
                super.paint(g);
            }
        };
        subheadline.setFont(new Font("SansSerif", Font.PLAIN, 20));
        subheadline.setBorder(new EmptyBorder(20, 0, 40, 0));

        JButton startBtn = new JButton("Get Started Now");
        startBtn.setPreferredSize(new Dimension(200, 50));
        startBtn.setBackground(new Color(41, 128, 185));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startBtn.addActionListener(e -> parent.showPage("LOGIN"));

        gbc.gridy = 0; heroSection.add(headline, gbc);
        gbc.gridy = 1; heroSection.add(subheadline, gbc);
        gbc.gridy = 2; heroSection.add(startBtn, gbc);

        // --- FEATURES GRID ---
        JPanel featureGrid = new JPanel(new GridLayout(1, 3, 30, 0));
        featureGrid.setOpaque(false);
        featureGrid.setBorder(new EmptyBorder(50, 100, 100, 100));

        featureGrid.add(createFeatureCard("🔒 Secure", "Military-grade encryption for all your transactions."));
        featureGrid.add(createFeatureCard("⚡ Fast", "Instant transfers and real-time balance updates."));
        featureGrid.add(createFeatureCard("📊 Smart", "Insightful analytics to help you grow your wealth."));

        add(heroSection, BorderLayout.NORTH);
        add(featureGrid, BorderLayout.CENTER);
    }

    private JPanel createFeatureCard(String title, String desc) {
        // Special JPanel that refreshes its background color based on theme
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                setBackground(ThemeManager.getCardBackground());
                // Update border color dynamically
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
                    new EmptyBorder(30, 30, 30, 30)
                ));
                super.paintComponent(g);
            }
        };
        
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title) {
            @Override
            public void paint(Graphics g) {
                setForeground(ThemeManager.getForeground());
                super.paint(g);
            }
        };
        t.setFont(new Font("SansSerif", Font.BOLD, 22));
        t.setAlignmentX(LEFT_ALIGNMENT);

        JLabel d = new JLabel("<html>" + desc + "</html>") {
            @Override
            public void paint(Graphics g) {
                setForeground(ThemeManager.getMutedForeground());
                super.paint(g);
            }
        };
        d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        d.setBorder(new EmptyBorder(10, 0, 0, 0));
        d.setAlignmentX(LEFT_ALIGNMENT);

        card.add(t);
        card.add(d);
        return card;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Ensure the main panel background updates
        setBackground(ThemeManager.getBackground());
        super.paintComponent(g);
        
        // Subtle decorative circle in the background
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(41, 128, 185, 20));
        g2d.fillOval(getWidth() - 400, -100, 600, 600);
    }
}