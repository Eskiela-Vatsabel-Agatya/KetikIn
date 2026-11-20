package ui;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import data.WordBank;
import mode.GameMode;
import model.Player;

/*
 * MainFrame: satu-satunya jendela aplikasi. Di dalamnya ada 4 layar (Menu, Game, Hasil,
 * Leaderboard) yang ditumpuk dengan CardLayout; hanya satu yang terlihat pada satu waktu.
 * Panel-panel memanggil method di kelas ini untuk pindah layar.
 * Owner: Person C
 * Materi: GUI (JFrame, CardLayout), Inheritance (extends JFrame)
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

    private WordBank wordBank;
    private Player player;

    public MainFrame() {
        super("KetikIn");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        // null = tidak relatif ke jendela lain, jadi jendela muncul di tengah layar.
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // TODO Phase 3: muat wordBank sekali di sini (tampilkan dialog error kalau gagal).

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Setiap panel diberi referensi "this" (MainFrame ini), supaya tombol di panel
        // bisa memanggil showScreen() dan pindah layar.
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

    // Menampilkan layar dengan nama tertentu (MENU, GAME, RESULT, atau LEADERBOARD).
    public void showScreen(String name) {
        cardLayout.show(cardPanel, name);
    }

    public void showMenu() {
        showScreen(MENU);
    }

    public void startGame(GameMode mode) {
        // TODO Phase 3: gamePanel.startGame(mode); showScreen(GAME);
    }

    public void showResult(GameMode mode) {
        // TODO Phase 3: resultPanel.showResult(mode, player); showScreen(RESULT);
    }

    public void showLeaderboard() {
        // TODO Phase 5: leaderboardPanel.refresh(); lalu tampilkan.
        showScreen(LEADERBOARD);
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
