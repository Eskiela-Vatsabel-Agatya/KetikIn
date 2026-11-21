package mode;

import data.WordBank;
import model.TypingStats;

// GameMode: kelas induk abstract untuk semua mode permainan
// Owner: Eskiela Vatsabel Agatya
// Materi: abstraction, inheritance, encapsulation, polymorphism
// [MATERI: Abstraction - abstract class]
public abstract class GameMode {

    public static final char NO_FOCUS = ' ';
    // isi kolom mode di CSV kalau tidak ada huruf fokus (materi tidak pakai enum)
    public static final String FOCUS_NORMAL = "Normal";

    protected String name;
    protected WordBank wordBank;
    protected TypingStats stats;

    public GameMode(String name, WordBank wordBank) {
        this.name = name;
        this.wordBank = wordBank;
        this.stats = new TypingStats();
    }

    // method abstract, wajib diisi tiap mode
    public abstract String getDescription();

    public abstract String getCurrentTarget();

    // true kalau semua huruf benar
    public abstract boolean submit(String typed);

    public abstract boolean isOver();

    public abstract int calculateScore();

    public String getName() {
        return name;
    }

    public TypingStats getStats() {
        return stats;
    }

    // [MATERI: Polymorphism - runtime]
    // default mode normal, di-override LetterFocusMode jadi GamePanel tidak perlu instanceof
    public char getFocusLetter() {
        return NO_FOCUS;
    }

    // "Normal" atau huruf besar, misalnya "J"
    public String getFocusLabel() {
        return FOCUS_NORMAL;
    }
}
