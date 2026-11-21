package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.ScoreEntry;

// LeaderboardStore: simpan dan baca skor di file CSV di folder home
// Owner: Satrio Ananda Widiyanto
// Materi: generics, Java IO, exception, collections
// [MATERI: Generics - T diganti ScoreEntry]
public class LeaderboardStore implements DataStore<ScoreEntry> {

    public static final String FOLDER_NAME = ".ketikin";
    public static final String FILE_NAME = "leaderboard.csv";
    // kolom mode isinya "Normal" atau satu huruf
    public static final String CSV_HEADER = "name,mode,score,wpm,accuracy,date";
    public static final String FILTER_ALL = "Semua";

    private File file;
    // null kalau tidak ada error
    private String lastErrorMessage;

    // file disimpan di luar jar karena jar tidak bisa ditulis
    public LeaderboardStore() {
        File folder = new File(System.getProperty("user.home"), FOLDER_NAME);
        file = new File(folder, FILE_NAME);
        lastErrorMessage = null;
    }

    // [MATERI: Polymorphism - compile-time]
    // dipakai SelfTest biar leaderboard asli tidak berubah
    public LeaderboardStore(File file) {
        this.file = file;
        lastErrorMessage = null;
    }

    // [MATERI: Java IO]
    @Override
    public List<ScoreEntry> loadAll() {
        lastErrorMessage = null;
        ArrayList<ScoreEntry> entries = new ArrayList<>();

        // belum ada skor, bukan error
        if (!file.exists()) {
            return entries;
        }

        // [MATERI: Exception - try-with-resources]
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // lewati header dan baris kosong
                boolean skipLine = line.trim().equals(CSV_HEADER) || line.trim().isEmpty();
                if (!skipLine) {
                    addEntryFromLine(entries, line, lineNumber);
                }
            }
        } catch (IOException e) {
            // jangan crash, mulai kosong dan simpan pesan untuk UI
            lastErrorMessage = "File leaderboard tidak bisa dibaca:\n" + e.getMessage();
            return new ArrayList<>();
        }
        return entries;
    }

    // try/catch per baris, jadi satu baris rusak tidak merusak yang lain
    private void addEntryFromLine(ArrayList<ScoreEntry> entries, String line, int lineNumber) {
        try {
            entries.add(ScoreEntry.fromCsvLine(line));
        } catch (IllegalArgumentException e) {
            System.err.println("Peringatan: baris " + lineNumber + " di " + file.getName()
                    + " dilewati. " + e.getMessage());
        }
    }

    // [MATERI: Java IO]
    @Override
    public boolean saveAll(List<ScoreEntry> items) {
        // bikin folder .ketikin kalau belum ada
        File folder = file.getParentFile();
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSV_HEADER);
            writer.newLine();
            for (ScoreEntry entry : items) {
                writer.write(entry.toCsvLine());
                writer.newLine();
            }
            lastErrorMessage = null;
            return true;
        } catch (IOException e) {
            lastErrorMessage = "Skor tidak bisa disimpan:\n" + e.getMessage();
            return false;
        }
    }

    public boolean addScore(ScoreEntry entry) {
        List<ScoreEntry> entries = loadAll();
        // file lama gagal dibaca, jangan disimpan biar skor lama tidak tertimpa
        if (lastErrorMessage != null) {
            lastErrorMessage = lastErrorMessage + "\nSkor baru tidak disimpan supaya data lama tidak hilang.";
            return false;
        }
        entries.add(entry);
        return saveAll(entries);
    }

    // focus bisa "Semua", "Normal", atau satu huruf
    public List<ScoreEntry> getTop(String focus, int limit) {
        List<ScoreEntry> allEntries = loadAll();

        // ambil yang cocok dengan filter, baris mode lama cuma muncul di "Semua"
        ArrayList<ScoreEntry> matchingEntries = new ArrayList<>();
        for (ScoreEntry entry : allEntries) {
            if (focus.equals(FILTER_ALL) || entry.getMode().equals(focus)) {
                matchingEntries.add(entry);
            }
        }

        // [MATERI: Collections - sort]
        Collections.sort(matchingEntries);

        // ambil paling banyak limit baris
        ArrayList<ScoreEntry> top = new ArrayList<>();
        for (int i = 0; i < matchingEntries.size() && i < limit; i++) {
            top.add(matchingEntries.get(i));
        }
        return top;
    }

    // -1 kalau belum pernah main dengan fokus itu
    public int getPersonalBest(String playerName, String focus) {
        int best = -1;
        for (ScoreEntry entry : loadAll()) {
            boolean samePlayer = entry.getName().equals(playerName);
            boolean sameFocus = entry.getMode().equals(focus);
            if (samePlayer && sameFocus && entry.getScore() > best) {
                best = entry.getScore();
            }
        }
        return best;
    }

    public String getFilePath() {
        return file.getPath();
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
}
