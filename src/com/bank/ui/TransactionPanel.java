package com.bank.ui;

import com.bank.utils.ThemeManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TransactionPanel extends JPanel {
    private CardLayout subCardLayout;
    private JPanel subContentPanel;
    private Dashboard mainDashboard;
    private String accNo;
    
    // Field to keep track of the history page for instant refreshing
    private HistorySubPage historyPage;

    public TransactionPanel(Dashboard parent, String accNo, double balance) {
        this.mainDashboard = parent;
        this.accNo = accNo;

        setLayout(new BorderLayout());
        setOpaque(false);

        subCardLayout = new CardLayout();
        subContentPanel = new JPanel(subCardLayout);
        subContentPanel.setOpaque(false);

        // Initialize sub-pages
        historyPage = new HistorySubPage(this);

        // --- REGISTER SUB-PAGES ---
        subContentPanel.add(createMainMenu(), "MENU");
        subContentPanel.add(new TransferSubPage(this, "ACCOUNT"), "ACC_TRANSFER");
        subContentPanel.add(new TransferSubPage(this, "MOBILE"), "MOBILE_TRANSFER");
        subContentPanel.add(historyPage, "HISTORY"); 
        subContentPanel.add(new CibilSubPage(this), "CIBIL");

        add(subContentPanel, BorderLayout.CENTER);
    }

    /**
     * Triggered by Dashboard or TransferSubPage to refresh the table 
     * without requiring a logout.
     */
    public void reloadHistory() {
        if (historyPage != null) {
            historyPage.loadData();
        }
    }

    private JPanel createMainMenu() {
        JPanel menu = new JPanel(new GridLayout(2, 2, 40, 40));
        menu.setOpaque(false);
        menu.setBorder(new EmptyBorder(50, 60, 50, 60));

        menu.add(createMenuCard("Bank Transfer", "🏠", "ACC_TRANSFER"));
        menu.add(createMenuCard("Mobile Pay", "📱", "MOBILE_TRANSFER"));
        menu.add(createMenuCard("History", "📜", "HISTORY"));
        menu.add(createMenuCard("CIBIL Score", "📈", "CIBIL"));

        return menu;
    }

    private JPanel createMenuCard(String title, String icon, String targetPage) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JButton card = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(ThemeManager.getBorderColor());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        card.setLayout(new GridBagLayout());
        card.setContentAreaFilled(false);
        card.setBorderPainted(false);
        card.setFocusPainted(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        String textColor = ThemeManager.isDarkMode ? "white" : "black";
        
        JLabel textLabel = new JLabel("<html><center>" +
            "<font size='12'>" + icon + "</font><br><br>" +
            "<div style='width: 180px; text-align: center;'>" +
            "<font size='5' color='" + textColor + "'><b>" + title + "</b></font>" +
            "</div></center></html>", SwingConstants.CENTER);
        
        card.add(textLabel);
        card.addActionListener(e -> subCardLayout.show(subContentPanel, targetPage));
        
        container.add(card);
        return container;
    }

    public void showMenu() { subCardLayout.show(subContentPanel, "MENU"); }
    public String getAccNo() { return accNo; }
    
    /**
     * FIX: Passes 'true' to indicate we are fetching the Savings Balance 
     * for transfer operations by default.
     */
    public double getBalance() { 
        return mainDashboard.getCurrentBalance(true); 
    }
    
    public Dashboard getMainDashboard() { return mainDashboard; }
}