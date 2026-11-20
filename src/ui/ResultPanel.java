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
 * waktu, jumlah ketukan benar/salah, dan heatmap kesalahan di keyboard.
 * Owner: Person B
 * Materi: GUI (BorderLayout, GridLayout, label, tombol), String (String.format),
 *         Polymorphism runtime (mode.calculateScore())
 */
public class ResultPanel extends JPanel {

    private static final double MILLIS_PER_SECOND = 1000.0;

    private MainFrame mainFrame;

    private JLabel titleLabel;
    private JLabel personalBestLabel;
    private JLabel scoreLabel;
    private JLabel wpmLabel;
    private JLabel accuracyLabel;
    private JLabel timeLabel;
    private JLabel correctLabel;
    private JLabel wrongLabel;
    // Heatmap kesalahan (gambar keyboard berwarna).
    private KeyboardHeatmapPanel heatmapPanel;

    // Nama mode ronde terakhir, dipakai tombol "Main lagi".
    private String lastModeName;

    public ResultPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(0, Theme.PADDING));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    // Bagian atas: judul, dan di bawahnya tulisan "Rekor pribadi baru!" (kalau ada).
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setOpaque(false);

        titleLabel = Theme.createLabel("", Theme.TITLE_FONT, Theme.ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);

        personalBestLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.CORRECT);
        personalBestLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(personalBestLabel);
        return panel;
    }

    // Bagian tengah: kotak berisi angka-angka hasil, dan heatmap keyboard di sebelahnya.
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

        // Heatmap kesalahan di sebelah kanan kotak angka. (Di bawahnya tidak muat
        // di jendela setinggi 650 pixel.) FlowLayout menaruh keduanya berdampingan.
        heatmapPanel = new KeyboardHeatmapPanel();
        center.add(heatmapPanel);
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

        JButton leaderboardButton = new JButton("Leaderboard");
        Theme.styleButton(leaderboardButton);
        leaderboardButton.addActionListener(e -> {
            // Langsung buka tab mode yang baru dimainkan.
            mainFrame.showLeaderboard(lastModeName);
        });
        panel.add(leaderboardButton);
        return panel;
    }

    // Mengisi semua label dengan hasil ronde yang baru selesai.
    // newPersonalBest = true kalau skor ini lebih tinggi dari semua skor pemain ini sebelumnya di mode ini.
    public void showResult(GameMode mode, Player player, boolean newPersonalBest) {
        lastModeName = mode.getName();
        TypingStats stats = mode.getStats();

        titleLabel.setText("Hasil " + mode.getName() + " - " + player.getName());
        if (newPersonalBest) {
            personalBestLabel.setText("Rekor pribadi baru!");
        } else {
            personalBestLabel.setText(" ");   // spasi supaya tinggi baris tetap sama
        }
        // calculateScore() dipanggil lewat variabel GameMode: rumus yang dipakai
        // adalah rumus milik mode aslinya (runtime polymorphism).
        scoreLabel.setText(String.valueOf(mode.calculateScore()));
        wpmLabel.setText(String.format("%.1f", stats.getWpm()));
        accuracyLabel.setText(String.format("%.1f%%", stats.getAccuracy()));
        timeLabel.setText(String.format("%.1f detik", stats.getElapsedMillis() / MILLIS_PER_SECOND));
        correctLabel.setText(String.valueOf(stats.getCorrectKeystrokes()));
        wrongLabel.setText(String.valueOf(stats.getWrongKeystrokes()));
        heatmapPanel.showMistakes(stats.getMistakes());
    }
}
