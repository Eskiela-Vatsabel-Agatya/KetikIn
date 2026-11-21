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

// LeaderboardPanel: tabel 10 skor terbaik dengan filter Semua, Normal, atau huruf
// Owner: Hanif Rafa Ramidzar
// Materi: GUI, event handling, collections
public class LeaderboardPanel extends JPanel {

    public static final int TOP_LIMIT = 10;
    private static final int ROW_HEIGHT = 30;

    private static final String[] COLUMN_NAMES = {"Peringkat", "Nama", "Fokus", "Skor", "WPM", "Akurasi", "Tanggal"};

    private MainFrame mainFrame;
    private LeaderboardStore store;
    // [MATERI: GUI - JComboBox]
    private JComboBox<String> filterBox;
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

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setOpaque(false);

        JLabel titleLabel = Theme.createLabel("Leaderboard", Theme.TITLE_FONT, Theme.ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);

        panel.add(createFilterPanel());
        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.SMALL_PADDING, 0));
        panel.setOpaque(false);

        panel.add(Theme.createLabel("Tampilkan:", Theme.NORMAL_FONT, Theme.TEXT));

        // isi filter: Semua, Normal, lalu A sampai Z
        filterBox = new JComboBox<>();
        filterBox.addItem(LeaderboardStore.FILTER_ALL);
        filterBox.addItem(GameMode.FOCUS_NORMAL);
        for (char c = 'A'; c <= 'Z'; c++) {
            filterBox.addItem(String.valueOf(c));
        }
        filterBox.setFont(Theme.NORMAL_FONT);
        // [MATERI: GUI - event handling]
        filterBox.addActionListener(e -> {
            refresh();
        });
        panel.add(filterBox);

        panel.add(Theme.createLabel("(" + TOP_LIMIT + " skor terbaik)", Theme.NORMAL_FONT, Theme.TEXT_MUTED));
        return panel;
    }

    // [MATERI: GUI - JTable]
    // pola dari modul praktikum, model kosong lalu diisi pakai loop di refresh()
    private JScrollPane createTablePanel() {
        // anonymous class biar sel tidak bisa diedit
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        styleTable(table);

        // judul kolom cuma tampil kalau tabel ada di JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Theme.PANEL);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    // lokasi file di kiri, tombol kembali di kanan
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // di CENTER biar path panjang dipotong "..." dan tidak menimpa tombol
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

    // setSelectedItem ikut memicu listener, jadi refresh() jalan sendiri
    public void showFilter(String focus) {
        filterBox.setSelectedItem(focus);
    }

    // baca ulang skor dan isi ulang tabel
    public void refresh() {
        String focus = (String) filterBox.getSelectedItem();

        tableModel.setRowCount(0);
        List<ScoreEntry> topEntries = store.getTop(focus, TOP_LIMIT);
        for (int i = 0; i < topEntries.size(); i++) {
            ScoreEntry entry = topEntries.get(i);
            Object[] row = {
                String.valueOf(i + 1),
                entry.getName(),
                entry.getMode(),
                String.valueOf(entry.getScore()),
                String.format("%.1f", entry.getWpm()),
                String.format("%.1f%%", entry.getAccuracy()),
                entry.getDate()
            };
            tableModel.addRow(row);
        }

        // file gagal dibaca, kasih tahu pemain
        String errorMessage = store.getLastErrorMessage();
        if (errorMessage != null) {
            JOptionPane.showMessageDialog(this, errorMessage, "Leaderboard", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void styleTable(JTable table) {
        table.setFont(Theme.NORMAL_FONT);
        table.setRowHeight(ROW_HEIGHT);
        table.setBackground(Theme.PANEL);
        table.setForeground(Theme.TEXT);
        table.setGridColor(Theme.BACKGROUND);
        table.setSelectionBackground(Theme.ACCENT);
        table.setSelectionForeground(Theme.BACKGROUND);
        // area kosong di bawah baris terakhir ikut warna PANEL
        table.setFillsViewportHeight(true);

        // isi sel rata tengah
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.BUTTON_FONT);
        header.setBackground(Theme.ACCENT);
        header.setForeground(Theme.BACKGROUND);
        header.setReorderingAllowed(false);
    }
}
