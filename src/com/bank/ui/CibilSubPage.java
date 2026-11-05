package com.bank.ui;

import com.bank.database.DBConnection;
import com.bank.utils.ThemeManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class CibilSubPage extends JPanel {

    public CibilSubPage(TransactionPanel parent) {
        setLayout(new GridBagLayout());
        setOpaque(false);

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
        card.setPreferredSize(new Dimension(500, 500));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("CIBIL Credit Score");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(ThemeManager.getForeground());
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel desc = new JLabel("Calculated based on your Nexus activity");
        desc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        desc.setForeground(ThemeManager.getMutedForeground());
        desc.setAlignmentX(CENTER_ALIGNMENT);

        int score = calculateMockCibil(parent.getAccNo());

        JLabel scoreLabel = new JLabel(String.valueOf(score));
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 100));
        scoreLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        if (score < 500) scoreLabel.setForeground(new Color(231, 76, 60)); 
        else if (score < 750) scoreLabel.setForeground(new Color(243, 156, 18)); 
        else scoreLabel.setForeground(new Color(46, 204, 113)); 

        JLabel statusLabel = new JLabel(getStatusText(score));
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        statusLabel.setForeground(scoreLabel.getForeground());
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);

        JButton backBtn = new JButton("← Back to Hub");
        styleSecondaryButton(backBtn);
        backBtn.addActionListener(e -> parent.showMenu());

        card.add(title);
        card.add(desc);
        card.add(Box.createRigidArea(new Dimension(0, 50)));
        card.add(scoreLabel);
        card.add(statusLabel);
        card.add(Box.createVerticalGlue());
        card.add(backBtn);

        add(card);
    }

    private int calculateMockCibil(String acc) {
        try (Connection conn = DBConnection.getConnection()) {
            // Logic: 300 base + 15 points per transaction.
            String sql = "SELECT COUNT(*) as txns FROM transactions WHERE account_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, acc);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("txns");
                int score = 300 + (count * 15);
                return Math.min(900, score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 300;
    }

    private String getStatusText(int score) {
        if (score < 500) return "POOR";
        if (score < 750) return "AVERAGE";
        if (score < 850) return "EXCELLENT";
        return "OUTSTANDING";
    }

    private void styleSecondaryButton(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setForeground(ThemeManager.getMutedForeground());
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(CENTER_ALIGNMENT);
    }
}