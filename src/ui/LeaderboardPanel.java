package ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import data.LeaderboardStore;

/*
 * LeaderboardPanel: layar Leaderboard. Satu tab per mode, setiap tab berisi JTable
 * dengan 10 skor terbaik (Peringkat, Nama, Skor, WPM, Akurasi, Tanggal).
 * Owner: Person C
 * Materi: GUI (JTabbedPane, JTable), Collections (List dari LeaderboardStore)
 */
public class LeaderboardPanel extends JPanel {

    public static final int TOP_LIMIT = 10;

    private MainFrame mainFrame;
    private JTabbedPane tabbedPane;
    private LeaderboardStore store;

    public LeaderboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // SEMENTARA: hanya judul + tombol kembali. Tabel sungguhan dibuat di Phase 5.
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        JLabel titleLabel = Theme.createLabel("Leaderboard (segera hadir)", Theme.TITLE_FONT, Theme.TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Kembali ke Menu");
        Theme.styleButton(backButton);
        backButton.addActionListener(e -> {
            mainFrame.showMenu();
        });
        add(backButton, BorderLayout.SOUTH);
    }

    // Membaca ulang data skor dan membangun ulang semua tabel.
    public void refresh() {
        // TODO Phase 5
    }

    // Membuat satu tabel (di dalam JScrollPane) untuk satu mode.
    private JScrollPane createTableForMode(String modeName) {
        // TODO Phase 5
        return new JScrollPane();
    }
}
