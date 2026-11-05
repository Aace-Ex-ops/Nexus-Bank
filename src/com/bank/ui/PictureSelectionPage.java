package com.bank.ui;

import com.bank.utils.ThemeManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class PictureSelectionPage extends JPanel {
    private ProfilePanel parentPanel;

    public PictureSelectionPage(ProfilePanel parent) {
        this.parentPanel = parent;
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
        card.setPreferredSize(new Dimension(500, 420));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel title = new JLabel("Update Profile Picture");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(ThemeManager.getForeground());
        title.setAlignmentX(CENTER_ALIGNMENT);

        JButton galleryBtn = createOptionButton("📁 Choose from Device", new Color(46, 204, 113));
        
        JButton cancelBtn = new JButton("Cancel");
        styleCancelButton(cancelBtn);

        galleryBtn.addActionListener(e -> handleGalleryUpload());
        cancelBtn.addActionListener(e -> parentPanel.showProfileMain());

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 70))); // Increased gap for clean layout
        card.add(galleryBtn);
        card.add(Box.createRigidArea(new Dimension(0, 40)));
        card.add(cancelBtn);

        add(card);
    }

    private void handleGalleryUpload() {
        JFileChooser chooser = new JFileChooser();
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image Files (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Select Profile Picture");

        int returnVal = chooser.showOpenDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            
            parentPanel.updateProfilePicture(path);
        }
    }

    private JButton createOptionButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(400, 50));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(CENTER_ALIGNMENT);
        
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(bg.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(bg);
            }
        });
        
        return b;
    }

    private void styleCancelButton(JButton b) {
        b.setContentAreaFilled(false);
        b.setForeground(ThemeManager.getMutedForeground());
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
    }
}