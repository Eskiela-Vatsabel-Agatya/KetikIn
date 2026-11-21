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
import model.TypingStats;

// GamePanel: layar main, teks target berwarna, kotak ketik, timer, WPM, akurasi
// Owner: Eskiela Vatsabel Agatya
// Materi: GUI, event handling, polymorphism, string, multithreading, abstraction
public class GamePanel extends JPanel implements TimerListener {

    private static final double MILLIS_PER_SECOND = 1000.0;

    private MainFrame mainFrame;

    // [MATERI: Polymorphism - runtime]
    // isinya bisa ClassicMode atau LetterFocusMode
    private GameMode mode;
    // null kalau tidak ada ronde
    private GameTimer timer;

    // isi kotak ketik sebelumnya, buat tahu huruf mana yang baru
    private String previousText = "";

    private JLabel modeLabel;
    private JLabel descriptionLabel;
    private JLabel timerLabel;
    private JLabel wpmLabel;
    private JLabel accuracyLabel;
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

    // nama mode di kiri, waktu WPM akurasi di kanan
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
        timerLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.TEXT);
        wpmLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.TEXT);
        accuracyLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.TEXT);
        numbers.add(timerLabel);
        numbers.add(wpmLabel);
        numbers.add(accuracyLabel);
        bar.add(numbers, BorderLayout.EAST);
        return bar;
    }

    private JPanel createTargetPanel() {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Theme.PANEL);
        box.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        // pakai JEditorPane bukan JLabel biar wrap sesuai lebar asli, tanpa tebak lebar px
        targetPane = new JEditorPane("text/html", "");
        targetPane.setEditable(false);
        // tidak bisa fokus, jadi kotak ketik tetap fokus walau teks diklik
        targetPane.setFocusable(false);
        targetPane.setBackground(Theme.PANEL);
        targetPane.setBorder(BorderFactory.createEmptyBorder());
        // biar HTML ikut font dan warna komponen ini
        targetPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        targetPane.setFont(Theme.TYPING_FONT);
        targetPane.setForeground(Theme.TEXT);
        box.add(targetPane, BorderLayout.CENTER);
        return box;
    }

    // petunjuk, kotak ketik, dan tombol keluar
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(Theme.PADDING, Theme.SMALL_PADDING));
        panel.setOpaque(false);

        JLabel hintLabel = Theme.createLabel(
                "Hijau = benar, merah = salah, garis bawah = posisi sekarang, tebal = huruf fokus. Backspace boleh.",
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
        // matikan paste biar tidak bisa tempel teks target
        inputField.setTransferHandler(null);

        // [MATERI: GUI - event handling]
        inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleInput();
                // kursor balik ke akhir biar tidak bisa sisip huruf di tengah
                inputField.setCaretPosition(inputField.getText().length());
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

    public void startGame(GameMode newMode) {
        stopTimer();

        mode = newMode;
        previousText = "";
        inputField.setText("");

        modeLabel.setText(mode.getName());
        descriptionLabel.setText(mode.getDescription());

        mode.getStats().start();
        refreshDisplay("");
        updateTimeLabel(0);
        wpmLabel.setText("WPM: 0");
        inputField.requestFocusInWindow();

        // [MATERI: Multithreading]
        // timer baru tiap ronde, thread cuma bisa di-start sekali
        timer = new GameTimer(this);
        timer.start();
    }

    private void finishGame() {
        stopTimer();
        mode.getStats().stop();
        mainFrame.showResult(mode);
    }

    // balik ke menu tanpa simpan skor
    private void quitGame() {
        // timer tetap jalan selama dialog terbuka, tidak masalah karena ronde selesai lewat ketikan
        boolean yes = mainFrame.askConfirmation(
                "Keluar dari permainan?\nSkor ronde ini tidak akan disimpan.", "Keluar ke Menu");
        if (yes) {
            stopTimer();
            mode = null;
            mainFrame.showMenu();
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stopTimer();
            // detak lama yang masih tertinggal diabaikan di onTick
            timer = null;
        }
    }

    // dipanggil tiap tombol dilepas, ronde selesai kalau semua huruf sudah diketik
    private void handleInput() {
        if (mode == null || mode.isOver()) {
            return;
        }
        String target = mode.getCurrentTarget();
        String typed = inputField.getText();

        // buang huruf yang lebih panjang dari target
        if (typed.length() > target.length()) {
            typed = typed.substring(0, target.length());
            inputField.setText(typed);
        }

        recordNewKeystrokes(typed, target);
        previousText = typed;
        refreshDisplay(typed);

        if (typed.length() == target.length()) {
            mode.submit(typed);
            if (mode.isOver()) {
                finishGame();
            }
        }
    }

    // hitung cuma huruf baru, backspace tidak dihitung
    // pakai loop karena ketik cepat bisa masuk beberapa huruf sekaligus
    private void recordNewKeystrokes(String typed, String target) {
        if (typed.length() <= previousText.length()) {
            return;
        }
        TypingStats stats = mode.getStats();
        for (int i = previousText.length(); i < typed.length(); i++) {
            stats.recordKeystroke(typed.charAt(i), target.charAt(i));
        }
    }

    private void refreshDisplay(String typed) {
        // mode normal kasih NO_FOCUS, jadi tidak ada yang ditebalkan
        targetPane.setText(buildColoredHtml(mode.getCurrentTarget(), typed, mode.getFocusLetter()));

        TypingStats stats = mode.getStats();
        wpmLabel.setText(String.format("WPM: %.0f", stats.getWpm()));
        accuracyLabel.setText(String.format("Akurasi: %.1f%%", stats.getAccuracy()));
    }

    private void updateTimeLabel(long elapsedMillis) {
        timerLabel.setText(String.format("Waktu: %.1f s", elapsedMillis / MILLIS_PER_SECOND));
    }

    // [MATERI: Multithreading - EDT]
    // jalan di thread timer, update UI harus lewat EDT
    @Override
    public void onTick(final long elapsedMillis) {
        final Thread tickingTimer = Thread.currentThread();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // abaikan detak telat dari timer lama atau ronde yang sudah selesai
                if (tickingTimer != timer || mode == null || mode.isOver()) {
                    return;
                }
                handleTick(elapsedMillis);
            }
        });
    }

    private void handleTick(long elapsedMillis) {
        updateTimeLabel(elapsedMillis);
        wpmLabel.setText(String.format("WPM: %.0f", mode.getStats().getWpm(elapsedMillis)));
    }

    // [MATERI: String - StringBuilder]
    // warnai tiap huruf sesuai benar atau salah, huruf fokus ditebalkan
    // huruf berurutan dengan gaya sama digabung satu span, biar lebar baris tidak salah di Java 9+
    private String buildColoredHtml(String target, String typed, char focusLetter) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");

        // kosong = belum ada span yang terbuka
        String previousStyle = "";
        for (int i = 0; i < target.length(); i++) {
            String style = styleForChar(target, typed, i);
            if (focusLetter != GameMode.NO_FOCUS && target.charAt(i) == focusLetter) {
                style = style + "; font-weight:bold";
            }

            // buka span baru kalau gayanya beda dari huruf sebelumnya
            if (!style.equals(previousStyle)) {
                if (!previousStyle.isEmpty()) {
                    html.append("</span>");
                }
                html.append("<span style='").append(style).append("'>");
                previousStyle = style;
            }
            // spasi biasa sengaja tidak diganti, di situ JEditorPane boleh wrap
            html.append(target.charAt(i));
        }
        if (!previousStyle.isEmpty()) {
            html.append("</span>");
        }

        html.append("</body></html>");
        return html.toString();
    }

    // hijau benar, merah salah, garis bawah posisi sekarang, abu-abu belum diketik
    private String styleForChar(String target, String typed, int index) {
        if (index < typed.length()) {
            if (typed.charAt(index) == target.charAt(index)) {
                return "color:" + Theme.CORRECT_HTML;
            }
            return "color:" + Theme.WRONG_HTML + "; text-decoration:underline";
        }
        if (index == typed.length()) {
            return "color:" + Theme.TEXT_HTML + "; text-decoration:underline";
        }
        return "color:" + Theme.PENDING_HTML;
    }
}
