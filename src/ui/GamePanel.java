package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import engine.GameTimer;
import engine.TimerListener;
import mode.GameMode;

/*
 * GamePanel: layar permainan. Menampilkan teks target dengan warna hijau/merah,
 * kotak ketik, timer, WPM, akurasi, dan nyawa (khusus Survival).
 * Menerima detak dari GameTimer lewat interface TimerListener.
 * Owner: Person A
 * Materi: GUI, Polymorphism runtime (variabel bertipe GameMode), Multithreading (EDT,
 *         invokeLater), String (membangun HTML), Abstraction (implements TimerListener)
 */
public class GamePanel extends JPanel implements TimerListener {

    private MainFrame mainFrame;

    // [MATERI: Polymorphism - runtime] isinya bisa ClassicMode, TimeAttackMode, dst.
    private GameMode mode;
    private GameTimer timer;

    private JLabel modeLabel;
    private JLabel timerLabel;
    private JLabel wpmLabel;
    private JLabel accuracyLabel;
    private JLabel livesLabel;
    private JLabel targetLabel;
    private JTextField inputField;

    public GamePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // SEMENTARA (Phase 1): hanya label besar + satu tombol untuk mencoba CardLayout.
        // Diganti dengan layar game sungguhan di Phase 3.
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        JLabel titleLabel = new JLabel("Game", SwingConstants.CENTER);
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT);
        add(titleLabel, BorderLayout.CENTER);

        JButton nextButton = new JButton("Coba CardLayout: ke layar Hasil");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showScreen(MainFrame.RESULT);
            }
        });
        add(nextButton, BorderLayout.SOUTH);
    }

    // Memulai satu ronde dengan mode apa pun.
    public void startGame(GameMode mode) {
        // TODO Phase 3
    }

    // Dari interface TimerListener. Dipanggil oleh thread timer, BUKAN oleh EDT.
    @Override
    public void onTick(long elapsedMillis) {
        // TODO Phase 4: semua perubahan UI di dalam SwingUtilities.invokeLater(...).
    }

    // Membangun teks HTML berwarna: hijau = benar, merah = salah, garis bawah = posisi sekarang.
    private String buildColoredHtml(String target, String typed) {
        // TODO Phase 3
        return "";
    }

    // Dipanggil setiap kali isi kotak ketik berubah.
    private void handleInput() {
        // TODO Phase 3
    }

    // Menghentikan timer (dipanggil saat game selesai, Quit, atau sebelum ronde baru).
    private void stopTimer() {
        // TODO Phase 4
    }

    // Tombol Keluar: tanya konfirmasi, lalu kembali ke menu tanpa menyimpan skor.
    private void quitGame() {
        // TODO Phase 3
    }
}
