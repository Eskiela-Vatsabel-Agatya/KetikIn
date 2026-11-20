package ui;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * KeyboardHeatmapPanel: gambar keyboard QWERTY dari JLabel. Semakin sering sebuah huruf
 * salah diketik, semakin merah tombolnya (putih = tidak pernah salah).
 * Di bawahnya ada tulisan 3 huruf yang paling sering salah.
 * Owner: Person B
 * Materi: GUI (GridLayout, JLabel), Collections (HashMap, Map.Entry), Variables (konstanta)
 */
public class KeyboardHeatmapPanel extends JPanel {

    // Baris-baris keyboard dari atas ke bawah.
    private static final String NUMBER_ROW = "1234567890";
    private static final String TOP_ROW = "qwertyuiop";
    private static final String MIDDLE_ROW = "asdfghjkl";
    private static final String BOTTOM_ROW = "zxcvbnm";

    // Kunci = huruf, nilai = JLabel tombolnya, supaya warnanya mudah diubah.
    private HashMap<Character, JLabel> keyLabels;
    private JLabel topMissedLabel;

    public KeyboardHeatmapPanel() {
        keyLabels = new HashMap<>();
        // TODO Phase 6: buat baris-baris tombol dengan GridLayout.
    }

    // Mewarnai setiap tombol berdasarkan jumlah kesalahan di map.
    public void showMistakes(HashMap<Character, Integer> mistakes) {
        // TODO Phase 6
    }

    // Menghitung warna antara putih (0 salah) dan merah tua (paling banyak salah).
    private Color calculateColor(int count, int maxCount) {
        // TODO Phase 6
        return Color.WHITE;
    }
}
