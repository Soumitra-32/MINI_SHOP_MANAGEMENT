package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Centralized Modern UI Theme and styling utility for the application.
 * Provides consistent typography, colors, borders, and component factories.
 */
public final class ModernTheme {

    private ModernTheme() {} // Utility class

    // ---- Rich professional palette (Indigo + Violet + Cyan accents) ----
    public static final Color PRIMARY = new Color(79, 70, 229);        // Indigo 600
    public static final Color PRIMARY_DARK = new Color(67, 56, 202);   // Indigo 700
    public static final Color PRIMARY_HOVER = new Color(55, 48, 163);
    public static final Color VIOLET = new Color(124, 58, 237);
    public static final Color ACCENT = new Color(6, 182, 212);         // Cyan 500
    public static final Color ACCENT_DARK = new Color(8, 145, 178);

    public static final Color SUCCESS = new Color(16, 185, 129);
    public static final Color SUCCESS_HOVER = new Color(5, 150, 105);
    public static final Color DANGER = new Color(239, 68, 68);
    public static final Color DANGER_HOVER = new Color(220, 38, 38);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color PINK = new Color(236, 72, 153);
    public static final Color SKY = new Color(14, 165, 233);
    public static final Color SLATE = new Color(100, 116, 139);
    public static final Color SLATE_DARK = new Color(71, 85, 105);

    public static final Color BG_LIGHT = new Color(235, 240, 255);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_MAIN = new Color(15, 23, 42);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    public static final Color BORDER_COLOR = new Color(203, 213, 225);
    public static final Color TABLE_HEADER_BG = new Color(49, 46, 129);
    public static final Color TABLE_HEADER_FG = Color.WHITE;
    public static final Color TABLE_ALT_ROW = new Color(238, 242, 255);
    public static final Color FIELD_BG = new Color(248, 250, 252);

    // ---- Standardized window sizes ----
    public static final int WIN_MAIN_W = 1024;
    public static final int WIN_MAIN_H = 640;
    public static final int WIN_AUTH_W = 540;
    public static final int WIN_AUTH_H = 640;
    public static final int WIN_HOME_W = 720;
    public static final int WIN_HOME_H = 620;
    public static final int WIN_DIALOG_W = 720;
    public static final int WIN_DIALOG_H = 560;

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    public static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Label.font", FONT_REGULAR);
            UIManager.put("Button.font", FONT_BUTTON);
            UIManager.put("Table.font", FONT_REGULAR);
            UIManager.put("TableHeader.font", FONT_BOLD);
            UIManager.put("ComboBox.font", FONT_REGULAR);
            UIManager.put("TextField.font", FONT_REGULAR);
        } catch (Exception ignored) {}
    }

    /** Hard floor for resizing; never applied above the window's own requested size. */
    public static final int MIN_W = 760;
    public static final int MIN_H = 480;

    public static void setupWindow(JFrame f, String title, int w, int h, int closeOp) {
        f.setTitle(title);
        f.setSize(w, h);
        // Clamp the minimum to the requested size, otherwise a compact auth/home
        // window would be silently widened to MIN_W by the window manager.
        f.setMinimumSize(new Dimension(Math.min(w, MIN_W), Math.min(h, MIN_H)));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(closeOp);
        f.getContentPane().setBackground(BG_LIGHT);
    }

    public static JPanel createHeader(String title, String subtitle, Color c1, Color c2) {
        JPanel header = new GradientPanel(c1, c2);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(22, 30, 22, 30));
        JLabel t = new JLabel(title);
        t.setFont(FONT_HEADER);
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitle);
        s.setFont(FONT_REGULAR);
        s.setForeground(new Color(255, 255, 255, 230));
        JPanel wrap = new JPanel(new GridLayout(2, 1, 0, 4));
        wrap.setOpaque(false);
        wrap.add(t);
        wrap.add(s);
        header.add(wrap, BorderLayout.WEST);
        return header;
    }

    public static JPanel createCard(int padding) {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(padding, padding, padding, padding)));
        return card;
    }

    public static JPanel createToolbar() {
        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        tb.setBackground(Color.WHITE);
        tb.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(4, 12, 4, 12)));
        return tb;
    }

    public static JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));

        Color hoverColor = bg.darker();
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_REGULAR);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_REGULAR);
        pf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    public static JButton createOutlineButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setBackground(Color.WHITE);
        b.setForeground(PRIMARY);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PRIMARY, 1, true), new EmptyBorder(8, 16, 8, 16)));
        return b;
    }

    public static void styleCombo(JComboBox<?> combo) {
        combo.setFont(FONT_REGULAR);
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_MAIN);
        combo.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        combo.setPreferredSize(new Dimension(150, 34));
        combo.setFocusable(false);
    }

    public static JPanel createToolbarOld() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(6, 12, 6, 12)));
        return p;
    }

    public static JLabel statValue(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 22));
        l.setForeground(color);
        return l;
    }

    public static JLabel statTitle(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(FONT_REGULAR);
        table.setSelectionBackground(new Color(199, 210, 254));
        table.setSelectionForeground(TEXT_MAIN);
        table.setGridColor(BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 2));
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_FG);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT_ROW);
                }
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
    }

    public static class GradientPanel extends JPanel {
        private final Color c1, c2;
        public GradientPanel(Color c1, Color c2) { this.c1 = c1; this.c2 = c2; }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
