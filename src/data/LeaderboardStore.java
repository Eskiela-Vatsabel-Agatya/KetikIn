package data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import model.ScoreEntry;

/*
 * LeaderboardStore: menyimpan dan membaca skor pemain di file CSV
 * <user home>\.ketikin\leaderboard.csv (di luar jar, karena isi jar tidak bisa diubah).
 * Owner: Person B
 * Materi: Generics (implements DataStore<ScoreEntry>), Java IO (BufferedReader/BufferedWriter, File),
 *         Exception (try-with-resources), Collections (ArrayList, Collections.sort)
 */
// [MATERI: Generics - T diganti ScoreEntry]
public class LeaderboardStore implements DataStore<ScoreEntry> {

    public static final String FOLDER_NAME = ".ketikin";
    public static final String FILE_NAME = "leaderboard.csv";
    public static final String CSV_HEADER = "name,mode,score,wpm,accuracy,date";

    private File file;
    // Pesan error terakhir (null kalau tidak ada error), supaya UI bisa menampilkannya.
    private String lastErrorMessage;

    public LeaderboardStore() {
        // TODO Phase 5: bangun path dari System.getProperty("user.home"), buat folder kalau belum ada.
    }

    @Override
    public List<ScoreEntry> loadAll() {
        // TODO Phase 5
        return new ArrayList<>();
    }

    @Override
    public void saveAll(List<ScoreEntry> items) {
        // TODO Phase 5
    }

    // Membaca semua skor, menambah satu skor baru, lalu menyimpan semuanya lagi.
    public void addScore(ScoreEntry entry) {
        // TODO Phase 5
    }

    // Skor terbaik untuk satu mode, sudah diurutkan, maksimal 'limit' baris.
    public List<ScoreEntry> getTop(String modeName, int limit) {
        // TODO Phase 5
        return new ArrayList<>();
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
}
