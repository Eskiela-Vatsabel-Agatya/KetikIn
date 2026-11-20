package ui;

import java.awt.Color;
import java.awt.Font;

/*
 * Theme: semua warna, font, dan jarak yang dipakai tampilan KetikIn dikumpulkan di sini.
 * Kalau mau ganti warna, cukup ubah di satu tempat ini.
 * Kelas ini hanya berisi konstanta, jadi tidak perlu dibuat objeknya.
 * Owner: Person C
 * Materi: Variables (static final / konstanta), GUI (Color, Font)
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

    // Warna yang sama dalam bentuk teks, untuk dipakai di dalam HTML JLabel.
    public static final String CORRECT_HTML = "#a6e3a1";
    public static final String WRONG_HTML = "#f38ba8";
    public static final String PENDING_HTML = "#9399b2";

    // ---------- Font ----------
    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 40);
    public static final Font HEADING_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    public static final Font NORMAL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    public static final Font SMALL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    // Font monospace (semua huruf sama lebar) untuk teks target dan kotak ketik.
    public static final Font TYPING_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 24);

    // ---------- Jarak ----------
    // Jarak kosong (padding) di tepi panel, dalam pixel.
    public static final int PADDING = 20;
}
