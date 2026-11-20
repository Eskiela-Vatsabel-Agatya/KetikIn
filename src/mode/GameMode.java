package mode;

import data.WordBank;
import model.TypingStats;

/*
 * GameMode: kelas induk (abstract) untuk semua mode permainan. Isinya hal-hal yang
 * dimiliki semua mode (nama, bank kata, statistik) dan daftar method yang WAJIB
 * dibuat oleh setiap mode.
 * Owner: Person A
 * Materi: Abstraction (abstract class), Inheritance (kelas induk), Encapsulation (protected)
 */
// [MATERI: Abstraction - abstract class]
public abstract class GameMode {

    // Nama-nama mode. Dipakai juga sebagai nama tab di Leaderboard dan isi kolom "mode" di CSV.
    // (Materi kuliah tidak memakai enum, jadi kita pakai konstanta String.)
    public static final String MODE_CLASSIC = "Classic";
    public static final String MODE_TIME_ATTACK = "Time Attack";
    public static final String MODE_SURVIVAL = "Survival";
    public static final String MODE_CODE = "Code";

    // protected = bisa dipakai langsung oleh kelas anak (ClassicMode, dll).
    protected String name;
    protected WordBank wordBank;
    protected TypingStats stats;

    public GameMode(String name, WordBank wordBank) {
        this.name = name;
        this.wordBank = wordBank;
        this.stats = new TypingStats();
    }

    public abstract String getDescription();

    // Teks yang harus diketik pemain sekarang.
    public abstract String getCurrentTarget();

    // Pemain selesai mengetik satu kata/baris/teks. true kalau benar.
    public abstract boolean submit(String typed);

    public abstract boolean isOver();

    public abstract int calculateScore();

    // Method biasa (concrete) yang sama untuk semua mode.
    public String getName() {
        return name;
    }

    public TypingStats getStats() {
        return stats;
    }
}
