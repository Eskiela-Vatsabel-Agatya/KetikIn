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
import mode.GameMode;
import mode.LetterFocusMode;
import model.Player;
import model.ScoreEntry;
import model.TypingStats;

/*
 * SelfTest: program kecil untuk mengetes bagian non-GUI (Player, TypingStats, ScoreEntry,
 * WordBank, ClassicMode, LetterFocusMode, LeaderboardStore) tanpa JUnit. Mencetak PASS/FAIL untuk setiap pengecekan, lalu jumlahnya.
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
        testModes();
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

        ScoreEntry original = new ScoreEntry("Budi", "J", 350, 72.5, 96.25, "2026-11-20 15:30");
        String line = original.toCsvLine();
        check("toCsvLine memakai titik untuk desimal: " + line,
                line.equals("Budi,J,350,72.50,96.25,2026-11-20 15:30"));

        ScoreEntry copy = ScoreEntry.fromCsvLine(line);
        boolean sameValues = copy.getName().equals("Budi")
                && copy.getMode().equals("J")
                && copy.getScore() == 350
                && isClose(copy.getWpm(), 72.5)
                && isClose(copy.getAccuracy(), 96.25)
                && copy.getDate().equals("2026-11-20 15:30");
        check("toCsvLine -> fromCsvLine menghasilkan nilai yang sama", sameValues);

        // Constructor kedua (dari TypingStats).
        TypingStats stats = new TypingStats();
        stats.recordKeystroke('a', 'a');
        ScoreEntry fromStats = new ScoreEntry("Sari", "Normal", 10, stats);
        check("constructor dari stats mengisi akurasi", isClose(fromStats.getAccuracy(), 100));
        check("constructor dari stats mengisi tanggal: " + fromStats.getDate(),
                fromStats.getDate().length() == ScoreEntry.DATE_PATTERN.length());

        testSorting();
        expectBadLine("Budi,Normal,100");                          // kolom kurang
        expectBadLine("Budi,Normal,banyak,50.0,90.0,2026-11-20");  // skor bukan angka
        expectBadLine("Budi,Normal,100,cepat,90.0,2026-11-20");    // WPM bukan angka
        System.out.println();
    }

    private static void testSorting() {
        ArrayList<ScoreEntry> list = new ArrayList<>();
        list.add(new ScoreEntry("A", "Normal", 100, 40.0, 90.0, "-"));
        list.add(new ScoreEntry("B", "Normal", 300, 50.0, 90.0, "-"));
        list.add(new ScoreEntry("C", "Normal", 200, 70.0, 90.0, "-"));
        list.add(new ScoreEntry("D", "Normal", 300, 60.0, 90.0, "-"));

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
            check("memuat minimal 1500 kata (" + bank.getWordCount() + ")", bank.getWordCount() >= 1500);

            String fiveWords = bank.randomWords(5);
            check("randomWords(5) berisi 5 kata: \"" + fiveWords + "\"", fiveWords.split(" ").length == 5);

            // Setiap huruf a-z harus ada di minimal 40 kata, supaya latihan huruf apa pun
            // (juga j, q, x, z) punya cukup banyak kata yang berbeda.
            String fewLetters = "";
            for (char c = 'a'; c <= 'z'; c++) {
                int count = bank.wordsContaining(c).size();
                if (count < 40) {
                    fewLetters = fewLetters + c + "(" + count + ") ";
                }
            }
            check("setiap huruf a-z ada di minimal 40 kata " + fewLetters, fewLetters.isEmpty());
            check("wordsContaining('q') hanya berisi kata dengan q",
                    allContain(bank.wordsContaining('q'), 'q'));
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

    // true kalau setiap kata di daftar berisi huruf letter.
    private static boolean allContain(List<String> words, char letter) {
        for (String word : words) {
            if (word.indexOf(letter) < 0) {
                return false;
            }
        }
        return true;
    }

    // Berapa kata di dalam teks (dipisah spasi) yang berisi huruf letter.
    private static int countWordsWith(String text, char letter) {
        int count = 0;
        for (String word : text.split(" ")) {
            if (word.indexOf(letter) >= 0) {
                count++;
            }
        }
        return count;
    }

    // ---------------------------------------------------------------
    // ClassicMode dan LetterFocusMode (tanpa GUI, tanpa timer)
    // ---------------------------------------------------------------

    private static void testModes() {
        System.out.println("--- ClassicMode dan LetterFocusMode ---");

        WordBank bank = new WordBank();
        try {
            bank.loadWords(WordBank.WORDS_PATH);
        } catch (WordFileException e) {
            check("file kata bisa dimuat untuk tes mode (error: " + e.getMessage() + ")", false);
            return;
        }

        // Disimpan di variabel bertipe GameMode (upcasting), seperti di GamePanel.
        GameMode normal = new ClassicMode(bank);
        check("Normal: 30 kata", normal.getCurrentTarget().split(" ").length == ClassicMode.WORD_COUNT);
        check("Normal: tidak ada huruf fokus", normal.getFocusLetter() == GameMode.NO_FOCUS);
        check("Normal: label fokus = Normal", normal.getFocusLabel().equals(GameMode.FOCUS_NORMAL));
        check("Normal: belum selesai sebelum submit", !normal.isOver());
        check("Normal: teks yang benar diterima", normal.submit(normal.getCurrentTarget()));
        check("Normal: selesai setelah submit", normal.isOver());

        GameMode focusJ = new LetterFocusMode(bank, 'J');
        String target = focusJ.getCurrentTarget();
        check("Fokus J: nama = Fokus huruf J", focusJ.getName().equals("Fokus huruf J"));
        check("Fokus J: LetterFocusMode juga sebuah ClassicMode (multi-level inheritance)",
                focusJ instanceof ClassicMode);
        check("Fokus J: getFocusLetter() = 'j' (huruf kecil)", focusJ.getFocusLetter() == 'j');
        check("Fokus J: label fokus = J", focusJ.getFocusLabel().equals("J"));
        check("Fokus J: 30 kata", target.split(" ").length == ClassicMode.WORD_COUNT);
        int withJ = countWordsWith(target, 'j');
        check("Fokus J: minimal 24 kata berisi j (" + withJ + ")", withJ >= LetterFocusMode.FOCUS_WORD_COUNT);

        // Semua huruf A-Z (termasuk Q, X, Z) bisa dipakai tanpa error.
        String badLetters = "";
        for (char c = 'A'; c <= 'Z'; c++) {
            GameMode mode = new LetterFocusMode(bank, c);
            char lower = Character.toLowerCase(c);
            if (countWordsWith(mode.getCurrentTarget(), lower) < LetterFocusMode.FOCUS_WORD_COUNT) {
                badLetters = badLetters + c;
            }
        }
        check("Fokus A-Z: setiap huruf memberi minimal 24 kata berisi huruf itu " + badLetters,
                badLetters.isEmpty());
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

        store.addScore(new ScoreEntry("Andi", "Normal", 100, 40.0, 90.0, "2026-11-20 10:00"));
        store.addScore(new ScoreEntry("Budi", "Normal", 300, 50.0, 95.0, "2026-11-20 10:05"));
        store.addScore(new ScoreEntry("Cici", "J", 500, 60.0, 99.0, "2026-11-20 10:10"));
        // Baris lama dari mode yang sudah dihapus: hanya boleh muncul di filter "Semua".
        store.addScore(new ScoreEntry("Dodi", "Survival", 50, 30.0, 80.0, "2026-11-19 09:00"));
        boolean saved = store.addScore(new ScoreEntry("Andi", "Normal", 200, 45.0, 92.0, "2026-11-20 10:15"));
        check("addScore berhasil menyimpan", saved);
        check("5 skor tersimpan di file", store.loadAll().size() == 5);

        List<ScoreEntry> topNormal = store.getTop("Normal", 10);
        String order = "";
        for (ScoreEntry entry : topNormal) {
            order = order + entry.getScore() + " ";
        }
        check("getTop Normal hanya berisi Normal, urut skor tertinggi: " + order,
                order.equals("300 200 100 "));
        check("getTop membatasi jumlah baris (limit 2)", store.getTop("Normal", 2).size() == 2);
        check("getTop J berisi 1 skor", store.getTop("J", 10).size() == 1);
        check("getTop Q kosong", store.getTop("Q", 10).isEmpty());
        check("getTop Semua berisi 5 skor (juga baris lama Survival)",
                store.getTop(LeaderboardStore.FILTER_ALL, 10).size() == 5);
        check("rekor pribadi Andi di Normal = 200", store.getPersonalBest("Andi", "Normal") == 200);
        check("rekor pribadi Andi di J = -1 (belum pernah main)",
                store.getPersonalBest("Andi", "J") == -1);

        // Tambahkan baris rusak secara manual. Baris lain harus tetap terbaca.
        // FileWriter(file, true) = mode "append": menulis di akhir file, isi lama tidak dihapus.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, true))) {
            writer.write("ini,baris,rusak");
            writer.newLine();
            writer.write("Dodi,Normal,bukan_angka,50.0,90.0,2026-11-20 10:20");
            writer.newLine();
        } catch (IOException e) {
            check("menulis baris rusak untuk tes (error: " + e.getMessage() + ")", false);
        }
        // Peringatan dicetak ke System.err, jadi di beberapa terminal bisa muncul di urutan lain.
        System.out.println("(Dua peringatan \"baris ... dilewati\" dari tes ini memang disengaja.)");
        check("2 baris rusak dilewati, 5 skor lain tetap terbaca", store.loadAll().size() == 5);
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
        boolean saved = store.addScore(new ScoreEntry("Andi", "Normal", 100, 40.0, 90.0, "-"));
        check("file tidak bisa dibaca -> skor baru tidak disimpan (data lama aman)", !saved);

        folderAsFile.delete();
    }
}
