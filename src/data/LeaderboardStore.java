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

/*
 * LeaderboardStore: menyimpan dan membaca skor pemain di file CSV
 * <user home>\.ketikin\leaderboard.csv.
 * Kenapa tidak di dalam jar? Jar itu seperti file zip yang hanya dibaca; program tidak bisa
 * menulis ke dalamnya. Folder home milik pengguna (misalnya C:\Users\Budi) selalu boleh ditulis.
 * Penyimpanan dilakukan langsung (tanpa thread tambahan), karena filenya sangat kecil.
 * Owner: Person B
 * Materi: Generics (implements DataStore<ScoreEntry>), Java IO (BufferedReader/BufferedWriter, File),
 *         Exception (try-with-resources, try/catch per baris), Collections (ArrayList, Collections.sort)
 */
// [MATERI: Generics - T diganti ScoreEntry]
// DataStore<T> ditulis dengan T. Di sini kita isi T = ScoreEntry, jadi method-nya menjadi
// List<ScoreEntry> loadAll() dan boolean saveAll(List<ScoreEntry> items).
public class LeaderboardStore implements DataStore<ScoreEntry> {

    public static final String FOLDER_NAME = ".ketikin";
    public static final String FILE_NAME = "leaderboard.csv";
    // Kolom "mode" berisi fokus permainan: "Normal" atau satu huruf, misalnya "J".
    public static final String CSV_HEADER = "name,mode,score,wpm,accuracy,date";
    // Pilihan filter di Leaderboard yang menampilkan skor dari semua fokus.
    public static final String FILTER_ALL = "Semua";

    private File file;
    // Pesan error terakhir (null kalau tidak ada error), supaya UI bisa menampilkannya.
    private String lastErrorMessage;

    // Constructor 1: file asli di folder home pengguna. Dipakai oleh aplikasi.
    public LeaderboardStore() {
        // System.getProperty("user.home") = folder home pengguna, misalnya C:\Users\Budi.
        // new File(folder, nama) menggabungkan folder dan nama file dengan pemisah yang benar.
        File folder = new File(System.getProperty("user.home"), FOLDER_NAME);
        file = new File(folder, FILE_NAME);
        lastErrorMessage = null;
    }

    // Constructor 2: file lain yang ditentukan sendiri. Dipakai oleh SelfTest,
    // supaya tes tidak mengubah leaderboard asli milik pemain.
    // [MATERI: Polymorphism - compile-time] constructor overloading.
    public LeaderboardStore(File file) {
        this.file = file;
        lastErrorMessage = null;
    }

    // [MATERI: Java IO] membaca file CSV baris demi baris dengan BufferedReader.
    @Override
    public List<ScoreEntry> loadAll() {
        lastErrorMessage = null;
        ArrayList<ScoreEntry> entries = new ArrayList<>();

        // Belum pernah ada skor yang disimpan: bukan error, leaderboard masih kosong.
        if (!file.exists()) {
            return entries;
        }

        // [MATERI: Exception - try-with-resources] reader otomatis ditutup setelah blok try.
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // Baris judul kolom dan baris kosong dilewati.
                boolean skipLine = line.trim().equals(CSV_HEADER) || line.trim().isEmpty();
                if (!skipLine) {
                    addEntryFromLine(entries, line, lineNumber);
                }
            }
        } catch (IOException e) {
            // Seluruh file tidak bisa dibaca (misalnya sedang dikunci program lain).
            // Jangan crash: mulai dengan leaderboard kosong, dan simpan pesannya untuk UI.
            lastErrorMessage = "File leaderboard tidak bisa dibaca:\n" + e.getMessage();
            return new ArrayList<>();
        }
        return entries;
    }

    // Mengubah satu baris CSV menjadi ScoreEntry dan menambahkannya ke daftar.
    // try/catch ada DI DALAM loop (per baris): kalau satu baris rusak, hanya baris itu yang
    // dilewati, dan baris-baris lain tetap dibaca.
    private void addEntryFromLine(ArrayList<ScoreEntry> entries, String line, int lineNumber) {
        try {
            entries.add(ScoreEntry.fromCsvLine(line));
        } catch (IllegalArgumentException e) {
            // Baris rusak (misalnya diedit manual). Cukup diberi peringatan di console.
            System.err.println("Peringatan: baris " + lineNumber + " di " + file.getName()
                    + " dilewati. " + e.getMessage());
        }
    }

    // [MATERI: Java IO] menulis ulang seluruh file dengan BufferedWriter.
    @Override
    public boolean saveAll(List<ScoreEntry> items) {
        // Buat folder .ketikin kalau belum ada. mkdirs() juga membuat folder induk yang belum ada.
        // Kalau gagal dibuat, FileWriter di bawah akan melempar IOException, jadi tetap tertangani.
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
            // Gagal menulis (misalnya disk penuh atau tidak ada izin). Beri tahu pemanggil.
            lastErrorMessage = "Skor tidak bisa disimpan:\n" + e.getMessage();
            return false;
        }
    }

    // Membaca semua skor, menambah satu skor baru, lalu menyimpan semuanya lagi.
    // Mengembalikan true kalau berhasil disimpan.
    public boolean addScore(ScoreEntry entry) {
        List<ScoreEntry> entries = loadAll();
        // File lama ada tapi tidak bisa dibaca: JANGAN menyimpan. Kalau disimpan,
        // file lama akan tertimpa dan semua skor lama hilang.
        if (lastErrorMessage != null) {
            lastErrorMessage = lastErrorMessage + "\nSkor baru tidak disimpan supaya data lama tidak hilang.";
            return false;
        }
        entries.add(entry);
        return saveAll(entries);
    }

    // Skor terbaik untuk satu pilihan filter, sudah diurutkan, maksimal 'limit' baris.
    // focus = FILTER_ALL ("Semua"), "Normal", atau satu huruf seperti "J".
    public List<ScoreEntry> getTop(String focus, int limit) {
        List<ScoreEntry> allEntries = loadAll();

        // Langkah 1: ambil hanya skor yang cocok dengan filter. "Semua" mengambil semuanya.
        // Baris lama dari mode yang sudah dihapus (misalnya "Survival") tidak cocok dengan
        // "Normal" atau huruf apa pun, jadi hanya muncul di "Semua".
        ArrayList<ScoreEntry> matchingEntries = new ArrayList<>();
        for (ScoreEntry entry : allEntries) {
            if (focus.equals(FILTER_ALL) || entry.getMode().equals(focus)) {
                matchingEntries.add(entry);
            }
        }

        // Langkah 2: urutkan. Collections.sort memakai compareTo milik ScoreEntry
        // (skor tertinggi dulu; kalau sama, WPM tertinggi dulu).
        Collections.sort(matchingEntries);

        // Langkah 3: ambil paling banyak 'limit' skor pertama.
        ArrayList<ScoreEntry> top = new ArrayList<>();
        for (int i = 0; i < matchingEntries.size() && i < limit; i++) {
            top.add(matchingEntries.get(i));
        }
        return top;
    }

    // Skor terbaik seorang pemain untuk satu fokus ("Normal" atau huruf), atau -1 kalau
    // ia belum pernah main dengan fokus itu. Jadi rekor J hanya dibandingkan dengan rekor J.
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

    // Lokasi file, untuk ditampilkan di layar Leaderboard.
    public String getFilePath() {
        return file.getPath();
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
}
