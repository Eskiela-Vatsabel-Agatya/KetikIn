package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import data.LeaderboardStore;
import mode.GameMode;
import model.ScoreEntry;

/*
 * LeaderboardPanel: layar Leaderboard. Satu JTable dengan 10 skor terbaik
 * (Peringkat, Nama, Fokus, Skor, WPM, Akurasi, Tanggal), dan sebuah JComboBox di atasnya
 * untuk memilih filter: "Semua", "Normal", atau satu huruf A-Z.
 * Data dibaca ulang dari file setiap kali filter dipilih (refresh).
 * Owner: Person C
 * Materi: GUI (JTable + DefaultTableModel, JComboBox, event handling, anonymous inner class,
 *         JOptionPane), Collections (List dari LeaderboardStore, diisi ke tabel dengan loop)
 */
public class LeaderboardPanel extends JPanel {

    public static final int TOP_LIMIT = 10;
    private static final int ROW_HEIGHT = 30;

    // Judul kolom tabel.
    private static final String[] COLUMN_NAMES = {"Peringkat", "Nama", "Fokus", "Skor", "WPM", "Akurasi", "Tanggal"};

    private MainFrame mainFrame;
    private LeaderboardStore store;
    // [MATERI: GUI - JComboBox] daftar pilihan filter.
    private JComboBox<String> filterBox;
    // Isi tabel. Satu model yang sama dipakai terus; isinya dihapus dan diisi ulang di refresh().
    private DefaultTableModel tableModel;

    public LeaderboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.store = mainFrame.getLeaderboardStore();

        setLayout(new BorderLayout(0, Theme.PADDING));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    // Bagian atas: judul, lalu baris filter di bawahnya.
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setOpaque(false);

        JLabel titleLabel = Theme.createLabel("Leaderboard", Theme.TITLE_FONT, Theme.ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);

        panel.add(createFilterPanel());
        return panel;
    }

    // Tulisan "Tampilkan:", kotak pilihan filter, dan keterangan singkat.
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.SMALL_PADDING, 0));
        panel.setOpaque(false);

        panel.add(Theme.createLabel("Tampilkan:", Theme.NORMAL_FONT, Theme.TEXT));

        // Pilihan: "Semua", "Normal", lalu "A" sampai "Z".
        filterBox = new JComboBox<>();
        filterBox.addItem(LeaderboardStore.FILTER_ALL);
        filterBox.addItem(GameMode.FOCUS_NORMAL);
        for (char c = 'A'; c <= 'Z'; c++) {
            filterBox.addItem(String.valueOf(c));
        }
        filterBox.setFont(Theme.NORMAL_FONT);
        // [MATERI: GUI - event handling] dipanggil setiap kali pilihan filter diganti.
        filterBox.addActionListener(e -> {
            refresh();
        });
        panel.add(filterBox);

        panel.add(Theme.createLabel("(" + TOP_LIMIT + " skor terbaik)", Theme.NORMAL_FONT, Theme.TEXT_MUTED));
        return panel;
    }

    // Tabel (di dalam JScrollPane). Barisnya diisi nanti di refresh().
    // [MATERI: GUI - JTable & Collections] pola dari modul praktikum:
    // DefaultTableModel(kolom, 0) = tabel dengan judul kolom dan 0 baris, lalu diisi dengan loop.
    private JScrollPane createTablePanel() {
        // Anonymous inner class: kita membuat "anak" DefaultTableModel tanpa nama kelas,
        // hanya untuk menimpa (override) satu method. isCellEditable selalu false,
        // jadi sel tabel tidak bisa diedit dengan klik dua kali.
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        styleTable(table);

        // JScrollPane: kalau barisnya banyak, tabel bisa digulir. Judul kolom juga hanya
        // tampil kalau JTable ada di dalam JScrollPane.
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Theme.PANEL);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
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

    // Memilih satu filter (misalnya "J" setelah bermain fokus J) lalu menampilkan datanya.
    // setSelectedItem juga memanggil ActionListener filterBox di atas, jadi refresh()
    // berjalan dengan sendirinya (sekali), bahkan kalau pilihannya sama dengan sebelumnya.
    public void showFilter(String focus) {
        filterBox.setSelectedItem(focus);
    }

    // Membaca ulang data skor untuk filter yang dipilih, lalu mengisi ulang tabel.
    public void refresh() {
        String focus = (String) filterBox.getSelectedItem();

        // Hapus semua baris lama (jumlah baris dibuat 0), lalu isi lagi dari data terbaru.
        tableModel.setRowCount(0);
        List<ScoreEntry> topEntries = store.getTop(focus, TOP_LIMIT);
        for (int i = 0; i < topEntries.size(); i++) {
            ScoreEntry entry = topEntries.get(i);
            // Satu baris tabel = array Object (setiap sel boleh berisi tipe apa saja).
            // Di sini semua sel kita isi dengan String supaya tampilannya kita atur sendiri.
            Object[] row = {
                String.valueOf(i + 1),                               // peringkat mulai dari 1
                entry.getName(),
                entry.getMode(),                                     // "Normal" atau huruf fokus
                String.valueOf(entry.getScore()),
                String.format("%.1f", entry.getWpm()),               // 1 angka di belakang koma
                String.format("%.1f%%", entry.getAccuracy()),        // %% = tanda persen
                entry.getDate()
            };
            tableModel.addRow(row);
        }

        // File tidak bisa dibaca sama sekali: tabel kosong, dan pemain diberi tahu.
        String errorMessage = store.getLastErrorMessage();
        if (errorMessage != null) {
            JOptionPane.showMessageDialog(this, errorMessage, "Leaderboard", JOptionPane.WARNING_MESSAGE);
        }
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
