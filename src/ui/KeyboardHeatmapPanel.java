package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/*
 * KeyboardHeatmapPanel: gambar keyboard QWERTY dari JLabel. Semakin sering sebuah huruf
 * salah diketik, semakin merah tombolnya (putih = tidak pernah salah).
 * Di bawahnya ada tulisan 3 huruf yang paling sering salah.
 * Owner: Person B
 * Materi: GUI (GridLayout, FlowLayout, JLabel), Collections (HashMap, Map.Entry, ArrayList),
 *         Variables (static final / konstanta)
 */
public class KeyboardHeatmapPanel extends JPanel {

    // [MATERI: Variables - static final] baris-baris keyboard dari atas ke bawah.
    private static final String NUMBER_ROW = "1234567890";
    private static final String TOP_ROW = "qwertyuiop";
    private static final String MIDDLE_ROW = "asdfghjkl";
    private static final String BOTTOM_ROW = "zxcvbnm";

    // Ukuran satu tombol dan jarak antar tombol, dalam pixel.
    private static final int KEY_SIZE = 34;
    private static final int KEY_GAP = 4;
    // Lebar tombol spasi = selebar 6 tombol biasa.
    private static final int SPACE_BAR_KEYS = 6;
    // Berapa huruf yang ditampilkan di "Paling sering salah".
    private static final int TOP_KEY_COUNT = 3;
    // Kalau tombol sudah lebih merah dari setengah, hurufnya ditulis putih supaya tetap terbaca.
    private static final double DARK_KEY_RATIO = 0.5;

    // [MATERI: Collections - HashMap]
    // Kunci = huruf, nilai = JLabel tombolnya. Dengan map ini kita bisa langsung mengambil
    // tombol untuk huruf 'e' dengan keyLabels.get('e'), tanpa mencari satu per satu.
    private HashMap<Character, JLabel> keyLabels;
    private JLabel topMissedLabel;

    public KeyboardHeatmapPanel() {
        keyLabels = new HashMap<>();

        setLayout(new BorderLayout(0, Theme.SMALL_PADDING));
        setBackground(Theme.PANEL);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        add(Theme.createLabel("Peta kesalahan", Theme.HEADING_FONT, Theme.TEXT_MUTED), BorderLayout.NORTH);
        add(createKeyboard(), BorderLayout.CENTER);
        topMissedLabel = Theme.createLabel(" ", Theme.SMALL_FONT, Theme.TEXT);
        add(topMissedLabel, BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------
    // Membuat tampilan
    // ---------------------------------------------------------------

    // [MATERI: GUI - GridLayout] 5 baris yang ditumpuk dari atas ke bawah.
    private JPanel createKeyboard() {
        JPanel keyboard = new JPanel(new GridLayout(5, 1, 0, KEY_GAP));
        keyboard.setOpaque(false);

        // Angka kedua = jarak kosong di kiri baris, supaya baris bergeser seperti keyboard asli.
        keyboard.add(createRow(NUMBER_ROW, 0));
        keyboard.add(createRow(TOP_ROW, KEY_SIZE / 2));
        keyboard.add(createRow(MIDDLE_ROW, KEY_SIZE * 3 / 4));
        keyboard.add(createRow(BOTTOM_ROW, KEY_SIZE * 5 / 4));

        // Tombol spasi: satu tombol lebar di tengah bawah.
        JLabel spaceKey = createKey(' ', "spasi");
        spaceKey.setPreferredSize(new Dimension(KEY_SIZE * SPACE_BAR_KEYS, KEY_SIZE));
        keyboard.add(addIndent(spaceKey, KEY_SIZE * 5 / 2));
        return keyboard;
    }

    // Satu baris tombol: GridLayout(1, jumlah tombol), jadi semua tombol berjajar sama besar.
    private JPanel createRow(String keys, int indent) {
        JPanel row = new JPanel(new GridLayout(1, keys.length(), KEY_GAP, 0));
        row.setOpaque(false);
        for (int i = 0; i < keys.length(); i++) {
            char key = keys.charAt(i);
            // Di keyboard asli hurufnya tertulis besar, jadi tampilkan huruf besar.
            row.add(createKey(key, String.valueOf(Character.toUpperCase(key))));
        }
        return addIndent(row, indent);
    }

    // Membungkus komponen dengan jarak kosong di kiri.
    // FlowLayout dipakai supaya baris tetap seukuran tombol-tombolnya (tidak ditarik selebar panel).
    private JPanel addIndent(JComponent content, int indent) {
        JPanel holder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        holder.setOpaque(false);
        holder.setBorder(BorderFactory.createEmptyBorder(0, indent, 0, 0));
        holder.add(content);
        return holder;
    }

    // Membuat satu tombol (JLabel berwarna) dan menyimpannya di keyLabels.
    private JLabel createKey(char key, String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(Theme.KEY_FONT);
        label.setPreferredSize(new Dimension(KEY_SIZE, KEY_SIZE));
        // setOpaque(true): JLabel biasanya transparan; tanpa ini warna latar tidak terlihat.
        label.setOpaque(true);
        label.setBackground(Theme.HEAT_NONE);
        label.setForeground(Theme.BACKGROUND);
        keyLabels.put(key, label);
        return label;
    }

    // ---------------------------------------------------------------
    // Mewarnai
    // ---------------------------------------------------------------

    // Mewarnai setiap tombol berdasarkan jumlah kesalahan di map, lalu mengisi tulisan di bawahnya.
    // mistakes: kunci = huruf yang seharusnya diketik, nilai = berapa kali salah (dari TypingStats).
    public void showMistakes(HashMap<Character, Integer> mistakes) {
        int maxCount = findMaxCount(mistakes);

        // [MATERI: Collections - Map.Entry] loop semua pasangan (huruf, tombol) di keyLabels.
        for (Map.Entry<Character, JLabel> entry : keyLabels.entrySet()) {
            Integer count = mistakes.get(entry.getKey());
            // null = huruf ini tidak pernah salah.
            if (count == null) {
                count = 0;
            }
            colorKey(entry.getKey(), entry.getValue(), count, maxCount);
        }

        if (mistakes.isEmpty()) {
            topMissedLabel.setText("Tidak ada kesalahan. Mantap!");
        } else {
            topMissedLabel.setText("Paling sering salah: " + buildTopText(mistakes, TOP_KEY_COUNT));
        }
    }

    // Langkah 1: cari jumlah kesalahan terbanyak (dipakai sebagai "100% merah").
    private int findMaxCount(HashMap<Character, Integer> mistakes) {
        int maxCount = 0;
        for (Map.Entry<Character, Integer> entry : mistakes.entrySet()) {
            // entry.getValue() adalah Integer; Java otomatis mengubahnya ke int (unboxing).
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
            }
        }
        return maxCount;
    }

    // Langkah 2: warna satu tombol dan warna hurufnya.
    private void colorKey(char key, JLabel label, int count, int maxCount) {
        // ratio = seberapa merah: 0.0 = tidak pernah salah, 1.0 = paling sering salah.
        double ratio = 0;
        if (maxCount > 0) {
            // (double) supaya pembagian tidak dibulatkan ke 0 seperti pembagian int.
            ratio = (double) count / maxCount;
        }
        label.setBackground(calculateColor(ratio));

        // Huruf gelap di tombol terang, huruf putih di tombol gelap.
        if (ratio > DARK_KEY_RATIO) {
            label.setForeground(Theme.HEAT_TEXT_ON_DARK);
        } else {
            label.setForeground(Theme.BACKGROUND);
        }
        // Tulisan kecil yang muncul kalau mouse diam di atas tombol.
        label.setToolTipText(displayKey(key) + " salah " + count + " kali");
    }

    // Langkah 3: menghitung warna di antara putih (ratio 0) dan merah tua (ratio 1).
    // Setiap komponen warna (merah, hijau, biru) dihitung dengan rumus yang sama:
    //   nilai = awal + (akhir - awal) x ratio
    // Contoh dengan angka, ratio 0.5 (setengah dari kesalahan terbanyak):
    //   red   = 255 + (170 - 255) x 0.5 = 212.5 -> 213
    //   green = 255 + ( 20 - 255) x 0.5 = 137.5 -> 138
    //   blue  = 255 + ( 40 - 255) x 0.5 = 147.5 -> 148
    //   hasilnya Color(213, 138, 148) = merah muda.
    private Color calculateColor(double ratio) {
        Color start = Theme.HEAT_NONE;
        Color end = Theme.HEAT_MAX;

        int red = (int) Math.round(start.getRed() + (end.getRed() - start.getRed()) * ratio);
        int green = (int) Math.round(start.getGreen() + (end.getGreen() - start.getGreen()) * ratio);
        int blue = (int) Math.round(start.getBlue() + (end.getBlue() - start.getBlue()) * ratio);

        return new Color(red, green, blue);
    }

    // ---------------------------------------------------------------
    // Tulisan di bawah keyboard
    // ---------------------------------------------------------------

    // Membuat tulisan seperti "'e' (4), 'a' (3), 'r' (2)" dari huruf-huruf yang paling sering salah.
    // Caranya sederhana: ulangi 'limit' kali, dan setiap kali cari huruf dengan jumlah
    // terbanyak yang BELUM dipilih.
    private String buildTopText(HashMap<Character, Integer> counts, int limit) {
        StringBuilder text = new StringBuilder();
        ArrayList<Character> chosen = new ArrayList<>();

        for (int place = 0; place < limit; place++) {
            Character bestKey = null;
            int bestCount = 0;
            for (Map.Entry<Character, Integer> entry : counts.entrySet()) {
                boolean alreadyChosen = chosen.contains(entry.getKey());
                if (!alreadyChosen && entry.getValue() > bestCount) {
                    bestKey = entry.getKey();
                    bestCount = entry.getValue();
                }
            }
            // Tidak ada huruf lagi (misalnya hanya 2 huruf yang salah): berhenti.
            if (bestKey == null) {
                break;
            }
            chosen.add(bestKey);

            if (place > 0) {
                text.append(", ");
            }
            text.append(displayKey(bestKey)).append(" (").append(bestCount).append(")");
        }
        return text.toString();
    }

    // Huruf ditulis di antara tanda kutip, contoh 'e'. Spasi tidak terlihat, jadi ditulis "spasi".
    private String displayKey(char key) {
        if (key == ' ') {
            return "spasi";
        }
        return "'" + key + "'";
    }
}
