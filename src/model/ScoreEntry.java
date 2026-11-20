package model;

/*
 * ScoreEntry: satu baris di leaderboard (nama, mode, skor, WPM, akurasi, tanggal).
 * Bisa diubah ke baris CSV dan dibaca kembali dari baris CSV, dan bisa diurutkan
 * (skor tertinggi dulu) karena mengimplementasikan Comparable.
 * Owner: Person C
 * Materi: Constructor overloading (Polymorphism compile-time), Generics (Comparable<ScoreEntry>),
 *         String (split, String.format), Exception (IllegalArgumentException)
 */
// [MATERI: Generics - Comparable<ScoreEntry>]
public class ScoreEntry implements Comparable<ScoreEntry> {

    private String name;
    private String mode;
    private int score;
    private double wpm;
    private double accuracy;
    private String date;

    // [MATERI: Polymorphism - compile-time] constructor 1: semua nilai diisi langsung (dipakai saat baca file).
    public ScoreEntry(String name, String mode, int score, double wpm, double accuracy, String date) {
        this.name = name;
        this.mode = mode;
        this.score = score;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.date = date;
    }

    // [MATERI: Polymorphism - compile-time] constructor 2: dibuat dari hasil permainan.
    public ScoreEntry(String playerName, String modeName, int score, TypingStats stats) {
        this.name = playerName;
        this.mode = modeName;
        this.score = score;
        // TODO Phase 2: isi wpm dan accuracy dari stats, date dari waktu sekarang.
        this.date = "";
    }

    @Override
    public int compareTo(ScoreEntry other) {
        // TODO Phase 2: skor lebih tinggi di depan; kalau sama, WPM lebih tinggi di depan.
        return 0;
    }

    public String toCsvLine() {
        // TODO Phase 2
        return "";
    }

    public static ScoreEntry fromCsvLine(String line) {
        // TODO Phase 2: split(","), cek jumlah kolom dan angka, lempar IllegalArgumentException kalau rusak.
        throw new IllegalArgumentException("Belum dibuat (Phase 2)");
    }

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
