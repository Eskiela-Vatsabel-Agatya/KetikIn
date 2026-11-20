package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import engine.GameTimer;
import engine.TimerListener;
import mode.GameMode;
import model.TypingStats;

/*
 * GamePanel: layar permainan. Menampilkan teks target dengan warna hijau/merah,
 * kotak ketik, timer, WPM, akurasi, dan nyawa (khusus Survival).
 * Menerima detak dari GameTimer lewat interface TimerListener (Phase 4).
 * Owner: Person A
 * Materi: GUI (layout, JTextField, KeyListener), Polymorphism runtime (variabel bertipe GameMode),
 *         String (StringBuilder, charAt, membangun HTML), Multithreading (EDT, Phase 4),
 *         Abstraction (implements TimerListener)
 */
public class GamePanel extends JPanel implements TimerListener {

    // Lebar teks target di dalam HTML. Swing menghitung "px" HTML sekitar 1.3 kali lebih besar,
    // jadi 660 di sini kira-kira 860 pixel di layar (pas dengan lebar kotak).
    private static final int TARGET_WIDTH = 660;
    private static final double MILLIS_PER_SECOND = 1000.0;

    private MainFrame mainFrame;

    // [MATERI: Polymorphism - runtime]
    // Variabel ini bertipe GameMode. Isinya bisa ClassicMode, TimeAttackMode, dan seterusnya.
    // GamePanel tidak tahu (dan tidak peduli) mode apa ini; ia hanya memanggil method GameMode,
    // dan Java menjalankan versi method milik objek aslinya.
    private GameMode mode;
    private GameTimer timer;

    // Isi kotak ketik pada pengecekan sebelumnya. Dipakai untuk tahu berapa huruf BARU yang diketik.
    private String previousText = "";

    private JLabel modeLabel;
    private JLabel descriptionLabel;
    private JLabel timerLabel;
    private JLabel wpmLabel;
    private JLabel accuracyLabel;
    private JLabel livesLabel;
    private JLabel targetLabel;
    private JTextField inputField;

    public GamePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(0, Theme.PADDING));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        add(createTopBar(), BorderLayout.NORTH);
        add(createTargetPanel(), BorderLayout.CENTER);
        add(createInputPanel(), BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------
    // Membuat tampilan
    // ---------------------------------------------------------------

    // Bagian atas: nama mode di kiri, angka-angka (waktu, WPM, akurasi) di kanan.
    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        JPanel modeInfo = new JPanel(new GridLayout(2, 1));
        modeInfo.setOpaque(false);
        modeLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.ACCENT);
        descriptionLabel = Theme.createLabel("", Theme.SMALL_FONT, Theme.TEXT_MUTED);
        modeInfo.add(modeLabel);
        modeInfo.add(descriptionLabel);
        bar.add(modeInfo, BorderLayout.WEST);

        JPanel numbers = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.PADDING, 0));
        numbers.setOpaque(false);
        livesLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.WRONG);
        livesLabel.setVisible(false);   // hanya dipakai Survival (Phase 4)
        timerLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.TEXT);
        wpmLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.TEXT);
        accuracyLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.TEXT);
        numbers.add(livesLabel);
        numbers.add(timerLabel);
        numbers.add(wpmLabel);
        numbers.add(accuracyLabel);
        bar.add(numbers, BorderLayout.EAST);
        return bar;
    }

    // Bagian tengah: kotak berisi teks target yang berwarna.
    private JPanel createTargetPanel() {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Theme.PANEL);
        box.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        targetLabel = Theme.createLabel("", Theme.TYPING_FONT, Theme.TEXT);
        // Teks mulai dari atas kotak, bukan di tengah.
        targetLabel.setVerticalAlignment(SwingConstants.TOP);
        box.add(targetLabel, BorderLayout.CENTER);
        return box;
    }

    // Bagian bawah: petunjuk kecil, kotak ketik, dan tombol keluar.
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(Theme.PADDING, Theme.SMALL_PADDING));
        panel.setOpaque(false);

        JLabel hintLabel = Theme.createLabel(
                "Hijau = benar, merah = salah, garis bawah = posisi sekarang. Backspace boleh dipakai.",
                Theme.SMALL_FONT, Theme.TEXT_MUTED);
        panel.add(hintLabel, BorderLayout.NORTH);

        inputField = new JTextField();
        inputField.setFont(Theme.TYPING_FONT);
        inputField.setBackground(Theme.PANEL);
        inputField.setForeground(Theme.TEXT);
        inputField.setCaretColor(Theme.TEXT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT, 2),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        // Mematikan copy/paste/drag ke kotak ketik, supaya pemain tidak bisa menempel teks target.
        inputField.setTransferHandler(null);

        // [MATERI: GUI - event handling] KeyListener sebagai anonymous inner class.
        // Kita hanya butuh keyReleased (tombol dilepas = huruf sudah masuk ke kotak ketik).
        inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Tidak dipakai.
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Tidak dipakai.
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleInput();
            }
        });
        panel.add(inputField, BorderLayout.CENTER);

        JButton quitButton = new JButton("Keluar ke Menu");
        Theme.styleButton(quitButton);
        quitButton.addActionListener(e -> {
            quitGame();
        });
        panel.add(quitButton, BorderLayout.EAST);
        return panel;
    }

    // ---------------------------------------------------------------
    // Jalannya permainan
    // ---------------------------------------------------------------

    // Memulai satu ronde dengan mode apa pun.
    public void startGame(GameMode newMode) {
        mode = newMode;
        previousText = "";
        inputField.setText("");

        // getName() dan getDescription() dipanggil lewat variabel GameMode:
        // hasilnya tergantung objek mode yang asli (runtime polymorphism).
        modeLabel.setText(mode.getName());
        descriptionLabel.setText(mode.getDescription());

        mode.getStats().start();
        refreshDisplay("");
        inputField.requestFocusInWindow();
    }

    // Dipanggil setiap kali tombol dilepas di kotak ketik.
    private void handleInput() {
        // Tidak ada ronde yang berjalan (misalnya sudah keluar atau sudah selesai).
        if (mode == null || mode.isOver()) {
            return;
        }

        String target = mode.getCurrentTarget();
        String typed = inputField.getText();

        // Ketikan tidak boleh lebih panjang dari target: buang huruf yang kelebihan.
        if (typed.length() > target.length()) {
            typed = typed.substring(0, target.length());
            inputField.setText(typed);
        }

        recordNewKeystrokes(typed, target);
        previousText = typed;
        refreshDisplay(typed);

        // Semua huruf target sudah diketik: kirim ke mode.
        if (typed.length() == target.length()) {
            mode.submit(typed);
            if (mode.isOver()) {
                finishGame();
            }
        }
    }

    // Mencatat huruf-huruf BARU ke TypingStats.
    // Hanya dihitung kalau teks bertambah panjang. Backspace membuat teks lebih pendek,
    // jadi Backspace tidak pernah dihitung sebagai ketukan.
    // Kalau pemain mengetik sangat cepat, bisa ada lebih dari satu huruf baru di antara
    // dua event, jadi kita pakai loop untuk semua huruf baru.
    private void recordNewKeystrokes(String typed, String target) {
        if (typed.length() <= previousText.length()) {
            return;
        }
        TypingStats stats = mode.getStats();
        for (int i = previousText.length(); i < typed.length(); i++) {
            stats.recordKeystroke(typed.charAt(i), target.charAt(i));
        }
    }

    // Menggambar ulang teks berwarna dan angka-angka statistik.
    private void refreshDisplay(String typed) {
        targetLabel.setText(buildColoredHtml(mode.getCurrentTarget(), typed));
        updateStatsLabels();
    }

    // Memperbarui label waktu, WPM, dan akurasi.
    // (Di Phase 4, waktu dan WPM juga diperbarui oleh timer setiap 100 ms.)
    private void updateStatsLabels() {
        TypingStats stats = mode.getStats();
        double seconds = stats.getElapsedMillis() / MILLIS_PER_SECOND;
        // [MATERI: String - format] %.1f = 1 angka di belakang koma, %% = tanda persen.
        timerLabel.setText(String.format("Waktu: %.1f s", seconds));
        wpmLabel.setText(String.format("WPM: %.0f", stats.getWpm()));
        accuracyLabel.setText(String.format("Akurasi: %.1f%%", stats.getAccuracy()));
    }

    // Ronde selesai: hentikan hitungan waktu, lalu pindah ke layar hasil.
    private void finishGame() {
        stopTimer();
        mode.getStats().stop();
        mainFrame.showResult(mode);
    }

    // Tombol keluar: tanya konfirmasi, lalu kembali ke menu tanpa menyimpan skor.
    private void quitGame() {
        boolean yes = mainFrame.askConfirmation(
                "Keluar dari permainan?\nSkor ronde ini tidak akan disimpan.", "Keluar ke Menu");
        if (yes) {
            stopTimer();
            mode = null;   // ronde dibuang
            mainFrame.showMenu();
        }
    }

    // ---------------------------------------------------------------
    // Teks berwarna (HTML)
    // ---------------------------------------------------------------

    // [MATERI: String - StringBuilder, charAt]
    // Membangun teks HTML berwarna untuk JLabel. Dibangun ulang dari nol setiap ada ketukan.
    // Setiap huruf target dicek satu per satu dan dibungkus tag sesuai keadaannya:
    //   1. sudah diketik dan benar  -> hijau
    //   2. sudah diketik tapi salah -> merah + garis bawah (yang ditampilkan huruf target)
    //   3. posisi sekarang          -> terang + garis bawah (seperti kursor)
    //   4. belum diketik            -> abu-abu
    // Contoh hasil untuk target "abc", ketikan "ax":
    //   <html><body style='width:660px'><font color='#a6e3a1'>a</font>
    //   <font color='#f38ba8'><u>b</u></font><font color='#cdd6f4'><u>c</u></font></body></html>
    private String buildColoredHtml(String target, String typed) {
        StringBuilder html = new StringBuilder();
        // width membuat teks yang panjang turun ke baris berikutnya.
        html.append("<html><body style='width:").append(TARGET_WIDTH).append("px'>");

        for (int i = 0; i < target.length(); i++) {
            String shownChar = escapeHtmlChar(target, i);

            if (i < typed.length()) {
                // Huruf ini sudah diketik: bandingkan dengan huruf target di posisi yang sama.
                if (typed.charAt(i) == target.charAt(i)) {
                    html.append("<font color='").append(Theme.CORRECT_HTML).append("'>");
                    html.append(shownChar);
                    html.append("</font>");
                } else {
                    html.append("<font color='").append(Theme.WRONG_HTML).append("'><u>");
                    html.append(shownChar);
                    html.append("</u></font>");
                }
            } else if (i == typed.length()) {
                // Posisi huruf yang akan diketik berikutnya.
                html.append("<font color='").append(Theme.TEXT_HTML).append("'><u>");
                html.append(shownChar);
                html.append("</u></font>");
            } else {
                // Belum diketik.
                html.append("<font color='").append(Theme.PENDING_HTML).append("'>");
                html.append(shownChar);
                html.append("</font>");
            }
        }

        html.append("</body></html>");
        return html.toString();
    }

    // Mengubah satu huruf target menjadi teks yang aman untuk HTML.
    // <, > dan & punya arti khusus di HTML, jadi harus ditulis sebagai kode (&lt; &gt; &amp;).
    // Ini penting untuk Code Mode, misalnya "List<String>" atau "a && b".
    private String escapeHtmlChar(String target, int index) {
        char c = target.charAt(index);
        if (c == '<') {
            return "&lt;";
        }
        if (c == '>') {
            return "&gt;";
        }
        if (c == '&') {
            return "&amp;";
        }
        if (c == ' ') {
            // HTML menggabungkan beberapa spasi biasa yang berurutan menjadi satu spasi.
            // &nbsp; tidak digabung, TAPI baris tidak bisa dipotong di &nbsp;, sehingga
            // paragraf panjang akan terpotong di tengah kata.
            // Jalan tengahnya: spasi pertama tetap spasi biasa (baris bisa dipotong di sini),
            // spasi berikutnya yang berurutan (dan spasi di awal teks) memakai &nbsp;.
            if (index == 0 || target.charAt(index - 1) == ' ') {
                return "&nbsp;";
            }
            return " ";
        }
        // Huruf biasa: tampilkan apa adanya.
        return String.valueOf(c);
    }

    // ---------------------------------------------------------------
    // Timer (Phase 4)
    // ---------------------------------------------------------------

    // Dari interface TimerListener. Dipanggil oleh thread timer, BUKAN oleh EDT.
    @Override
    public void onTick(long elapsedMillis) {
        // TODO Phase 4: semua perubahan UI di dalam SwingUtilities.invokeLater(...).
    }

    // Menghentikan timer (dipanggil saat game selesai, keluar, atau sebelum ronde baru).
    private void stopTimer() {
        // TODO Phase 4: timer.stopTimer();
    }
}
