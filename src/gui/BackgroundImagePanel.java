package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class BackgroundImagePanel extends JPanel {
    private Image backgroundImage;

    public BackgroundImagePanel(String imagePath) {
        setLayout(new BorderLayout());
        try {
            // First attempt: load as classpath resource
            java.net.URL resUrl = getClass().getResource(imagePath);
            if (resUrl == null && !imagePath.startsWith("/")) {
                resUrl = getClass().getResource("/gui/" + imagePath);
            }
            if (resUrl == null && !imagePath.startsWith("/")) {
                resUrl = getClass().getResource("/" + imagePath);
            }

            if (resUrl != null) {
                backgroundImage = new ImageIcon(resUrl).getImage();
            } else {
                // Second attempt: check relative or absolute path on disk
                File file = new File(imagePath);
                if (!file.exists()) {
                    file = new File("src/gui/" + new File(imagePath).getName());
                }
                if (!file.exists()) {
                    file = new File("src/gui/money_bg.jpg");
                }
                if (file.exists()) {
                    backgroundImage = new ImageIcon(file.getAbsolutePath()).getImage();
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Soft fallback background color if image cannot be found
            g.setColor(new Color(230, 240, 250));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}


