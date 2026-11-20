package ui;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.WordBank;
import data.WordFileException;
import mode.ClassicMode;
import mode.GameMode;
import model.Player;

/*
 * MainFrame: satu-satunya jendela aplikasi. Di dalamnya ada 4 layar (Menu, Game, Hasil,
 * Leaderboard) yang ditumpuk dengan CardLayout; hanya satu yang terlihat pada satu waktu.
 * MainFrame juga menyimpan data bersama: WordBank (dimuat sekali) dan Player saat ini.
 * Panel-panel memanggil method di kelas ini untuk pindah layar.
 * Owner: Person C
 * Materi: GUI (JFrame, CardLayout, JOptionPane), Inheritance (extends JFrame),
 *         Exception (try/catch WordFileException), Polymorphism runtime (createMode)
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
        // TODO Phase 4 dan 5: tambahkan Time Attack, Survival, dan Code di sini.
        // Sementara mode lain belum bisa dipilih dari menu, jadi pakai Classic.
        return new ClassicMode(wordBank);
    }

    // Memulai ronde baru untuk mode tertentu. Dipanggil dari menu dan dari tombol "Main lagi".
    public void startGame(String modeName) {
        GameMode mode = createMode(modeName);
        // Tampilkan layar game dulu, baru mulai ronde, supaya kotak ketik bisa langsung diberi fokus.
        showScreen(GAME);
        gamePanel.startGame(mode);
    }

    // Dipanggil GamePanel ketika ronde selesai.
    public void showResult(GameMode mode) {
        resultPanel.showResult(mode, player);
        showScreen(RESULT);
    }

    public void showLeaderboard() {
        // TODO Phase 5: leaderboardPanel.refresh(); lalu tampilkan.
        showScreen(LEADERBOARD);
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
