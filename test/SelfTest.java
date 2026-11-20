import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import data.InvalidUsernameException;
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
import model.TypingStats;

/*
 * SelfTest: program kecil untuk mengetes bagian non-GUI (Player, TypingStats, ScoreEntry,
 * WordBank, mode-mode permainan, LeaderboardStore) tanpa JUnit. Mencetak PASS/FAIL untuk setiap pengecekan, lalu jumlahnya.
 * Dijalankan dengan selftest.bat (atau: java -ea -cp out SelfTest).
 * Owner: Person A / B / C (masing-masing menulis tes untuk kelasnya sendiri)
 * Materi: Assertion, Exception (try/catch untuk exception yang DIHARAPKAN), Collections
 */
public class SelfTest {

    // Selisih maksimal saat membandingkan angka double (double tidak selalu persis sama).
    private static final double TOLERANCE = 0.001;

    private static int passCount = 0;
    private static int failCount = 0;

    public static void main(String[] args) {
        checkAssertionsEnabled();

        testPlayer();
        testTypingStats();
        testScoreEntry();
        testWordBank();
        testTimedModes();
        testCodeMode();
        testLeaderboardStore();

        System.out.println();
        System.out.println("Hasil: " + passCount + " PASS, " + failCount + " FAIL");

        // [MATERI: Assertion] asumsi internal: tes di atas pasti sudah menjalankan pengecekan.
        assert passCount + failCount > 0 : "Tidak ada tes yang dijalankan";
    }

    // ---------------------------------------------------------------
    // Alat bantu
    // ---------------------------------------------------------------

    // Mencetak PASS atau FAIL dan menghitungnya.
    private static void check(String description, boolean condition) {
        if (condition) {
            passCount++;
            System.out.println("PASS: " + description);
        } else {
            failCount++;
            System.out.println("FAIL: " + description);
        }
    }

    private static boolean isClose(double a, double b) {
        return Math.abs(a - b) < TOLERANCE;
    }

    // Trik untuk mengetahui apakah -ea aktif:
    // perintah "assert" hanya DIJALANKAN kalau assertion aktif. Di sini isinya adalah
    // assignment (=, bukan ==) yang hasilnya true, jadi tidak pernah gagal;
    // efek sampingnya hanya mengubah assertionsOn menjadi true.
    private static void checkAssertionsEnabled() {
        boolean assertionsOn = false;
        assert assertionsOn = true;
        if (assertionsOn) {
            System.out.println("(Assertion aktif)");
        } else {
            System.out.println("PERINGATAN: assertion tidak aktif. Jalankan dengan: java -ea -cp out SelfTest");
        }
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Player
    // ---------------------------------------------------------------

    private static void testPlayer() {
        System.out.println("--- Player ---");

        try {
            Player player = new Player("  Budi_123  ");
            check("nama valid diterima dan di-trim", player.getName().equals("Budi_123"));
        } catch (InvalidUsernameException e) {
            check("nama valid diterima (error: " + e.getMessage() + ")", false);
        }

        // Satu nama salah untuk setiap aturan.
        expectInvalidName("   ");               // kosong
        expectInvalidName("ab");                // terlalu pendek
        expectInvalidName("abcdefghijklmnop");  // terlalu panjang (16)
        expectInvalidName("1budi");             // tidak diawali huruf
        expectInvalidName("_budi");             // tidak diawali huruf
        expectInvalidName("budi sari");         // ada spasi di tengah
        expectInvalidName("budi,123");          // ada koma (akan merusak CSV)
        expectInvalidName("ADMIN");             // nama terlarang
        expectInvalidName("Null");              // nama terlarang
        expectInvalidName("guest");             // nama terlarang
        System.out.println();
    }

    // Membuat Player dengan nama salah. Tes LULUS kalau exception-nya benar-benar dilempar.
    private static void expectInvalidName(String name) {
        try {
            new Player(name);
            check("nama \"" + name + "\" seharusnya ditolak", false);
        } catch (InvalidUsernameException e) {
            check("nama \"" + name + "\" ditolak -> " + e.getMessage(), true);
        }
    }

    // ---------------------------------------------------------------
    // TypingStats
    // ---------------------------------------------------------------

    private static void testTypingStats() {
        System.out.println("--- TypingStats ---");

        TypingStats empty = new TypingStats();
        check("akurasi 100 kalau belum ada ketukan", isClose(empty.getAccuracy(), 100));
        check("WPM 0 kalau belum 1 detik", isClose(empty.getWpm(500), 0));

        TypingStats stats = new TypingStats();
        stats.recordKeystroke('a', 'a');   // benar
        stats.recordKeystroke('b', 'x');   // salah, seharusnya 'x'
        stats.recordKeystroke('c', 'c');   // benar
        stats.recordKeystroke('d', 'X');   // salah, seharusnya 'X' -> dicatat sebagai 'x'

        check("total ketukan = 4", stats.getTotalKeystrokes() == 4);
        check("ketukan benar = 2", stats.getCorrectKeystrokes() == 2);
        check("ketukan salah = 2", stats.getWrongKeystrokes() == 2);
        check("akurasi = 50.0", isClose(stats.getAccuracy(), 50.0));

        HashMap<Character, Integer> mistakes = stats.getMistakes();
        Integer xCount = mistakes.get('x');
        check("huruf 'x' salah 2 kali (huruf besar ikut dihitung)", xCount != null && xCount == 2);
        check("hanya 1 huruf di peta kesalahan", mistakes.size() == 1);

        // getMistakes() mengembalikan salinan: mengubahnya tidak boleh mengubah data asli.
        mistakes.clear();
        check("getMistakes() mengembalikan salinan", stats.getMistakes().size() == 1);

        // 10 ketukan benar dalam 1 menit = 10/5 = 2 kata per menit.
        TypingStats wpmStats = new TypingStats();
        for (int i = 0; i < 10; i++) {
            wpmStats.recordKeystroke('k', 'k');
        }
        check("WPM = 2.0 untuk 10 huruf benar dalam 60 detik", isClose(wpmStats.getWpm(60000), 2.0));
        check("WPM = 4.0 untuk 10 huruf benar dalam 30 detik", isClose(wpmStats.getWpm(30000), 4.0));
        System.out.println();
    }

    // ---------------------------------------------------------------
    // ScoreEntry
    // ---------------------------------------------------------------

    private static void testScoreEntry() {
        System.out.println("--- ScoreEntry ---");

        ScoreEntry original = new ScoreEntry("Budi", "Time Attack", 350, 72.5, 96.25, "2026-11-20 15:30");
        String line = original.toCsvLine();
        check("toCsvLine memakai titik untuk desimal: " + line,
                line.equals("Budi,Time Attack,350,72.50,96.25,2026-11-20 15:30"));

        ScoreEntry copy = ScoreEntry.fromCsvLine(line);
        boolean sameValues = copy.getName().equals("Budi")
                && copy.getMode().equals("Time Attack")
                && copy.getScore() == 350
                && isClose(copy.getWpm(), 72.5)
                && isClose(copy.getAccuracy(), 96.25)
                && copy.getDate().equals("2026-11-20 15:30");
        check("toCsvLine -> fromCsvLine menghasilkan nilai yang sama", sameValues);

        // Constructor kedua (dari TypingStats).
        TypingStats stats = new TypingStats();
        stats.recordKeystroke('a', 'a');
        ScoreEntry fromStats = new ScoreEntry("Sari", "Classic", 10, stats);
        check("constructor dari stats mengisi akurasi", isClose(fromStats.getAccuracy(), 100));
        check("constructor dari stats mengisi tanggal: " + fromStats.getDate(),
                fromStats.getDate().length() == ScoreEntry.DATE_PATTERN.length());

        testSorting();
        expectBadLine("Budi,Classic,100");                          // kolom kurang
        expectBadLine("Budi,Classic,banyak,50.0,90.0,2026-11-20");  // skor bukan angka
        expectBadLine("Budi,Classic,100,cepat,90.0,2026-11-20");    // WPM bukan angka
        System.out.println();
    }

    private static void testSorting() {
        ArrayList<ScoreEntry> list = new ArrayList<>();
        list.add(new ScoreEntry("A", "Classic", 100, 40.0, 90.0, "-"));
        list.add(new ScoreEntry("B", "Classic", 300, 50.0, 90.0, "-"));
        list.add(new ScoreEntry("C", "Classic", 200, 70.0, 90.0, "-"));
        list.add(new ScoreEntry("D", "Classic", 300, 60.0, 90.0, "-"));

        // Collections.sort memakai compareTo milik ScoreEntry.
        Collections.sort(list);

        String order = "";
        for (ScoreEntry entry : list) {
            order = order + entry.getName();
        }
        check("urutan setelah sort = DBCA (skor tinggi dulu, lalu WPM tinggi): " + order,
                order.equals("DBCA"));
    }

    // Membaca baris CSV yang rusak. Tes LULUS kalau IllegalArgumentException dilempar.
    private static void expectBadLine(String line) {
        try {
            ScoreEntry.fromCsvLine(line);
            check("baris rusak seharusnya ditolak: " + line, false);
        } catch (IllegalArgumentException e) {
            check("baris rusak ditolak -> " + e.getMessage(), true);
        }
    }

    // ---------------------------------------------------------------
    // WordBank
    // ---------------------------------------------------------------

    private static void testWordBank() {
        System.out.println("--- WordBank ---");

        WordBank bank = new WordBank();
        try {
            bank.loadWords(WordBank.WORDS_PATH);
            bank.loadSnippets(WordBank.SNIPPETS_PATH);
            check("memuat minimal 250 kata (" + bank.getWordCount() + ")", bank.getWordCount() >= 250);
            check("memuat minimal 12 potongan kode (" + bank.getSnippetCount() + ")",
                    bank.getSnippetCount() >= 12);

            String fiveWords = bank.randomWords(5);
            check("randomWords(5) berisi 5 kata: \"" + fiveWords + "\"", fiveWords.split(" ").length == 5);

            ArrayList<String> snippet = bank.randomSnippet();
            check("potongan kode berisi 4 sampai 8 baris (" + snippet.size() + ")",
                    snippet.size() >= 4 && snippet.size() <= 8);
        } catch (WordFileException e) {
            check("file kata bisa dimuat (error: " + e.getMessage() + ")", false);
        }

        // File yang tidak ada harus menghasilkan WordFileException, bukan crash.
        try {
            bank.loadWords("/resources/tidak_ada.txt");
            check("file yang tidak ada seharusnya ditolak", false);
        } catch (WordFileException e) {
            check("file yang tidak ada ditolak -> " + e.getMessage(), true);
        }
        System.out.println();
    }

    // ---------------------------------------------------------------
    // TimeAttackMode dan SurvivalMode (tanpa GUI, tanpa timer)
    // ---------------------------------------------------------------

    private static void testTimedModes() {
        System.out.println("--- Time Attack dan Survival ---");

        WordBank bank = new WordBank();
        try {
            bank.loadWords(WordBank.WORDS_PATH);
        } catch (WordFileException e) {
            check("file kata bisa dimuat untuk tes mode (error: " + e.getMessage() + ")", false);
            return;
        }

        // Time Attack: kata benar dihitung, kata salah tidak, waktu habis = selesai.
        TimeAttackMode timeAttack = new TimeAttackMode(bank);
        String firstWord = timeAttack.getCurrentTarget();
        check("Time Attack: kata benar diterima", timeAttack.submit(firstWord));
        check("Time Attack: kata salah ditolak", !timeAttack.submit("zzzzz"));
        check("Time Attack: 1 kata benar", timeAttack.getCorrectWords() == 1);
        check("Time Attack: ada kata berikutnya untuk ditampilkan",
                timeAttack.getUpcomingText().split(" ").length == 4);
        check("Time Attack: sisa waktu setelah 15 detik = 45 detik",
                timeAttack.getTimeLeftMillis(15000) == 45000);
        check("Time Attack: belum selesai sebelum waktu habis", !timeAttack.isOver());
        timeAttack.onTimeUp();
        check("Time Attack: selesai setelah onTimeUp()", timeAttack.isOver());

        // Survival: 3 nyawa, salah atau waktu habis = nyawa -1.
        SurvivalMode survival = new SurvivalMode(bank);
        check("Survival: mulai dengan 3 nyawa", survival.getLives() == 3);
        check("Survival: kata benar diterima", survival.submit(survival.getCurrentTarget()));
        check("Survival: batas waktu turun 0.1 detik setelah kata benar",
                survival.getTimeLimitMillis() == 4900);
        survival.submit("zzzzz");
        check("Survival: salah ketik = nyawa tinggal 2", survival.getLives() == 2);
        survival.onTimeUp();
        check("Survival: waktu habis = nyawa tinggal 1", survival.getLives() == 1);
        check("Survival: belum selesai dengan 1 nyawa", !survival.isOver());
        survival.onTimeUp();
        check("Survival: selesai saat nyawa 0", survival.isOver());
        check("Survival: skor = 1 kata x 15 + 0 huruf = 15", survival.calculateScore() == 15);
        System.out.println();
    }

    // ---------------------------------------------------------------
    // CodeMode (tanpa GUI)
    // ---------------------------------------------------------------

    private static void testCodeMode() {
        System.out.println("--- Code Mode ---");

        WordBank bank = new WordBank();
        try {
            bank.loadSnippets(WordBank.SNIPPETS_PATH);
        } catch (WordFileException e) {
            check("file potongan kode bisa dimuat (error: " + e.getMessage() + ")", false);
            return;
        }

        // Disimpan di variabel bertipe GameMode (upcasting), seperti di GamePanel.
        GameMode mode = new CodeMode(bank);
        check("Code: nama mode = " + GameMode.MODE_CODE, mode.getName().equals(GameMode.MODE_CODE));
        check("Code: CodeMode juga sebuah ClassicMode (multi-level inheritance)", mode instanceof ClassicMode);
        check("Code: Enter yang mengirim baris (isLineMode)", mode.isLineMode());

        CodeMode code = new CodeMode(bank);
        check("Code: maksimal 5 baris per ronde (" + code.getLineCount() + ")",
                code.getLineCount() >= 1 && code.getLineCount() <= CodeMode.LINES_PER_ROUND);

        // Kirim semua baris dengan benar. Setiap baris tidak boleh diawali spasi (indentasi dibuang).
        boolean allCorrect = true;
        boolean noIndent = true;
        int lineCount = code.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            String line = code.getCurrentTarget();
            if (line.isEmpty() || line.charAt(0) == ' ') {
                noIndent = false;
            }
            if (!code.submit(line)) {
                allCorrect = false;
            }
        }
        check("Code: tidak ada baris yang diawali spasi", noIndent);
        check("Code: semua baris yang benar diterima", allCorrect);
        check("Code: selesai setelah baris terakhir", code.isOver());

        CodeMode wrongCode = new CodeMode(bank);
        check("Code: baris yang salah ditolak", !wrongCode.submit("ini bukan kode"));
        check("Code: belum selesai setelah 1 baris", !wrongCode.isOver());

        // Belum ada ketukan: WPM 0, jadi skor Classic 0, dan 0 x 1.5 = 0.
        check("Code: skor 0 kalau belum mengetik", wrongCode.calculateScore() == 0);
        System.out.println();
    }

    // ---------------------------------------------------------------
    // LeaderboardStore (memakai file sementara, BUKAN leaderboard asli)
    // ---------------------------------------------------------------

    private static void testLeaderboardStore() {
        System.out.println("--- LeaderboardStore ---");

        // Folder sementara milik sistem (misalnya C:\Users\...\AppData\Local\Temp).
        File testFile = new File(System.getProperty("java.io.tmpdir"), "ketikin_selftest.csv");
        testFile.delete();   // sisa tes sebelumnya (kalau ada) dibuang dulu

        LeaderboardStore store = new LeaderboardStore(testFile);
        check("file belum ada -> leaderboard kosong, tanpa error",
                store.loadAll().isEmpty() && store.getLastErrorMessage() == null);

        store.addScore(new ScoreEntry("Andi", "Classic", 100, 40.0, 90.0, "2026-11-20 10:00"));
        store.addScore(new ScoreEntry("Budi", "Classic", 300, 50.0, 95.0, "2026-11-20 10:05"));
        store.addScore(new ScoreEntry("Cici", "Survival", 500, 60.0, 99.0, "2026-11-20 10:10"));
        boolean saved = store.addScore(new ScoreEntry("Andi", "Classic", 200, 45.0, 92.0, "2026-11-20 10:15"));
        check("addScore berhasil menyimpan", saved);
        check("4 skor tersimpan di file", store.loadAll().size() == 4);

        List<ScoreEntry> topClassic = store.getTop("Classic", 10);
        String order = "";
        for (ScoreEntry entry : topClassic) {
            order = order + entry.getScore() + " ";
        }
        check("getTop Classic hanya berisi Classic, urut skor tertinggi: " + order,
                order.equals("300 200 100 "));
        check("getTop membatasi jumlah baris (limit 2)", store.getTop("Classic", 2).size() == 2);
        check("getTop Survival berisi 1 skor", store.getTop("Survival", 10).size() == 1);
        check("rekor pribadi Andi di Classic = 200", store.getPersonalBest("Andi", "Classic") == 200);
        check("rekor pribadi Andi di Survival = -1 (belum pernah main)",
                store.getPersonalBest("Andi", "Survival") == -1);

        // Tambahkan baris rusak secara manual. Baris lain harus tetap terbaca.
        // FileWriter(file, true) = mode "append": menulis di akhir file, isi lama tidak dihapus.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, true))) {
            writer.write("ini,baris,rusak");
            writer.newLine();
            writer.write("Dodi,Classic,bukan_angka,50.0,90.0,2026-11-20 10:20");
            writer.newLine();
        } catch (IOException e) {
            check("menulis baris rusak untuk tes (error: " + e.getMessage() + ")", false);
        }
        // Peringatan dicetak ke System.err, jadi di beberapa terminal bisa muncul di urutan lain.
        System.out.println("(Dua peringatan \"baris ... dilewati\" dari tes ini memang disengaja.)");
        check("2 baris rusak dilewati, 4 skor lain tetap terbaca", store.loadAll().size() == 4);
        check("baris rusak bukan error untuk seluruh file", store.getLastErrorMessage() == null);

        testUnreadableLeaderboard();
        testFile.delete();
    }

    // "File" yang sebenarnya folder tidak bisa dibuka oleh FileReader -> IOException.
    // Tidak boleh crash: hasilnya daftar kosong + pesan error, dan skor baru tidak disimpan.
    private static void testUnreadableLeaderboard() {
        File folderAsFile = new File(System.getProperty("java.io.tmpdir"), "ketikin_selftest_folder.csv");
        folderAsFile.mkdir();

        LeaderboardStore store = new LeaderboardStore(folderAsFile);
        List<ScoreEntry> entries = store.loadAll();
        check("file tidak bisa dibaca -> daftar kosong + pesan error",
                entries.isEmpty() && store.getLastErrorMessage() != null);
        boolean saved = store.addScore(new ScoreEntry("Andi", "Classic", 100, 40.0, 90.0, "-"));
        check("file tidak bisa dibaca -> skor baru tidak disimpan (data lama aman)", !saved);

        folderAsFile.delete();
    }
}
