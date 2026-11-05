package com.bank.ui;

import com.bank.database.DBConnection;
import com.bank.utils.ThemeManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class HistorySubPage extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private String currentAccount;

    public HistorySubPage(TransactionPanel parent) {
        this.currentAccount = parent.getAccNo();
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Transaction History") {
            @Override public void paint(Graphics g) { setForeground(ThemeManager.getForeground()); super.paint(g); }
        };
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        JButton backBtn = new JButton("← Back to Hub");
        styleNavButton(backBtn);
        backBtn.addActionListener(e -> parent.showMenu());

        header.add(title, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);

        String[] cols = {"TXN ID", "Type", "Amount", "Reference", "Date"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        table = new JTable(model);
        setupTableStyling();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
        scrollPane.getViewport().setBackground(ThemeManager.getBackground());

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    private void setupTableStyling() {
        table.setRowHeight(45);
        table.setOpaque(false);
        table.setBackground(ThemeManager.getCardBackground());
        table.setForeground(ThemeManager.getForeground());
        table.setGridColor(ThemeManager.getBorderColor());
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(ThemeManager.getSelectionColor());
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(ThemeManager.getBackground());
        table.getTableHeader().setForeground(ThemeManager.getForeground());
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                
                comp.setForeground(ThemeManager.getForeground());

                if (c == 2 && v != null) {
                    String amountStr = v.toString();
                    if (amountStr.startsWith("+")) {
                        comp.setForeground(new Color(46, 204, 113)); // Green
                    } else if (amountStr.startsWith("-")) {
                        comp.setForeground(new Color(231, 76, 60)); // Red
                    }
                }
                
                setBackground(isS ? ThemeManager.getSelectionColor() : ThemeManager.getCardBackground());
                return comp;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    public void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM transactions WHERE account_no = ? ORDER BY trans_date DESC";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, currentAccount);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String tid = rs.getString("transaction_id");
                String type = rs.getString("type").toUpperCase();
                double amt = rs.getDouble("amount");
                String ref = rs.getString("related_account");
                Timestamp date = rs.getTimestamp("trans_date");
                
                // FORCE SIGN LOGIC: Withdrawals and Transfers are always negative
                String symbol;
                if (type.contains("WITHDRAW") || type.contains("TRANSFER")) {
                    symbol = "-";
                } else if (type.contains("DEPOSIT") || type.contains("RECEIVED")) {
                    symbol = "+";
                } else {
                    symbol = (amt >= 0) ? "+" : "-";
                }

                String formattedAmt = symbol + String.format("$%.2f", Math.abs(amt));

                // REFERENCE LOGIC: Handle null/empty for withdrawals/deposits
                String referenceDisplay;
                if (ref != null && !ref.trim().isEmpty()) {
                    referenceDisplay = ref;
                } else if (type.contains("WITHDRAW")) {
                    referenceDisplay = "Cash Out / ATM";
                } else if (type.contains("DEPOSIT")) {
                    referenceDisplay = "Self Deposit";
                } else {
                    referenceDisplay = "Nexus System";
                }

                model.addRow(new Object[]{
                    tid != null ? tid : "N/A",
                    type,
                    formattedAmt,
                    referenceDisplay,
                    date
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void styleNavButton(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setForeground(ThemeManager.getForeground());
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}