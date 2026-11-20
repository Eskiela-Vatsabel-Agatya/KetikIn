package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import data.InvalidUsernameException;
import mode.GameMode;
import model.Player;

/*
 * MenuPanel: layar pertama. Pemain mengisi nama, memilih mode permainan, atau membuka
 * Leaderboard / keluar.
 * Owner: Person C
 * Materi: GUI (BorderLayout, GridLayout, FlowLayout, JTextField, JButton, event handling,
 *         JOptionPane), Exception (try/catch InvalidUsernameException), String (validasi nama)
 */
public class MenuPanel extends JPanel {

    // Lebar kotak nama, dalam jumlah karakter.
    private static final int NAME_FIELD_COLUMNS = 15;

    private MainFrame mainFrame;
    private JTextField nameField;

    public MenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

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

        JLabel subtitleLabel = Theme.createLabel("Latihan mengetik: cepat, tepat, dan seru.",
                Theme.NORMAL_FONT, Theme.TEXT_MUTED);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subtitleLabel);
        return panel;
    }

    // Bagian tengah: kotak nama di atas, empat kartu mode di bawahnya.
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.PADDING));
        panel.setOpaque(false);
        panel.add(createNamePanel(), BorderLayout.NORTH);
        panel.add(createModeGrid(), BorderLayout.CENTER);
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

    // [MATERI: GUI - GridLayout] 2 baris x 2 kolom, satu kartu per mode.
    private JPanel createModeGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, Theme.PADDING, Theme.PADDING));
        grid.setOpaque(false);

        // [MATERI: GUI - event handling] lambda, sama seperti di modul praktikum.
        JButton classicButton = new JButton(GameMode.MODE_CLASSIC);
        classicButton.addActionListener(e -> {
            startMode(GameMode.MODE_CLASSIC);
        });
        grid.add(createModeCard(classicButton, "30 kata acak. Selesaikan secepat dan setepat mungkin."));

        JButton timeAttackButton = new JButton(GameMode.MODE_TIME_ATTACK);
        timeAttackButton.addActionListener(e -> {
            startMode(GameMode.MODE_TIME_ATTACK);
        });
        grid.add(createModeCard(timeAttackButton, "60 detik. Ketik kata sebanyak-banyaknya, Spasi untuk kirim."));

        JButton survivalButton = new JButton(GameMode.MODE_SURVIVAL);
        survivalButton.addActionListener(e -> {
            startMode(GameMode.MODE_SURVIVAL);
        });
        grid.add(createModeCard(survivalButton, "5 detik per kata, 3 nyawa. Salah atau telat = nyawa hilang."));

        JButton codeButton = new JButton(GameMode.MODE_CODE);
        codeButton.addActionListener(e -> {
            startMode(GameMode.MODE_CODE);
        });
        grid.add(createModeCard(codeButton, "Ketik 5 baris kode Java persis sama. Enter untuk kirim satu baris."));

        return grid;
    }

    // Satu kartu: tombol mode di atas, penjelasan singkat di bawahnya.
    private JPanel createModeCard(JButton button, String description) {
        JPanel card = new JPanel(new BorderLayout(0, Theme.SMALL_PADDING));
        card.setBackground(Theme.PANEL);
        card.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        Theme.styleButton(button);
        card.add(button, BorderLayout.NORTH);

        // <html> membuat teks panjang otomatis turun baris kalau tidak muat.
        JLabel descriptionLabel = Theme.createLabel("<html>" + description + "</html>",
                Theme.NORMAL_FONT, Theme.TEXT_MUTED);
        descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
        card.add(descriptionLabel, BorderLayout.CENTER);
        return card;
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

    // Dipanggil saat tombol mode diklik: cek nama dulu, lalu mulai permainan.
    private void startMode(String modeName) {
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
        mainFrame.startGame(modeName);
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
