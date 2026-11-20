package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import mode.GameMode;
import model.Player;

/*
 * ResultPanel: layar hasil setelah satu ronde selesai. Menampilkan skor, WPM, akurasi,
 * waktu, jumlah ketukan benar/salah, dan heatmap keyboard.
 * Owner: Person B
 * Materi: GUI (layout, label, tombol), String (String.format), Collections (HashMap dari stats)
 */
public class ResultPanel extends JPanel {

    private MainFrame mainFrame;

    private JLabel scoreLabel;
    private JLabel wpmLabel;
    private JLabel accuracyLabel;
    private JLabel timeLabel;
    private JLabel keystrokesLabel;
    private KeyboardHeatmapPanel heatmapPanel;

    // Mode ronde terakhir, dipakai tombol "Main lagi".
    private GameMode lastMode;

    public ResultPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // SEMENTARA (Phase 1): hanya label besar + satu tombol untuk mencoba CardLayout.
        // Diganti dengan layar hasil sungguhan di Phase 3.
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        JLabel titleLabel = new JLabel("Hasil", SwingConstants.CENTER);
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT);
        add(titleLabel, BorderLayout.CENTER);

        JButton nextButton = new JButton("Coba CardLayout: ke layar Leaderboard");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showScreen(MainFrame.LEADERBOARD);
            }
        });
        add(nextButton, BorderLayout.SOUTH);
    }

    // Mengisi semua label dengan hasil ronde yang baru selesai.
    public void showResult(GameMode mode, Player player) {
        // TODO Phase 3
    }
}
