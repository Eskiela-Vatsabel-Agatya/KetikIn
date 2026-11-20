package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * ScoreEntry: satu baris di leaderboard (nama, fokus, skor, WPM, akurasi, tanggal).
 * Bisa diubah ke baris CSV dan dibaca kembali dari baris CSV, dan bisa diurutkan
 * (skor tertinggi dulu) karena mengimplementasikan Comparable.
 * Owner: Person C
 * Materi: Constructor overloading (Polymorphism compile-time), Generics (Comparable<ScoreEntry>),
 *         String (split, trim, String.format), Exception (IllegalArgumentException)
 */
// [MATERI: Generics - Comparable<ScoreEntry>]
public class ScoreEntry implements Comparable<ScoreEntry> {

    // Jumlah kolom di file CSV: name,mode,score,wpm,accuracy,date
    public static final int COLUMN_COUNT = 6;
    // Format tanggal, contoh: 2026-11-20 15:30
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    private String name;
    // Fokus permainan: "Normal" atau satu huruf, misalnya "J". Namanya tetap "mode"
    // karena kolom CSV-nya bernama "mode".
    private String mode;
    private int score;
    private double wpm;
    private double accuracy;
    private String date;

    // [MATERI: Polymorphism - compile-time]
    // Dua constructor dengan nama sama tapi parameter berbeda = overloading.
    // Java memilih constructor yang cocok saat compile, berdasarkan argumen yang diberikan.

    // Constructor 1: semua nilai diisi langsung (dipakai saat membaca file CSV).
    public ScoreEntry(String name, String mode, int score, double wpm, double accuracy, String date) {
        this.name = name;
        this.mode = mode;
        this.score = score;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.date = date;
    }

    // Constructor 2: dibuat dari hasil permainan. WPM dan akurasi diambil dari stats,
    // tanggal diambil dari waktu sekarang.
    public ScoreEntry(String playerName, String modeName, int score, TypingStats stats) {
        this.name = playerName;
        this.mode = modeName;
        this.score = score;
        this.wpm = stats.getWpm();
        this.accuracy = stats.getAccuracy();

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        this.date = formatter.format(new Date());
    }

    // Menentukan urutan saat Collections.sort dipanggil.
    // Hasil negatif = "this" ditaruh di depan "other", positif = di belakang, 0 = sama.
    // Kita ingin skor TERTINGGI di depan; kalau skornya sama, WPM tertinggi di depan.
    @Override
    public int compareTo(ScoreEntry other) {
        if (this.score > other.score) {
            return -1;
        }
        if (this.score < other.score) {
            return 1;
        }
        // Skor sama: bandingkan WPM.
        if (this.wpm > other.wpm) {
            return -1;
        }
        if (this.wpm < other.wpm) {
            return 1;
        }
        return 0;
    }

    // Mengubah objek ini menjadi satu baris CSV, contoh:
    // Budi,J,512,64.30,97.50,2026-11-20 15:30
    // Locale.US dipakai supaya desimal selalu memakai TITIK (64.30). Di komputer dengan
    // setelan Indonesia, String.format biasa bisa menulis KOMA (64,30), dan itu akan
    // merusak CSV karena koma adalah pemisah kolom.
    public String toCsvLine() {
        return String.format(Locale.US, "%s,%s,%d,%.2f,%.2f,%s", name, mode, score, wpm, accuracy, date);
    }

    // Kebalikan dari toCsvLine: membaca satu baris CSV menjadi ScoreEntry.
    // Kalau barisnya rusak, lempar IllegalArgumentException (unchecked) dengan pesan yang jelas.
    public static ScoreEntry fromCsvLine(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Baris kosong.");
        }

        // [MATERI: String - split] pecah baris menjadi kolom-kolom di setiap koma.
        String[] parts = line.split(",");
        if (parts.length != COLUMN_COUNT) {
            throw new IllegalArgumentException(
                    "Harus ada " + COLUMN_COUNT + " kolom, tapi ada " + parts.length + ": " + line);
        }

        String name = parts[0].trim();
        String mode = parts[1].trim();
        String date = parts[5].trim();

        int score;
        double wpm;
        double accuracy;
        try {
            score = Integer.parseInt(parts[2].trim());
            wpm = Double.parseDouble(parts[3].trim());
            accuracy = Double.parseDouble(parts[4].trim());
        } catch (NumberFormatException e) {
            // Ubah menjadi pesan yang menjelaskan baris mana yang rusak.
            throw new IllegalArgumentException("Angka tidak valid: " + line);
        }

        return new ScoreEntry(name, mode, score, wpm, accuracy, date);
    }

    // Hanya getter, tidak ada setter: skor yang sudah tercatat tidak boleh diubah.
    public String getName() {
        return name;
    }

    public String getMode() {
        return mode;
    }

    public int getScore() {
        return score;
    }

    public double getWpm() {
        return wpm;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public String getDate() {
        return date;
    }
}
