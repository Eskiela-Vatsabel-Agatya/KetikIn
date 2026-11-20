package mode;

import data.WordBank;
import model.TypingStats;

/*
 * GameMode: kelas induk (abstract) untuk semua mode permainan. Isinya hal-hal yang
 * dimiliki semua mode (nama, bank kata, statistik) dan daftar method yang WAJIB
 * dibuat oleh setiap mode.
 * Kenapa abstract? "Mode permainan" saja belum punya aturan; aturannya baru ada di mode
 * yang konkret (Classic, Time Attack, ...). Jadi GameMode tidak boleh dibuat objeknya langsung.
 * Owner: Person A
 * Materi: Abstraction (abstract class), Inheritance (kelas induk), Encapsulation (protected),
 *         Polymorphism runtime (method yang di-override oleh kelas anak)
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

    // ---------- Method abstract: WAJIB dibuat oleh setiap mode ----------

    public abstract String getDescription();

    // Teks yang harus diketik pemain sekarang.
    public abstract String getCurrentTarget();

    // Pemain selesai mengetik satu kata/baris/teks. true kalau benar.
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
    // Isinya nilai default untuk mode "teks panjang" (Classic). Mode lain yang berbeda
    // cukup meng-override method yang perlu saja.

    // true kalau pemain mengirim satu kata setiap kali dengan Spasi/Enter (Time Attack, Survival).
    // false kalau pemain mengetik satu teks utuh (Classic).
    public boolean isWordMode() {
        return false;
    }

    // true kalau pemain mengetik satu baris lalu mengirimnya dengan Enter (Code Mode).
    // false kalau teks langsung selesai begitu panjang ketikan sama dengan target (Classic).
    public boolean isLineMode() {
        return false;
    }

    // Kata-kata berikutnya yang ditampilkan abu-abu setelah target. Kosong kalau tidak ada.
    public String getUpcomingText() {
        return "";
    }

    // Info tambahan di pojok kanan atas layar game (misalnya nyawa). Kosong kalau tidak ada.
    public String getStatusText() {
        return "";
    }
}
