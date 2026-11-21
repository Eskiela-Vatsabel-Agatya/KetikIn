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

// ResultPanel: layar hasil, skor, WPM, akurasi, waktu, dan heatmap
// Owner: Satrio Ananda Widiyanto
// Materi: GUI, string, polymorphism
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
    private KeyboardHeatmapPanel heatmapPanel;

    // dipakai tombol "Main lagi" dan "Leaderboard"
    private char lastFocusLetter;
    private String lastFocusLabel;

    public ResultPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(0, Theme.PADDING));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    // judul dan tulisan rekor pribadi
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

    // kotak angka di kiri, heatmap di kanan
    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.setOpaque(false);

        // [MATERI: GUI - GridLayout]
        JPanel statsBox = new JPanel(new GridLayout(6, 2, Theme.PADDING * 2, Theme.SMALL_PADDING));
        statsBox.setBackground(Theme.PANEL);
        statsBox.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING * 2, Theme.PADDING, Theme.PADDING * 2));

        scoreLabel = addStatRow(statsBox, "Skor");
        wpmLabel = addStatRow(statsBox, "WPM");
        accuracyLabel = addStatRow(statsBox, "Akurasi");
        timeLabel = addStatRow(statsBox, "Waktu");
        correctLabel = addStatRow(statsBox, "Ketukan benar");
        wrongLabel = addStatRow(statsBox, "Ketukan salah");
        scoreLabel.setForeground(Theme.ACCENT);

        center.add(statsBox);

        // di samping, karena di bawah tidak muat di tinggi 650
        heatmapPanel = new KeyboardHeatmapPanel();
        center.add(heatmapPanel);
        return center;
    }

    // tambah baris "nama : nilai", balikin label nilainya
    private JLabel addStatRow(JPanel box, String name) {
        box.add(Theme.createLabel(name, Theme.HEADING_FONT, Theme.TEXT_MUTED));
        JLabel valueLabel = Theme.createLabel("-", Theme.HEADING_FONT, Theme.TEXT);
        box.add(valueLabel);
        return valueLabel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.PADDING, 0));
        panel.setOpaque(false);

        JButton playAgainButton = new JButton("Main lagi");
        Theme.styleButton(playAgainButton);
        playAgainButton.addActionListener(e -> {
            // ronde baru dengan fokus yang sama
            mainFrame.startGame(lastFocusLetter);
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
            mainFrame.showLeaderboard(lastFocusLabel);
        });
        panel.add(leaderboardButton);
        return panel;
    }

    // isi semua label dengan hasil ronde
    public void showResult(GameMode mode, Player player, boolean newPersonalBest) {
        lastFocusLetter = mode.getFocusLetter();
        lastFocusLabel = mode.getFocusLabel();
        TypingStats stats = mode.getStats();

        titleLabel.setText("Hasil " + player.getName() + " - " + mode.getName());
        if (newPersonalBest) {
            personalBestLabel.setText("Rekor pribadi baru!");
        } else {
            // spasi biar tinggi baris tetap
            personalBestLabel.setText(" ");
        }
        // [MATERI: Polymorphism - runtime]
        scoreLabel.setText(String.valueOf(mode.calculateScore()));
        wpmLabel.setText(String.format("%.1f", stats.getWpm()));
        accuracyLabel.setText(String.format("%.1f%%", stats.getAccuracy()));
        timeLabel.setText(String.format("%.1f detik", stats.getElapsedMillis() / MILLIS_PER_SECOND));
        correctLabel.setText(String.valueOf(stats.getCorrectKeystrokes()));
        wrongLabel.setText(String.valueOf(stats.getWrongKeystrokes()));
        heatmapPanel.showMistakes(stats.getMistakes());
    }
}
