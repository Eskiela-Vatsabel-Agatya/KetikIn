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

// KeyboardHeatmapPanel: keyboard QWERTY yang makin merah kalau hurufnya sering salah
// Owner: Satrio Ananda Widiyanto
// Materi: GUI, collections, variables
public class KeyboardHeatmapPanel extends JPanel {

    // [MATERI: Variables - static final]
    private static final String NUMBER_ROW = "1234567890";
    private static final String TOP_ROW = "qwertyuiop";
    private static final String MIDDLE_ROW = "asdfghjkl";
    private static final String BOTTOM_ROW = "zxcvbnm";

    private static final int KEY_SIZE = 34;
    private static final int KEY_GAP = 4;
    // spasi selebar 6 tombol
    private static final int SPACE_BAR_KEYS = 6;
    private static final int TOP_KEY_COUNT = 3;
    // di atas ini huruf ditulis putih biar terbaca
    private static final double DARK_KEY_RATIO = 0.5;

    // [MATERI: Collections - HashMap]
    // huruf -> label tombolnya
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

    // [MATERI: GUI - GridLayout]
    private JPanel createKeyboard() {
        JPanel keyboard = new JPanel(new GridLayout(5, 1, 0, KEY_GAP));
        keyboard.setOpaque(false);

        // tiap baris digeser ke kanan seperti keyboard asli
        keyboard.add(createRow(NUMBER_ROW, 0));
        keyboard.add(createRow(TOP_ROW, KEY_SIZE / 2));
        keyboard.add(createRow(MIDDLE_ROW, KEY_SIZE * 3 / 4));
        keyboard.add(createRow(BOTTOM_ROW, KEY_SIZE * 5 / 4));

        JLabel spaceKey = createKey(' ', "spasi");
        spaceKey.setPreferredSize(new Dimension(KEY_SIZE * SPACE_BAR_KEYS, KEY_SIZE));
        keyboard.add(addIndent(spaceKey, KEY_SIZE * 5 / 2));
        return keyboard;
    }

    private JPanel createRow(String keys, int indent) {
        JPanel row = new JPanel(new GridLayout(1, keys.length(), KEY_GAP, 0));
        row.setOpaque(false);
        for (int i = 0; i < keys.length(); i++) {
            char key = keys.charAt(i);
            row.add(createKey(key, String.valueOf(Character.toUpperCase(key))));
        }
        return addIndent(row, indent);
    }

    // FlowLayout biar baris tidak ditarik selebar panel
    private JPanel addIndent(JComponent content, int indent) {
        JPanel holder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        holder.setOpaque(false);
        holder.setBorder(BorderFactory.createEmptyBorder(0, indent, 0, 0));
        holder.add(content);
        return holder;
    }

    private JLabel createKey(char key, String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(Theme.KEY_FONT);
        label.setPreferredSize(new Dimension(KEY_SIZE, KEY_SIZE));
        // tanpa setOpaque warna latar JLabel tidak kelihatan
        label.setOpaque(true);
        label.setBackground(Theme.HEAT_NONE);
        label.setForeground(Theme.BACKGROUND);
        keyLabels.put(key, label);
        return label;
    }

    // warnai semua tombol dari map kesalahan TypingStats
    public void showMistakes(HashMap<Character, Integer> mistakes) {
        int maxCount = findMaxCount(mistakes);

        // [MATERI: Collections - Map.Entry]
        for (Map.Entry<Character, JLabel> entry : keyLabels.entrySet()) {
            Integer count = mistakes.get(entry.getKey());
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

    // jumlah salah terbanyak dipakai sebagai merah penuh
    private int findMaxCount(HashMap<Character, Integer> mistakes) {
        int maxCount = 0;
        for (Map.Entry<Character, Integer> entry : mistakes.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
            }
        }
        return maxCount;
    }

    private void colorKey(char key, JLabel label, int count, int maxCount) {
        // 0 = tidak pernah salah, 1 = paling sering salah
        double ratio = 0;
        if (maxCount > 0) {
            ratio = (double) count / maxCount;
        }
        label.setBackground(calculateColor(ratio));

        if (ratio > DARK_KEY_RATIO) {
            label.setForeground(Theme.HEAT_TEXT_ON_DARK);
        } else {
            label.setForeground(Theme.BACKGROUND);
        }
        label.setToolTipText(displayKey(key) + " salah " + count + " kali");
    }

    // warna antara putih dan merah tua, nilai = awal + (akhir - awal) x ratio
    private Color calculateColor(double ratio) {
        Color start = Theme.HEAT_NONE;
        Color end = Theme.HEAT_MAX;

        int red = (int) Math.round(start.getRed() + (end.getRed() - start.getRed()) * ratio);
        int green = (int) Math.round(start.getGreen() + (end.getGreen() - start.getGreen()) * ratio);
        int blue = (int) Math.round(start.getBlue() + (end.getBlue() - start.getBlue()) * ratio);

        return new Color(red, green, blue);
    }

    // contoh hasil: 'e' (4), 'a' (3), 'r' (2)
    // tiap putaran cari huruf terbanyak yang belum dipilih
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
            // huruf yang salah sudah habis
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

    private String displayKey(char key) {
        if (key == ' ') {
            return "spasi";
        }
        return "'" + key + "'";
    }
}
