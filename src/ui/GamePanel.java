package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import engine.GameTimer;
import engine.TimerListener;
import mode.GameMode;
import mode.Timed;
import model.TypingStats;

/*
 * GamePanel: layar permainan. Menampilkan teks target dengan warna hijau/merah,
 * kotak ketik, timer, WPM, akurasi, dan nyawa (khusus Survival).
 * Setiap ronde punya satu GameTimer (thread) yang memanggil onTick() setiap 100 ms.
 * Owner: Person A
 * Materi: GUI (layout, JTextField, KeyListener), Polymorphism runtime (variabel bertipe GameMode),
 *         String (StringBuilder, charAt, indexOf, substring, membangun HTML),
 *         Multithreading (GameTimer, EDT, invokeLater), Abstraction (implements TimerListener,
 *         instanceof Timed)
 */
public class GamePanel extends JPanel implements TimerListener {

    private static final double MILLIS_PER_SECOND = 1000.0;

    private MainFrame mainFrame;

    // [MATERI: Polymorphism - runtime]
    // Variabel ini bertipe GameMode. Isinya bisa ClassicMode, TimeAttackMode, dan seterusnya.
    // GamePanel tidak tahu (dan tidak peduli) mode apa ini; ia hanya memanggil method GameMode,
    // dan Java menjalankan versi method milik objek aslinya.
    private GameMode mode;
    // Timer ronde yang sedang berjalan (null kalau tidak ada ronde).
    private GameTimer timer;

    // Isi kotak ketik pada pengecekan sebelumnya. Dipakai untuk tahu berapa huruf BARU yang diketik.
    private String previousText = "";

    private JLabel modeLabel;
    private JLabel descriptionLabel;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private JLabel wpmLabel;
    private JLabel accuracyLabel;
    // Tempat menampilkan teks target berwarna (HTML). Lihat createTargetPanel().
    private JEditorPane targetPane;
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

    // Bagian atas: nama mode di kiri, angka-angka (nyawa, waktu, WPM, akurasi) di kanan.
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
        // Info tambahan dari mode (misalnya nyawa di Survival). Disembunyikan kalau kosong.
        statusLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.WRONG);
        timerLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.TEXT);
        wpmLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.TEXT);
        accuracyLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.TEXT);
        numbers.add(statusLabel);
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

        // Kenapa JEditorPane, bukan JLabel? JLabel dengan HTML butuh lebar yang kita tebak sendiri
        // (style='width:...px'), dan ukuran "px" itu berbeda di setiap versi Java dan setelan
        // skala layar Windows, sehingga teks bisa terpotong. JEditorPane otomatis memotong baris
        // (wrap) sesuai lebarnya yang sebenarnya, jadi tidak perlu angka tebakan.
        // "text/html" = isi yang kita berikan dibaca sebagai HTML.
        targetPane = new JEditorPane("text/html", "");
        // Hanya untuk dibaca, pemain tidak bisa mengubah isinya.
        targetPane.setEditable(false);
        // Tidak bisa menerima fokus, jadi kotak ketik tetap fokus walaupun teks target diklik.
        targetPane.setFocusable(false);
        // Latar sama dengan kotak di sekelilingnya.
        targetPane.setBackground(Theme.PANEL);
        // Tanpa garis tepi dan tanpa jarak tambahan bawaan JEditorPane.
        targetPane.setBorder(BorderFactory.createEmptyBorder());
        // Supaya HTML memakai font dan warna teks milik komponen ini (bukan font bawaan HTML).
        targetPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        // Font monospace dari Theme, sama seperti kotak ketik.
        targetPane.setFont(Theme.TYPING_FONT);
        targetPane.setForeground(Theme.TEXT);
        box.add(targetPane, BorderLayout.CENTER);
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
                // Kembalikan kursor ke akhir teks. Jadi tombol panah kiri/kanan dan Home
                // tidak bisa dipakai untuk menyisipkan huruf di tengah ketikan.
                inputField.setCaretPosition(inputField.getText().length());
            }
        });

        // JTextField memanggil ActionListener ketika Enter ditekan.
        inputField.addActionListener(e -> {
            handleEnter();
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
    // Mulai, selesai, keluar
    // ---------------------------------------------------------------

    // Memulai satu ronde dengan mode apa pun.
    public void startGame(GameMode newMode) {
        // Timer ronde sebelumnya (kalau masih ada) dihentikan dulu.
        stopTimer();

        mode = newMode;
        previousText = "";
        inputField.setText("");

        // getName() dan getDescription() dipanggil lewat variabel GameMode:
        // hasilnya tergantung objek mode yang asli (runtime polymorphism).
        modeLabel.setText(mode.getName());
        descriptionLabel.setText(mode.getDescription());

        mode.getStats().start();
        refreshDisplay("");
        updateTimeLabel(0);
        wpmLabel.setText("WPM: 0");
        inputField.requestFocusInWindow();

        // [MATERI: Multithreading] Setiap ronde membuat objek GameTimer BARU,
        // karena sebuah Thread hanya bisa di-start() satu kali.
        timer = new GameTimer(this);
        timer.start();
    }

    // Ronde selesai: hentikan timer dan hitungan waktu, lalu pindah ke layar hasil.
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

    // Menghentikan timer ronde ini (kalau ada).
    private void stopTimer() {
        if (timer != null) {
            timer.stopTimer();
            // null = tidak ada timer aktif. Detak yang masih "tertinggal" dari timer lama
            // akan diabaikan di onTick (lihat penjelasan di sana).
            timer = null;
        }
    }

    // ---------------------------------------------------------------
    // Input dari keyboard
    // ---------------------------------------------------------------

    // Dipanggil setiap kali tombol dilepas di kotak ketik.
    private void handleInput() {
        // Tidak ada ronde yang berjalan (misalnya sudah keluar atau sudah selesai).
        if (mode == null || mode.isOver()) {
            return;
        }
        // isWordMode() di-override oleh Time Attack dan Survival (runtime polymorphism).
        if (mode.isWordMode()) {
            handleWordInput();
        } else {
            handleTextInput();
        }
    }

    // Classic: satu teks panjang. Selesai ketika semua huruf sudah diketik.
    // Code Mode juga memakai method ini untuk satu baris, tapi barisnya baru dikirim dengan Enter.
    private void handleTextInput() {
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
        // Kecuali Code Mode (isLineMode): di sana pemain harus menekan Enter (lihat handleEnter).
        if (typed.length() == target.length() && !mode.isLineMode()) {
            mode.submit(typed);
            if (mode.isOver()) {
                finishGame();
            }
        }
    }

    // Time Attack dan Survival: satu kata setiap kali, dikirim dengan Spasi.
    // Spasi dicek PALING DULU, sebelum teks dipotong atau dihitung. Spasi sendiri tidak
    // dihitung sebagai ketukan; ia hanya tanda "kata ini selesai".
    private void handleWordInput() {
        String typed = inputField.getText();

        // Selama masih ada spasi, kata sebelum spasi dikirim.
        // Biasanya hanya satu kali. Bisa lebih kalau pemain mengetik sangat cepat, sehingga
        // huruf kata berikutnya sudah masuk sebelum event untuk Spasi sempat diproses.
        int spaceIndex = typed.indexOf(' ');
        while (spaceIndex >= 0) {
            String word = typed.substring(0, spaceIndex);
            // Catat dulu huruf kata ini yang belum sempat dicatat.
            recordNewKeystrokes(word, mode.getCurrentTarget());
            submitWord(word);
            if (mode.isOver()) {
                finishGame();
                return;
            }
            // Sisa setelah spasi = awal kata berikutnya.
            typed = typed.substring(spaceIndex + 1);
            previousText = "";
            spaceIndex = typed.indexOf(' ');
        }

        // Kalau ada kata yang dikirim, kotak ketik hanya berisi sisa ketikannya.
        if (!typed.equals(inputField.getText())) {
            inputField.setText(typed);
        }
        recordNewKeystrokes(typed, mode.getCurrentTarget());
        previousText = typed;
        refreshDisplay(typed);
    }

    // Enter ditekan.
    //   Time Attack dan Survival: Enter sama dengan Spasi, kirim kata sekarang.
    //   Code Mode: kirim baris sekarang.
    //   Classic: Enter tidak melakukan apa-apa.
    private void handleEnter() {
        if (mode == null || mode.isOver()) {
            return;
        }
        if (mode.isWordMode()) {
            submitWordWithEnter();
        } else if (mode.isLineMode()) {
            submitLine();
        }
    }

    // Time Attack dan Survival: kirim isi kotak ketik sebagai satu kata.
    private void submitWordWithEnter() {
        String typed = inputField.getText();
        recordNewKeystrokes(typed, mode.getCurrentTarget());
        submitWord(typed);
        if (mode.isOver()) {
            finishGame();
            return;
        }
        clearInput();
    }

    // Code Mode: kirim baris yang sedang diketik.
    private void submitLine() {
        // Proses dulu huruf terakhir yang mungkin belum diproses (kalau pemain menekan Enter
        // sebelum tombol huruf terakhir dilepas). Ini juga membuang huruf yang kelebihan.
        handleTextInput();

        String typed = inputField.getText();
        // Baris belum lengkap: Enter diabaikan, supaya Enter yang tidak sengaja
        // tidak membuat pemain melewati satu baris.
        if (typed.length() < mode.getCurrentTarget().length()) {
            return;
        }
        // Jangan trim() di sini: di Code Mode spasi ikut dinilai, jadi harus sama persis.
        mode.submit(typed);
        if (mode.isOver()) {
            finishGame();
            return;
        }
        // Baris berikutnya: kotak ketik dikosongkan.
        clearInput();
    }

    // Mengirim satu kata ke mode. trim() membuang spasi yang mungkin tersisa.
    // Kata kosong (misalnya Spasi ditekan di kotak yang masih kosong) tidak dikirim.
    private void submitWord(String word) {
        String cleanWord = word.trim();
        if (cleanWord.isEmpty()) {
            return;
        }
        mode.submit(cleanWord);
    }

    // Mengosongkan kotak ketik dan menampilkan target yang baru.
    private void clearInput() {
        inputField.setText("");
        previousText = "";
        refreshDisplay("");
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
            stats.recordKeystroke(typed.charAt(i), expectedCharAt(target, i));
        }
    }

    // Huruf yang seharusnya diketik di posisi index.
    // Di mode kata, pemain bisa mengetik lebih panjang dari kata targetnya. Di posisi itu
    // seharusnya ia menekan Spasi, jadi huruf yang diharapkan adalah spasi.
    private char expectedCharAt(String target, int index) {
        if (index < target.length()) {
            return target.charAt(index);
        }
        return ' ';
    }

    // ---------------------------------------------------------------
    // Tampilan
    // ---------------------------------------------------------------

    // Menggambar ulang teks berwarna, info mode, dan akurasi/WPM.
    private void refreshDisplay(String typed) {
        targetPane.setText(buildColoredHtml(mode.getCurrentTarget(), typed, mode.getUpcomingText()));

        // getStatusText() di-override oleh Survival (nyawa). Mode lain mengembalikan "".
        String status = mode.getStatusText();
        statusLabel.setText(status);
        statusLabel.setVisible(!status.isEmpty());

        TypingStats stats = mode.getStats();
        // [MATERI: String - format] %.0f = tanpa angka di belakang koma, %% = tanda persen.
        wpmLabel.setText(String.format("WPM: %.0f", stats.getWpm()));
        accuracyLabel.setText(String.format("Akurasi: %.1f%%", stats.getAccuracy()));
    }

    // Classic menampilkan waktu yang berjalan naik. Mode dengan batas waktu menampilkan sisa waktu.
    private void updateTimeLabel(long elapsedMillis) {
        // [MATERI: Abstraction - interface] instanceof mengecek apakah objek mode ini
        // mengimplementasikan interface Timed. Kalau ya, kita "cast" (ubah tipe variabelnya)
        // ke Timed supaya method milik Timed bisa dipanggil. Objeknya sendiri tetap sama.
        if (mode instanceof Timed) {
            Timed timedMode = (Timed) mode;
            long timeLeftMillis = timedMode.getTimeLeftMillis(elapsedMillis);
            if (timeLeftMillis < 0) {
                timeLeftMillis = 0;
            }
            timerLabel.setText(String.format("Sisa: %.1f s", timeLeftMillis / MILLIS_PER_SECOND));
        } else {
            timerLabel.setText(String.format("Waktu: %.1f s", elapsedMillis / MILLIS_PER_SECOND));
        }
    }

    // ---------------------------------------------------------------
    // Timer
    // ---------------------------------------------------------------

    // Dari interface TimerListener. PENTING: method ini dijalankan oleh thread TIMER, bukan oleh EDT.
    // [MATERI: Multithreading - EDT]
    // Swing tidak thread-safe: hanya Event Dispatch Thread (EDT) yang boleh mengubah komponen.
    // Jadi di sini kita TIDAK menyentuh label sama sekali. Pekerjaannya dititipkan ke EDT
    // lewat SwingUtilities.invokeLater, dan EDT akan menjalankannya sesaat kemudian.
    // final: supaya variabel ini boleh dipakai di dalam anonymous class di bawah.
    @Override
    public void onTick(final long elapsedMillis) {
        // Thread yang sedang menjalankan method ini = thread timer yang mengirim detak ini.
        final Thread tickingTimer = Thread.currentThread();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Detak "terlambat": timer bisa saja sudah menitipkan detak ini ke EDT tepat
                // sebelum ronde selesai, dihentikan, atau diganti ronde baru. Kalau detak ini
                // bukan dari timer ronde sekarang, atau ronde sudah selesai, abaikan saja.
                // Tanpa pengecekan ini, detak lama bisa mengubah layar ronde baru, atau
                // memanggil finishGame() dua kali.
                if (tickingTimer != timer || mode == null || mode.isOver()) {
                    return;
                }
                handleTick(elapsedMillis);
            }
        });
    }

    // Dijalankan di EDT setiap 100 ms selama ronde berjalan.
    private void handleTick(long elapsedMillis) {
        if (mode instanceof Timed) {
            Timed timedMode = (Timed) mode;
            if (timedMode.getTimeLeftMillis(elapsedMillis) <= 0) {
                // Time Attack: ronde selesai. Survival: nyawa -1 dan kata baru.
                timedMode.onTimeUp();
                if (mode.isOver()) {
                    finishGame();
                    return;
                }
                // Survival masih hidup: kata baru muncul, kotak ketik dikosongkan.
                clearInput();
            }
        }
        updateTimeLabel(elapsedMillis);
        // getWpm(long) = versi overload yang memakai waktu dari timer.
        wpmLabel.setText(String.format("WPM: %.0f", mode.getStats().getWpm(elapsedMillis)));
    }

    // ---------------------------------------------------------------
    // Teks berwarna (HTML)
    // ---------------------------------------------------------------

    // [MATERI: String - StringBuilder, charAt]
    // Membangun teks HTML berwarna untuk targetPane. Dibangun ulang dari nol setiap ada ketukan.
    // Setiap huruf target dicek satu per satu, lalu diberi gaya sesuai keadaannya (styleForChar):
    //   1. sudah diketik dan benar  -> hijau
    //   2. sudah diketik tapi salah -> merah + garis bawah (yang ditampilkan huruf target)
    //   3. posisi sekarang          -> terang + garis bawah (seperti kursor)
    //   4. belum diketik            -> abu-abu
    // Huruf-huruf berurutan yang gayanya SAMA dimasukkan ke dalam satu <span> yang sama.
    // Kenapa tidak satu <span> per huruf? Di Java 9 ke atas (dengan skala layar Windows),
    // Swing salah menghitung lebar baris kalau setiap huruf punya tag sendiri, sehingga
    // baris pertama keluar dari kotak dan terpotong. Dengan sedikit tag, lebarnya dihitung benar.
    // upcoming = kata-kata berikutnya (Time Attack), ditampilkan abu-abu setelah target.
    // Contoh hasil untuk target "abcd", ketikan "ax":
    //   <html><body><span style='color:#a6e3a1'>a</span>
    //   <span style='color:#f38ba8; text-decoration:underline'>b</span>
    //   <span style='color:#cdd6f4; text-decoration:underline'>c</span>
    //   <span style='color:#9399b2'>d</span></body></html>
    // Baris yang terlalu panjang otomatis turun ke baris berikutnya oleh JEditorPane.
    private String buildColoredHtml(String target, String typed, String upcoming) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");

        // Gaya huruf sebelumnya. Kosong = belum ada <span> yang terbuka.
        String previousStyle = "";
        for (int i = 0; i < target.length(); i++) {
            String style = styleForChar(target, typed, i);

            // Buka <span> baru hanya kalau gaya huruf ini berbeda dari huruf sebelumnya.
            if (!style.equals(previousStyle)) {
                if (!previousStyle.isEmpty()) {
                    html.append("</span>");
                }
                html.append("<span style='").append(style).append("'>");
                previousStyle = style;
            }
            html.append(escapeHtmlChar(target, i));
        }
        // Tutup <span> terakhir.
        if (!previousStyle.isEmpty()) {
            html.append("</span>");
        }

        // Kata-kata berikutnya (hanya berisi huruf, jadi tidak perlu di-escape).
        if (!upcoming.isEmpty()) {
            html.append("&nbsp;&nbsp;&nbsp;<span style='color:").append(Theme.PENDING_HTML).append("'>");
            html.append(upcoming);
            html.append("</span>");
        }

        html.append("</body></html>");
        return html.toString();
    }

    // Gaya (CSS) untuk huruf target di posisi index: warna, dan garis bawah kalau perlu.
    private String styleForChar(String target, String typed, int index) {
        if (index < typed.length()) {
            // Huruf ini sudah diketik: bandingkan dengan huruf target di posisi yang sama.
            if (typed.charAt(index) == target.charAt(index)) {
                return "color:" + Theme.CORRECT_HTML;
            }
            return "color:" + Theme.WRONG_HTML + "; text-decoration:underline";
        }
        if (index == typed.length()) {
            // Posisi huruf yang akan diketik berikutnya.
            return "color:" + Theme.TEXT_HTML + "; text-decoration:underline";
        }
        // Belum diketik.
        return "color:" + Theme.PENDING_HTML;
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
}
