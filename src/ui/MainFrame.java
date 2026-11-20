package ui;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.LeaderboardStore;
import data.WordBank;
import data.WordFileException;
import mode.ClassicMode;
import mode.CodeMode;
import mode.GameMode;
import mode.SurvivalMode;
import mode.TimeAttackMode;
import model.Player;
import model.ScoreEntry;

/*
 * MainFrame: satu-satunya jendela aplikasi. Di dalamnya ada 4 layar (Menu, Game, Hasil,
 * Leaderboard) yang ditumpuk dengan CardLayout; hanya satu yang terlihat pada satu waktu.
 * MainFrame juga menyimpan data bersama: WordBank (dimuat sekali), LeaderboardStore,
 * dan Player saat ini. Panel-panel memanggil method di kelas ini untuk pindah layar.
 * Owner: Person C
 * Materi: GUI (JFrame, CardLayout, JOptionPane), Inheritance (extends JFrame),
 *         Exception (try/catch WordFileException), Polymorphism runtime (createMode),
 *         Polymorphism compile-time (overload showLeaderboard)
 */
// [MATERI: Inheritance] MainFrame adalah JFrame yang kita tambahi isi sendiri.
public class MainFrame extends JFrame {

    // Nama setiap layar di CardLayout.
    public static final String MENU = "MENU";
    public static final String GAME = "GAME";
    public static final String RESULT = "RESULT";
    public static final String LEADERBOARD = "LEADERBOARD";

    // Ukuran jendela. (Tidak dinamai WIDTH/HEIGHT karena JFrame sudah punya konstanta bernama itu.)
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 650;

    // [MATERI: GUI - CardLayout]
    private CardLayout cardLayout;
    // Panel "wadah" yang memakai CardLayout; keempat layar dimasukkan ke sini.
    private JPanel cardPanel;

    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private ResultPanel resultPanel;
    private LeaderboardPanel leaderboardPanel;

    // Dimuat SEKALI saat aplikasi mulai, lalu dipakai bersama oleh semua ronde.
    private WordBank wordBank;
    // Satu objek untuk membaca/menulis file leaderboard, dipakai bersama oleh semua layar.
    private LeaderboardStore leaderboardStore;
    private Player player;
    // true kalau file kata berhasil dimuat dan semua layar sudah dibuat.
    private boolean ready;

    public MainFrame() {
        super("KetikIn");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        // null = tidak relatif ke jendela lain, jadi jendela muncul di tengah layar.
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);

        leaderboardStore = new LeaderboardStore();
        ready = loadWordBank();
        // Layar hanya dibuat kalau file kata berhasil dimuat. Kalau gagal, Main akan menutup jendela ini.
        if (ready) {
            buildScreens();
        }
    }

    // Memuat file kata dan potongan kode. Mengembalikan false (dan menampilkan pesan) kalau gagal.
    private boolean loadWordBank() {
        wordBank = new WordBank();
        // [MATERI: Exception] loadWords/loadSnippets bisa melempar WordFileException (checked),
        // jadi compiler mewajibkan try/catch di sini.
        try {
            wordBank.loadWords(WordBank.WORDS_PATH);
            wordBank.loadSnippets(WordBank.SNIPPETS_PATH);
            return true;
        } catch (WordFileException e) {
            // Tanpa daftar kata, permainan tidak bisa berjalan. Beri tahu pemain dengan sopan.
            JOptionPane.showMessageDialog(null,
                    "File kata tidak bisa dimuat:\n" + e.getMessage() + "\n\nKetikIn akan ditutup.",
                    "KetikIn - Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Membuat keempat layar dan memasukkannya ke CardLayout.
    private void buildScreens() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Setiap panel diberi referensi "this" (MainFrame ini), supaya tombol di panel
        // bisa memanggil method MainFrame, misalnya showMenu() atau startGame().
        // Cara ini sederhana: panel cukup tahu satu objek, yaitu MainFrame.
        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);
        resultPanel = new ResultPanel(this);
        leaderboardPanel = new LeaderboardPanel(this);

        // Argumen kedua = nama kartu, dipakai nanti oleh showScreen().
        cardPanel.add(menuPanel, MENU);
        cardPanel.add(gamePanel, GAME);
        cardPanel.add(resultPanel, RESULT);
        cardPanel.add(leaderboardPanel, LEADERBOARD);

        add(cardPanel);
        showScreen(MENU);
    }

    public boolean isReady() {
        return ready;
    }

    // Menampilkan layar dengan nama tertentu (MENU, GAME, RESULT, atau LEADERBOARD).
    public void showScreen(String name) {
        cardLayout.show(cardPanel, name);
    }

    public void showMenu() {
        showScreen(MENU);
    }

    // Membuat objek mode baru berdasarkan namanya.
    // [MATERI: Polymorphism - runtime] tipe kembaliannya GameMode, tapi objek aslinya
    // bisa ClassicMode, TimeAttackMode, dan seterusnya (upcasting).
    private GameMode createMode(String modeName) {
        if (modeName.equals(GameMode.MODE_CLASSIC)) {
            return new ClassicMode(wordBank);
        }
        if (modeName.equals(GameMode.MODE_TIME_ATTACK)) {
            return new TimeAttackMode(wordBank);
        }
        if (modeName.equals(GameMode.MODE_SURVIVAL)) {
            return new SurvivalMode(wordBank);
        }
        if (modeName.equals(GameMode.MODE_CODE)) {
            return new CodeMode(wordBank);
        }

        // Nama mode tidak dikenal = salah di kode kita sendiri (bukan salah pemain).
        // Lebih baik langsung error dengan pesan jelas daripada diam-diam memakai mode lain.
        throw new IllegalArgumentException("Mode tidak dikenal: " + modeName);
    }

    // Memulai ronde baru untuk mode tertentu. Dipanggil dari menu dan dari tombol "Main lagi".
    public void startGame(String modeName) {
        GameMode mode = createMode(modeName);
        // Tampilkan layar game dulu, baru mulai ronde, supaya kotak ketik bisa langsung diberi fokus.
        showScreen(GAME);
        gamePanel.startGame(mode);
    }

    // Dipanggil GamePanel ketika ronde selesai (BUKAN saat pemain keluar di tengah ronde).
    // Skor disimpan ke file dulu, lalu layar hasil ditampilkan.
    public void showResult(GameMode mode) {
        int score = mode.calculateScore();

        // Rekor lama dicari SEBELUM skor baru disimpan, supaya bisa dibandingkan.
        int previousBest = leaderboardStore.getPersonalBest(player.getName(), mode.getName());

        // Constructor ScoreEntry versi kedua: WPM, akurasi, dan tanggal diambil otomatis.
        ScoreEntry entry = new ScoreEntry(player.getName(), mode.getName(), score, mode.getStats());
        // Disimpan langsung di EDT, tanpa thread tambahan: filenya hanya beberapa baris,
        // jadi menulisnya sangat cepat dan layar tidak sempat terasa macet.
        boolean saved = leaderboardStore.addScore(entry);
        boolean newPersonalBest = saved && score > previousBest;

        resultPanel.showResult(mode, player, newPersonalBest);
        showScreen(RESULT);

        // Gagal menyimpan bukan alasan untuk menyembunyikan hasil: layar hasil tetap tampil,
        // pemain hanya diberi peringatan.
        if (!saved) {
            JOptionPane.showMessageDialog(this, leaderboardStore.getLastErrorMessage(),
                    "Leaderboard", JOptionPane.WARNING_MESSAGE);
        }
    }

    // [MATERI: Polymorphism - compile-time] dua method showLeaderboard dengan parameter berbeda.
    // Dari menu: tanpa parameter, tab pertama (Classic) yang terbuka.
    public void showLeaderboard() {
        showLeaderboard(GameMode.MODE_CLASSIC);
    }

    // Dari layar hasil: tab mode yang baru saja dimainkan langsung terbuka.
    public void showLeaderboard(String modeName) {
        showScreen(LEADERBOARD);
        // Data dibaca ulang setiap kali layar ini dibuka, supaya skor terbaru ikut tampil.
        leaderboardPanel.refresh();
        leaderboardPanel.selectTab(modeName);
    }

    // Menampilkan dialog konfirmasi dengan tombol "Ya" dan "Tidak" (bahasa Indonesia).
    // Mengembalikan true kalau pemain memilih "Ya".
    public boolean askConfirmation(String message, String title) {
        String[] options = {"Ya", "Tidak"};
        int choice = JOptionPane.showOptionDialog(this, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);
        // choice = nomor tombol yang diklik: 0 = "Ya", 1 = "Tidak", -1 = dialog ditutup.
        return choice == 0;
    }

    public WordBank getWordBank() {
        return wordBank;
    }

    public LeaderboardStore getLeaderboardStore() {
        return leaderboardStore;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
