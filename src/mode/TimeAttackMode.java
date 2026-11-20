package mode;

import java.util.ArrayList;

import data.WordBank;

/*
 * TimeAttackMode: ketik kata sebanyak-banyaknya dalam 60 detik. Tekan Spasi untuk
 * mengirim satu kata. Skor = kata benar x 10 + jumlah huruf benar.
 * Owner: Person A
 * Materi: Inheritance (extends GameMode), Abstraction (implements Timed), Collections (ArrayList)
 */
// [MATERI: Inheritance] + [MATERI: Abstraction - interface]
public class TimeAttackMode extends GameMode implements Timed {

    public static final long TIME_LIMIT_MILLIS = 60000;
    public static final int POINTS_PER_WORD = 10;

    // Antrean kata yang akan datang. Kata pertama = kata yang sedang diketik.
    private ArrayList<String> upcomingWords;
    private int correctWords;
    private int correctCharacters;
    private boolean timeUp;

    public TimeAttackMode(WordBank wordBank) {
        super(MODE_TIME_ATTACK, wordBank);
        upcomingWords = new ArrayList<>();
        // TODO Phase 4: isi antrean kata.
    }

    @Override
    public String getDescription() {
        // TODO Phase 4
        return "";
    }

    @Override
    public String getCurrentTarget() {
        // TODO Phase 4
        return "";
    }

    // Beberapa kata berikutnya, untuk ditampilkan abu-abu di sebelah kata sekarang.
    public String getNextWordsText() {
        // TODO Phase 4
        return "";
    }

    @Override
    public boolean submit(String typed) {
        // TODO Phase 4
        return false;
    }

    @Override
    public boolean isOver() {
        return timeUp;
    }

    @Override
    public int calculateScore() {
        // TODO Phase 4
        return 0;
    }

    @Override
    public long getTimeLimitMillis() {
        return TIME_LIMIT_MILLIS;
    }

    @Override
    public void onTimeUp() {
        // TODO Phase 4
    }
}
