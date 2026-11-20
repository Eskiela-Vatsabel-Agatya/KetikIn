package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

/*
 * Theme: semua warna, font, dan jarak yang dipakai tampilan KetikIn dikumpulkan di sini.
 * Kalau mau ganti warna, cukup ubah di satu tempat ini.
 * Ada juga dua method static kecil untuk membuat label dan tombol dengan gaya yang sama,
 * supaya kode panel tidak mengulang baris yang sama berkali-kali.
 * Kelas ini tidak perlu dibuat objeknya; semua isinya static.
 * Owner: Person C
 * Materi: Variables (static final / konstanta, static method), GUI (Color, Font)
 */
// [MATERI: Variables - static final]
public class Theme {

    // ---------- Warna (angka R, G, B; kode hex di komentar) ----------
    public static final Color BACKGROUND = new Color(30, 30, 46);     // #1E1E2E latar utama gelap
    public static final Color PANEL = new Color(49, 50, 68);          // #313244 latar kotak/kartu
    public static final Color TEXT = new Color(205, 214, 244);        // #CDD6F4 teks utama terang
    public static final Color TEXT_MUTED = new Color(147, 153, 178);  // #9399B2 teks abu-abu
    public static final Color ACCENT = new Color(137, 180, 250);      // #89B4FA warna aksen (biru)
    public static final Color CORRECT = new Color(166, 227, 161);     // #A6E3A1 hijau = benar
    public static final Color WRONG = new Color(243, 139, 168);       // #F38BA8 merah = salah

    // Warna heatmap: tombol tanpa kesalahan = putih, tombol paling sering salah = merah tua.
    // Warna di antaranya dihitung oleh KeyboardHeatmapPanel.calculateColor().
    public static final Color HEAT_NONE = new Color(255, 255, 255);   // #FFFFFF putih
    public static final Color HEAT_MAX = new Color(170, 20, 40);      // #AA1428 merah tua
    // Warna huruf di atas tombol heatmap yang sudah gelap.
    public static final Color HEAT_TEXT_ON_DARK = new Color(255, 255, 255);

    // Warna yang sama dalam bentuk teks, untuk dipakai di dalam teks HTML (teks target di GamePanel).
    public static final String TEXT_HTML = "#cdd6f4";
    public static final String CORRECT_HTML = "#a6e3a1";
    public static final String WRONG_HTML = "#f38ba8";
    public static final String PENDING_HTML = "#9399b2";

    // ---------- Font ----------
    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 40);
    public static final Font HEADING_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    public static final Font NORMAL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    public static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    public static final Font SMALL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    // Huruf di tombol-tombol heatmap keyboard.
    public static final Font KEY_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    // Font monospace (semua huruf sama lebar) untuk teks target dan kotak ketik.
    public static final Font TYPING_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 24);

    // ---------- Jarak ----------
    // Jarak kosong (padding) di tepi panel, dalam pixel.
    public static final int PADDING = 20;
    // Jarak kecil di dalam tombol dan kotak.
    public static final int SMALL_PADDING = 10;

    // Membuat JLabel dengan teks, font, dan warna tertentu.
    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    // Memberi gaya yang sama ke sebuah tombol: latar biru aksen, teks gelap, tanpa garis tepi abu-abu.
    public static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(ACCENT);
        button.setForeground(BACKGROUND);
        // Tidak menggambar kotak titik-titik di sekitar teks saat tombol fokus.
        button.setFocusPainted(false);
        // Kursor mouse berubah menjadi "tangan" saat berada di atas tombol, tanda bisa diklik.
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(SMALL_PADDING, PADDING, SMALL_PADDING, PADDING));
    }
}
