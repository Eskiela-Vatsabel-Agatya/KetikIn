package ui;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.LeaderboardStore;
import data.WordBank;
import data.WordFileException;
import mode.ClassicMode;
import mode.GameMode;
import mode.LetterFocusMode;
import model.Player;
import model.ScoreEntry;

// MainFrame: jendela utama, 4 layar ditumpuk pakai CardLayout
// Owner: Hanif Rafa Ramidzar
// Materi: GUI, inheritance, exception, polymorphism
// [MATERI: Inheritance]
public class MainFrame extends JFrame {

    // nama tiap layar di CardLayout
    public static final String MENU = "MENU";
    public static final String GAME = "GAME";
    public static final String RESULT = "RESULT";
    public static final String LEADERBOARD = "LEADERBOARD";

    // bukan WIDTH/HEIGHT karena JFrame sudah punya konstanta itu
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 650;

    // [MATERI: GUI - CardLayout]
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private ResultPanel resultPanel;
    private LeaderboardPanel leaderboardPanel;

    // dimuat sekali, dipakai semua ronde
    private WordBank wordBank;
    private LeaderboardStore leaderboardStore;
    private Player player;
    // true kalau file kata berhasil dimuat
    private boolean ready;

    public MainFrame() {
        super("KetikIn");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        // muncul di tengah layar
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);

        leaderboardStore = new LeaderboardStore();
        ready = loadWordBank();
        // kalau gagal, Main yang menutup jendela ini
        if (ready) {
            buildScreens();
        }
    }

    private boolean loadWordBank() {
        wordBank = new WordBank();
        // [MATERI: Exception]
        try {
            wordBank.loadWords(WordBank.WORDS_PATH);
            return true;
        } catch (WordFileException e) {
            JOptionPane.showMessageDialog(null,
                    "File kata tidak bisa dimuat:\n" + e.getMessage() + "\n\nKetikIn akan ditutup.",
                    "KetikIn - Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void buildScreens() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // tiap panel dikasih this biar tombolnya bisa panggil method MainFrame
        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);
        resultPanel = new ResultPanel(this);
        leaderboardPanel = new LeaderboardPanel(this);

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

    public void showScreen(String name) {
        cardLayout.show(cardPanel, name);
    }

    public void showMenu() {
        showScreen(MENU);
    }

    // [MATERI: Polymorphism - runtime]
    // tipe kembaliannya GameMode, isinya ClassicMode atau LetterFocusMode
    private GameMode createMode(char focusLetter) {
        if (focusLetter == GameMode.NO_FOCUS) {
            return new ClassicMode(wordBank);
        }
        return new LetterFocusMode(wordBank, focusLetter);
    }

    // dipanggil dari menu dan tombol "Main lagi"
    public void startGame(char focusLetter) {
        GameMode mode = createMode(focusLetter);
        // tampilkan layar dulu biar kotak ketik bisa langsung fokus
        showScreen(GAME);
        gamePanel.startGame(mode);
    }

    // ronde selesai, simpan skor lalu tampilkan hasil
    public void showResult(GameMode mode) {
        int score = mode.calculateScore();
        String focus = mode.getFocusLabel();

        // cari rekor lama sebelum skor baru disimpan, cuma fokus yang sama
        int previousBest = leaderboardStore.getPersonalBest(player.getName(), focus);

        ScoreEntry entry = new ScoreEntry(player.getName(), focus, score, mode.getStats());
        // simpan langsung di EDT, filenya kecil jadi tidak bikin macet
        boolean saved = leaderboardStore.addScore(entry);
        boolean newPersonalBest = saved && score > previousBest;

        resultPanel.showResult(mode, player, newPersonalBest);
        showScreen(RESULT);

        // gagal simpan cuma kasih peringatan, hasil tetap tampil
        if (!saved) {
            JOptionPane.showMessageDialog(this, leaderboardStore.getLastErrorMessage(),
                    "Leaderboard", JOptionPane.WARNING_MESSAGE);
        }
    }

    // [MATERI: Polymorphism - compile-time]
    // dari menu, filter "Semua"
    public void showLeaderboard() {
        showLeaderboard(LeaderboardStore.FILTER_ALL);
    }

    // dari layar hasil, filter fokus yang baru dimainkan
    public void showLeaderboard(String focus) {
        showScreen(LEADERBOARD);
        leaderboardPanel.showFilter(focus);
    }

    // [MATERI: GUI - JOptionPane]
    // tombol Ya/Tidak, true kalau pilih Ya
    public boolean askConfirmation(String message, String title) {
        String[] options = {"Ya", "Tidak"};
        int choice = JOptionPane.showOptionDialog(this, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);
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
