package mode;

import data.WordBank;
import model.TypingStats;

/*
 * GameMode: kelas induk (abstract) untuk semua mode permainan. Isinya hal-hal yang
 * dimiliki semua mode (nama, bank kata, statistik) dan daftar method yang WAJIB
 * dibuat oleh setiap mode.
 * Kenapa abstract? "Mode permainan" saja belum punya aturan; aturannya baru ada di mode
 * yang konkret (ClassicMode, LetterFocusMode). Jadi GameMode tidak boleh dibuat objeknya langsung.
 * Owner: Person A
 * Materi: Abstraction (abstract class), Inheritance (kelas induk), Encapsulation (protected),
 *         Polymorphism runtime (method yang di-override oleh kelas anak)
 */
// [MATERI: Abstraction - abstract class]
public abstract class GameMode {

    // Nilai getFocusLetter() kalau tidak ada huruf fokus (mode normal).
    public static final char NO_FOCUS = ' ';
    // Isi kolom "mode" di CSV (dan kolom Fokus di Leaderboard) untuk mode normal.
    // Kalau ada huruf fokus, isinya huruf itu sendiri, misalnya "J".
    // (Materi kuliah tidak memakai enum, jadi kita pakai konstanta String.)
    public static final String FOCUS_NORMAL = "Normal";

    // protected = bisa dipakai langsung oleh kelas anak (ClassicMode, LetterFocusMode).
    protected String name;
    protected WordBank wordBank;
    protected TypingStats stats;

    public GameMode(String name, WordBank wordBank) {
        this.name = name;
        this.wordBank = wordBank;
        this.stats = new TypingStats();
    }

    // ---------- Method abstract: WAJIB dibuat oleh setiap mode ----------

    public abstract String getDescription();

    // Teks yang harus diketik pemain sekarang.
    public abstract String getCurrentTarget();

    // Pemain selesai mengetik teksnya. true kalau semua huruf benar.
    public abstract boolean submit(String typed);

    public abstract boolean isOver();

    public abstract int calculateScore();

    // ---------- Method biasa (concrete): sama untuk semua mode ----------

    public String getName() {
        return name;
    }

    public TypingStats getStats() {
        return stats;
    }

    // ---------- Method biasa yang BOLEH di-override ----------
    // Isinya nilai untuk mode normal (tanpa huruf fokus).
    // [MATERI: Polymorphism - runtime] LetterFocusMode meng-override kedua method ini.
    // GamePanel cukup memanggil mode.getFocusLetter(); Java otomatis menjalankan versi milik
    // objek aslinya. Jadi GamePanel tidak perlu mengecek "mode ini kelas apa?" (instanceof).

    // Huruf yang sedang dilatih (huruf kecil), atau NO_FOCUS kalau tidak ada.
    public char getFocusLetter() {
        return NO_FOCUS;
    }

    // Nilai yang disimpan di CSV dan ditampilkan di kolom Fokus: "Normal" atau huruf besar, misalnya "J".
    public String getFocusLabel() {
        return FOCUS_NORMAL;
    }
}
