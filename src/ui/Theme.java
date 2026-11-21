package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

// Theme: semua warna, font, dan jarak dikumpulkan di sini
// Owner: Hanif Rafa Ramidzar
// Materi: variables, GUI
// [MATERI: Variables - static final]
public class Theme {

    // warna
    public static final Color BACKGROUND = new Color(30, 30, 46);     // #1E1E2E
    public static final Color PANEL = new Color(49, 50, 68);          // #313244
    public static final Color TEXT = new Color(205, 214, 244);        // #CDD6F4
    public static final Color TEXT_MUTED = new Color(147, 153, 178);  // #9399B2
    public static final Color ACCENT = new Color(137, 180, 250);      // #89B4FA
    public static final Color CORRECT = new Color(166, 227, 161);     // #A6E3A1
    public static final Color WRONG = new Color(243, 139, 168);       // #F38BA8

    // heatmap dari putih (tidak salah) ke merah tua (paling sering salah)
    public static final Color HEAT_NONE = new Color(255, 255, 255);
    public static final Color HEAT_MAX = new Color(170, 20, 40);
    public static final Color HEAT_TEXT_ON_DARK = new Color(255, 255, 255);

    // warna yang sama untuk HTML teks target
    public static final String TEXT_HTML = "#cdd6f4";
    public static final String CORRECT_HTML = "#a6e3a1";
    public static final String WRONG_HTML = "#f38ba8";
    public static final String PENDING_HTML = "#9399b2";

    // font
    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 40);
    public static final Font HEADING_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    public static final Font NORMAL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    public static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    public static final Font SMALL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font KEY_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    public static final Font TYPING_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 24);

    // jarak
    public static final int PADDING = 20;
    public static final int SMALL_PADDING = 10;

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    // gaya tombol yang sama di semua layar
    public static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(ACCENT);
        button.setForeground(BACKGROUND);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(SMALL_PADDING, PADDING, SMALL_PADDING, PADDING));
    }
}
