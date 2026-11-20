package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import mode.GameMode;
import model.Player;
import model.TypingStats;

/*
 * ResultPanel: layar hasil setelah satu ronde selesai. Menampilkan skor, WPM, akurasi,
 * waktu, dan jumlah ketukan benar/salah. (Heatmap keyboard ditambahkan di Phase 6.)
 * Owner: Person B
 * Materi: GUI (BorderLayout, GridLayout, label, tombol), String (String.format),
 *         Polymorphism runtime (mode.calculateScore())
 */
public class ResultPanel extends JPanel {

    private static final double MILLIS_PER_SECOND = 1000.0;

    private MainFrame mainFrame;

    private JLabel titleLabel;
    private JLabel scoreLabel;
    private JLabel wpmLabel;
    private JLabel accuracyLabel;
    private JLabel timeLabel;
    private JLabel correctLabel;
    private JLabel wrongLabel;
    // Heatmap kesalahan, dibuat di Phase 6.
    private KeyboardHeatmapPanel heatmapPanel;

    // Nama mode ronde terakhir, dipakai tombol "Main lagi".
    private String lastModeName;

    public ResultPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(0, Theme.PADDING));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        titleLabel = Theme.createLabel("", Theme.TITLE_FONT, Theme.ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    // Bagian tengah: kotak berisi angka-angka hasil.
    // Di Phase 6, heatmap keyboard akan ditaruh di bawah kotak ini.
    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.setOpaque(false);

        // [MATERI: GUI - GridLayout] 6 baris x 2 kolom: nama angka di kiri, nilainya di kanan.
        JPanel statsBox = new JPanel(new GridLayout(6, 2, Theme.PADDING * 2, Theme.SMALL_PADDING));
        statsBox.setBackground(Theme.PANEL);
        statsBox.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING * 2, Theme.PADDING, Theme.PADDING * 2));

        scoreLabel = addStatRow(statsBox, "Score");
        wpmLabel = addStatRow(statsBox, "WPM");
        accuracyLabel = addStatRow(statsBox, "Akurasi");
        timeLabel = addStatRow(statsBox, "Waktu");
        correctLabel = addStatRow(statsBox, "Ketukan benar");
        wrongLabel = addStatRow(statsBox, "Ketukan salah");
        // Skor dibuat lebih menonjol.
        scoreLabel.setForeground(Theme.ACCENT);

        center.add(statsBox);
        // TODO Phase 6: tambahkan heatmapPanel di sini (di bawah statsBox).
        return center;
    }

    // Menambahkan satu baris "nama : nilai" ke kotak, lalu mengembalikan label nilainya
    // supaya bisa diisi nanti di showResult().
    private JLabel addStatRow(JPanel box, String name) {
        box.add(Theme.createLabel(name, Theme.HEADING_FONT, Theme.TEXT_MUTED));
        JLabel valueLabel = Theme.createLabel("-", Theme.HEADING_FONT, Theme.TEXT);
        box.add(valueLabel);
        return valueLabel;
    }

    // Tombol "Main lagi" dan "Menu" di bagian bawah.
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.PADDING, 0));
        panel.setOpaque(false);

        JButton playAgainButton = new JButton("Main lagi");
        Theme.styleButton(playAgainButton);
        playAgainButton.addActionListener(e -> {
            // Ronde BARU dengan mode yang sama (kata-katanya diacak ulang).
            mainFrame.startGame(lastModeName);
        });
        panel.add(playAgainButton);

        JButton menuButton = new JButton("Menu");
        Theme.styleButton(menuButton);
        menuButton.addActionListener(e -> {
            mainFrame.showMenu();
        });
        panel.add(menuButton);

        // TODO Phase 5: tombol "Leaderboard".
        return panel;
    }

    // Mengisi semua label dengan hasil ronde yang baru selesai.
    public void showResult(GameMode mode, Player player) {
        lastModeName = mode.getName();
        TypingStats stats = mode.getStats();

        titleLabel.setText("Hasil " + mode.getName() + " - " + player.getName());
        // calculateScore() dipanggil lewat variabel GameMode: rumus yang dipakai
        // adalah rumus milik mode aslinya (runtime polymorphism).
        scoreLabel.setText(String.valueOf(mode.calculateScore()));
        wpmLabel.setText(String.format("%.1f", stats.getWpm()));
        accuracyLabel.setText(String.format("%.1f%%", stats.getAccuracy()));
        timeLabel.setText(String.format("%.1f detik", stats.getElapsedMillis() / MILLIS_PER_SECOND));
        correctLabel.setText(String.valueOf(stats.getCorrectKeystrokes()));
        wrongLabel.setText(String.valueOf(stats.getWrongKeystrokes()));
    }
}
