package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import data.LeaderboardStore;
import mode.GameMode;
import model.ScoreEntry;

/*
 * LeaderboardPanel: layar Leaderboard. Satu tab per mode, setiap tab berisi JTable
 * dengan 10 skor terbaik (Peringkat, Nama, Score, WPM, Akurasi, Tanggal).
 * Data dibaca ulang dari file setiap kali layar ini dibuka (refresh).
 * Owner: Person C
 * Materi: GUI (JTabbedPane, JTable + DefaultTableModel, anonymous inner class, JOptionPane),
 *         Collections (List dari LeaderboardStore, diisi ke tabel dengan loop)
 */
public class LeaderboardPanel extends JPanel {

    public static final int TOP_LIMIT = 10;
    private static final int ROW_HEIGHT = 30;

    // Urutan tab, sama dengan urutan tombol mode di menu.
    private static final String[] MODE_NAMES = {
        GameMode.MODE_CLASSIC, GameMode.MODE_TIME_ATTACK, GameMode.MODE_SURVIVAL, GameMode.MODE_CODE
    };
    // Judul kolom tabel.
    private static final String[] COLUMN_NAMES = {"Peringkat", "Nama", "Score", "WPM", "Akurasi", "Tanggal"};

    private MainFrame mainFrame;
    private JTabbedPane tabbedPane;
    private LeaderboardStore store;

    public LeaderboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.store = mainFrame.getLeaderboardStore();

        setLayout(new BorderLayout(0, Theme.PADDING));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        add(createTitlePanel(), BorderLayout.NORTH);

        // [MATERI: GUI - JTabbedPane] satu tab per mode. Isi tab dibuat di refresh().
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.BUTTON_FONT);
        add(tabbedPane, BorderLayout.CENTER);

        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    // Judul dan keterangan singkat di bagian atas.
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setOpaque(false);

        JLabel titleLabel = Theme.createLabel("Leaderboard", Theme.TITLE_FONT, Theme.ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);

        JLabel infoLabel = Theme.createLabel(TOP_LIMIT + " skor terbaik untuk setiap mode.",
                Theme.NORMAL_FONT, Theme.TEXT_MUTED);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(infoLabel);
        return panel;
    }

    // Bagian bawah: lokasi file di kiri, tombol kembali di kanan.
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Lokasi file ditampilkan supaya mudah dicari (misalnya untuk dicek atau dihapus).
        // CENTER (bukan WEST): label hanya mendapat sisa tempat di samping tombol, jadi
        // path yang sangat panjang dipotong dengan "..." dan tidak menimpa tombol.
        JLabel fileLabel = Theme.createLabel("File: " + store.getFilePath(), Theme.SMALL_FONT, Theme.TEXT_MUTED);
        panel.add(fileLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        JButton backButton = new JButton("Kembali ke Menu");
        Theme.styleButton(backButton);
        backButton.addActionListener(e -> {
            mainFrame.showMenu();
        });
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.EAST);
        return panel;
    }

    // Membaca ulang data skor dan membangun ulang semua tabel.
    public void refresh() {
        // Buang semua tab lama, lalu buat lagi dari data terbaru.
        tabbedPane.removeAll();
        for (int i = 0; i < MODE_NAMES.length; i++) {
            tabbedPane.addTab(MODE_NAMES[i], createTableForMode(MODE_NAMES[i]));
        }

        // File tidak bisa dibaca sama sekali: tabel kosong, dan pemain diberi tahu SEKALI
        // (bukan sekali per tab).
        String errorMessage = store.getLastErrorMessage();
        if (errorMessage != null) {
            JOptionPane.showMessageDialog(this, errorMessage, "Leaderboard", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Membuka tab milik mode tertentu (misalnya mode yang baru saja dimainkan).
    public void selectTab(String modeName) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(modeName)) {
                tabbedPane.setSelectedIndex(i);
            }
        }
    }

    // Membuat satu tabel (di dalam JScrollPane) untuk satu mode.
    // [MATERI: GUI - JTable & Collections] pola dari modul praktikum:
    // DefaultTableModel(kolom, 0) = tabel dengan judul kolom dan 0 baris, lalu diisi dengan loop.
    private JScrollPane createTableForMode(String modeName) {
        // Anonymous inner class: kita membuat "anak" DefaultTableModel tanpa nama kelas,
        // hanya untuk menimpa (override) satu method. isCellEditable selalu false,
        // jadi sel tabel tidak bisa diedit dengan klik dua kali.
        DefaultTableModel tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<ScoreEntry> topEntries = store.getTop(modeName, TOP_LIMIT);
        for (int i = 0; i < topEntries.size(); i++) {
            ScoreEntry entry = topEntries.get(i);
            // Satu baris tabel = array Object (setiap sel boleh berisi tipe apa saja).
            // Di sini semua sel kita isi dengan String supaya tampilannya kita atur sendiri.
            Object[] row = {
                String.valueOf(i + 1),                               // peringkat mulai dari 1
                entry.getName(),
                String.valueOf(entry.getScore()),
                String.format("%.1f", entry.getWpm()),               // 1 angka di belakang koma
                String.format("%.1f%%", entry.getAccuracy()),        // %% = tanda persen
                entry.getDate()
            };
            tableModel.addRow(row);
        }

        JTable table = new JTable(tableModel);
        styleTable(table);

        // JScrollPane: kalau barisnya banyak, tabel bisa digulir. Judul kolom juga hanya
        // tampil kalau JTable ada di dalam JScrollPane.
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Theme.PANEL);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    // Warna dan font tabel disesuaikan dengan Theme.
    private void styleTable(JTable table) {
        table.setFont(Theme.NORMAL_FONT);
        table.setRowHeight(ROW_HEIGHT);
        table.setBackground(Theme.PANEL);
        table.setForeground(Theme.TEXT);
        table.setGridColor(Theme.BACKGROUND);
        table.setSelectionBackground(Theme.ACCENT);
        table.setSelectionForeground(Theme.BACKGROUND);
        // Area kosong di bawah baris terakhir ikut berwarna PANEL (tidak putih).
        table.setFillsViewportHeight(true);

        // Isi setiap sel ditaruh di tengah (bawaannya rata kiri, menempel di garis sel).
        // Renderer = objek yang "menggambar" isi sel; kita pakai renderer bawaan Swing
        // dan hanya mengubah perataannya.
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.BUTTON_FONT);
        header.setBackground(Theme.ACCENT);
        header.setForeground(Theme.BACKGROUND);
        // Kolom tidak bisa digeser-geser urutannya dengan mouse.
        header.setReorderingAllowed(false);
    }
}
