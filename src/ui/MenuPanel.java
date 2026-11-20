package ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import data.InvalidUsernameException;
import mode.GameMode;
import model.Player;

/*
 * MenuPanel: layar pertama. Pemain mengisi nama, memilih huruf yang ingin dilatih
 * (atau "Normal"), lalu menekan "Mulai". Ada juga tombol Leaderboard dan Keluar.
 * Owner: Person C
 * Materi: GUI (BorderLayout, GridLayout, FlowLayout, JTextField, JButton, JToggleButton +
 *         ButtonGroup, event handling, JOptionPane), Exception (try/catch InvalidUsernameException),
 *         String (validasi nama), Collections (ArrayList tombol huruf)
 */
public class MenuPanel extends JPanel {

    // Lebar kotak nama, dalam jumlah karakter.
    private static final int NAME_FIELD_COLUMNS = 15;
    // Pemilih huruf: 27 tombol ("Normal" + A-Z) dalam 3 baris x 9 kolom.
    private static final int LETTER_ROWS = 3;
    private static final int LETTER_COLUMNS = 9;
    private static final int LETTER_GAP = 6;

    private MainFrame mainFrame;
    private JTextField nameField;
    // Tulisan di bawah pemilih huruf, misalnya "Fokus: huruf J".
    private JLabel focusLabel;
    // Semua tombol di pemilih huruf, supaya warnanya bisa diatur ulang dengan satu loop.
    private ArrayList<JToggleButton> letterButtons;
    // Huruf yang sedang dipilih (huruf besar), atau GameMode.NO_FOCUS untuk "Normal".
    private char selectedLetter;

    public MenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        letterButtons = new ArrayList<>();
        // Saat aplikasi dibuka, "Normal" yang terpilih.
        selectedLetter = GameMode.NO_FOCUS;

        // [MATERI: GUI - layout] BorderLayout: judul di atas, isi di tengah, tombol di bawah.
        setLayout(new BorderLayout(0, Theme.PADDING));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING * 2, Theme.PADDING, Theme.PADDING * 2));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    // Judul "KetikIn" dan satu kalimat penjelasan di bawahnya.
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setOpaque(false);   // tidak menggambar latar sendiri, jadi latar MenuPanel yang terlihat

        JLabel titleLabel = Theme.createLabel("KetikIn", Theme.TITLE_FONT, Theme.ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);

        JLabel subtitleLabel = Theme.createLabel("Latihan mengetik: pilih huruf yang masih sering salah, lalu latih.",
                Theme.NORMAL_FONT, Theme.TEXT_MUTED);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subtitleLabel);
        return panel;
    }

    // Bagian tengah: kotak nama di atas, pemilih huruf di tengah, tombol "Mulai" di bawah.
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.PADDING));
        panel.setOpaque(false);
        panel.add(createNamePanel(), BorderLayout.NORTH);
        panel.add(createLetterPicker(), BorderLayout.CENTER);
        panel.add(createStartPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createNamePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        panel.add(Theme.createLabel("Nama pemain:", Theme.NORMAL_FONT, Theme.TEXT));

        nameField = new JTextField(NAME_FIELD_COLUMNS);
        nameField.setFont(Theme.HEADING_FONT);
        nameField.setBackground(Theme.PANEL);
        nameField.setForeground(Theme.TEXT);
        nameField.setCaretColor(Theme.TEXT);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        panel.add(nameField);

        panel.add(Theme.createLabel("(3-15 huruf/angka/_)", Theme.SMALL_FONT, Theme.TEXT_MUTED));
        return panel;
    }

    // Kotak pemilih huruf: judul kecil, grid tombol, dan tulisan "Fokus: ..." di bawahnya.
    private JPanel createLetterPicker() {
        JPanel box = new JPanel(new BorderLayout(0, Theme.SMALL_PADDING));
        box.setBackground(Theme.PANEL);
        box.setBorder(BorderFactory.createEmptyBorder(Theme.SMALL_PADDING, Theme.PADDING, Theme.SMALL_PADDING, Theme.PADDING));

        box.add(Theme.createLabel("Pilih huruf yang ingin dilatih:", Theme.NORMAL_FONT, Theme.TEXT), BorderLayout.NORTH);
        box.add(createLetterGrid(), BorderLayout.CENTER);

        focusLabel = Theme.createLabel("", Theme.HEADING_FONT, Theme.ACCENT);
        focusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        box.add(focusLabel, BorderLayout.SOUTH);

        // Tampilan awal: "Normal" terpilih.
        updateLetterButtons();
        return box;
    }

    // [MATERI: GUI - GridLayout] 3 baris x 9 kolom: "Normal", lalu A sampai Z.
    // Semua tombol masuk ke SATU ButtonGroup: kalau satu tombol dipilih, tombol lain
    // otomatis tidak terpilih lagi (seperti pilihan ganda, hanya boleh satu jawaban).
    private JPanel createLetterGrid() {
        JPanel grid = new JPanel(new GridLayout(LETTER_ROWS, LETTER_COLUMNS, LETTER_GAP, LETTER_GAP));
        grid.setOpaque(false);
        ButtonGroup group = new ButtonGroup();

        JToggleButton normalButton = createLetterButton("Normal", GameMode.NO_FOCUS, group);
        normalButton.setSelected(true);
        grid.add(normalButton);

        // char juga bisa dipakai di loop: 'A', 'B', 'C', ... sampai 'Z'.
        for (char c = 'A'; c <= 'Z'; c++) {
            grid.add(createLetterButton(String.valueOf(c), c, group));
        }
        return grid;
    }

    // Membuat satu tombol di pemilih huruf. letter = huruf tombol ini, atau NO_FOCUS untuk "Normal".
    private JToggleButton createLetterButton(String text, char letter, ButtonGroup group) {
        JToggleButton button = new JToggleButton(text);
        button.setFont(Theme.BUTTON_FONT);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(Theme.SMALL_PADDING, 0, Theme.SMALL_PADDING, 0));
        // Tampilan bawaan Java mewarnai tombol yang terpilih dengan abu-abu miliknya sendiri.
        // Supaya warna yang kita atur di updateLetterButtons() yang terlihat:
        // setContentAreaFilled(false) = jangan pakai warna bawaan itu,
        // setOpaque(true) = gambar latar tombol dengan warna dari setBackground.
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        // [MATERI: GUI - event handling] lambda, sama seperti di modul praktikum.
        // 'letter' adalah parameter yang tidak pernah diubah, jadi boleh dipakai di dalam lambda.
        button.addActionListener(e -> {
            selectedLetter = letter;
            updateLetterButtons();
        });

        group.add(button);
        letterButtons.add(button);
        return button;
    }

    // Mewarnai ulang semua tombol huruf (yang terpilih = warna aksen) dan
    // memperbarui tulisan "Fokus: ..." di bawahnya.
    private void updateLetterButtons() {
        for (JToggleButton button : letterButtons) {
            if (button.isSelected()) {
                button.setBackground(Theme.ACCENT);
                button.setForeground(Theme.BACKGROUND);
            } else {
                button.setBackground(Theme.BACKGROUND);
                button.setForeground(Theme.TEXT);
            }
        }

        if (selectedLetter == GameMode.NO_FOCUS) {
            focusLabel.setText("Mode normal");
        } else {
            focusLabel.setText("Fokus: huruf " + selectedLetter);
        }
    }

    // Tombol besar "Mulai".
    private JPanel createStartPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        JButton startButton = new JButton("Mulai");
        Theme.styleButton(startButton);
        startButton.setFont(Theme.HEADING_FONT);
        // Lebih lebar dari tombol biasa, supaya jelas inilah tombol utama.
        startButton.setBorder(BorderFactory.createEmptyBorder(Theme.SMALL_PADDING, Theme.PADDING * 4,
                Theme.SMALL_PADDING, Theme.PADDING * 4));
        startButton.addActionListener(e -> {
            startGame();
        });
        panel.add(startButton);
        return panel;
    }

    // Tombol Leaderboard dan Keluar di bagian bawah.
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.PADDING, 0));
        panel.setOpaque(false);

        JButton leaderboardButton = new JButton("Leaderboard");
        Theme.styleButton(leaderboardButton);
        leaderboardButton.addActionListener(e -> {
            mainFrame.showLeaderboard();
        });
        panel.add(leaderboardButton);

        JButton exitButton = new JButton("Keluar");
        Theme.styleButton(exitButton);
        exitButton.addActionListener(e -> {
            confirmExit();
        });
        panel.add(exitButton);
        return panel;
    }

    // Dipanggil saat tombol "Mulai" diklik: cek nama dulu, lalu mulai permainan.
    private void startGame() {
        Player player;
        // [MATERI: Exception] constructor Player mengecek nama dan bisa melempar
        // InvalidUsernameException. Pesannya menjelaskan aturan mana yang dilanggar.
        try {
            player = new Player(nameField.getText());
        } catch (InvalidUsernameException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Nama tidak valid", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocusInWindow();
            return;   // tetap di menu
        }

        // Tampilkan nama yang sudah dirapikan (tanpa spasi di awal/akhir).
        nameField.setText(player.getName());
        mainFrame.setPlayer(player);
        mainFrame.startGame(selectedLetter);
    }

    // Tombol Keluar: tanya konfirmasi dulu, baru System.exit(0).
    private void confirmExit() {
        boolean yes = mainFrame.askConfirmation("Yakin ingin keluar dari KetikIn?", "Keluar");
        if (yes) {
            // 0 = program selesai dengan normal.
            System.exit(0);
        }
    }
}
