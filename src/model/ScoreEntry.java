package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// ScoreEntry: satu baris leaderboard, bisa ke CSV dan diurutkan
// Owner: Hanif Rafa Ramidzar
// Materi: polymorphism, generics, string, exception
// [MATERI: Generics - Comparable<ScoreEntry>]
public class ScoreEntry implements Comparable<ScoreEntry> {

    // name,mode,score,wpm,accuracy,date
    public static final int COLUMN_COUNT = 6;
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    private String name;
    // isinya fokus ("Normal" atau huruf), namanya ikut kolom CSV
    private String mode;
    private int score;
    private double wpm;
    private double accuracy;
    private String date;

    // [MATERI: Polymorphism - compile-time]
    // dipakai waktu baca CSV
    public ScoreEntry(String name, String mode, int score, double wpm, double accuracy, String date) {
        this.name = name;
        this.mode = mode;
        this.score = score;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.date = date;
    }

    // dipakai setelah main, tanggalnya waktu sekarang
    public ScoreEntry(String playerName, String modeName, int score, TypingStats stats) {
        this.name = playerName;
        this.mode = modeName;
        this.score = score;
        this.wpm = stats.getWpm();
        this.accuracy = stats.getAccuracy();

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        this.date = formatter.format(new Date());
    }

    // skor tertinggi di depan, kalau sama WPM tertinggi di depan
    @Override
    public int compareTo(ScoreEntry other) {
        if (this.score > other.score) {
            return -1;
        }
        if (this.score < other.score) {
            return 1;
        }
        if (this.wpm > other.wpm) {
            return -1;
        }
        if (this.wpm < other.wpm) {
            return 1;
        }
        return 0;
    }

    // pakai Locale.US biar desimal pakai titik, koma bisa merusak CSV
    public String toCsvLine() {
        return String.format(Locale.US, "%s,%s,%d,%.2f,%.2f,%s", name, mode, score, wpm, accuracy, date);
    }

    // baris rusak dilempar sebagai IllegalArgumentException
    public static ScoreEntry fromCsvLine(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Baris kosong.");
        }

        // [MATERI: String - split]
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
            throw new IllegalArgumentException("Angka tidak valid: " + line);
        }

        return new ScoreEntry(name, mode, score, wpm, accuracy, date);
    }

    // tidak ada setter, skor yang sudah tercatat tidak boleh diubah
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
