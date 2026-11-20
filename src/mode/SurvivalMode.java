package mode;

import data.WordBank;

/*
 * SurvivalMode: satu kata setiap kali, 5 detik per kata, 3 nyawa. Salah ketik atau
 * kehabisan waktu = nyawa berkurang satu. Permainan selesai ketika nyawa habis.
 * Owner: Person A
 * Materi: Inheritance (extends GameMode), Abstraction (implements Timed), Assertion
 */
// [MATERI: Inheritance] + [MATERI: Abstraction - interface]
public class SurvivalMode extends GameMode implements Timed {

    public static final int START_LIVES = 3;
    public static final long WORD_TIME_LIMIT_MILLIS = 5000;
    public static final int POINTS_PER_WORD = 15;

    private int lives;
    private int wordsSurvived;
    private int correctCharacters;
    private String currentWord;

    public SurvivalMode(WordBank wordBank) {
        super(MODE_SURVIVAL, wordBank);
        lives = START_LIVES;
        // TODO Phase 4: currentWord = wordBank.randomWord();
        currentWord = "";
    }

    @Override
    public String getDescription() {
        // TODO Phase 4
        return "";
    }

    @Override
    public String getCurrentTarget() {
        return currentWord;
    }

    @Override
    public boolean submit(String typed) {
        // TODO Phase 4
        return false;
    }

    @Override
    public boolean isOver() {
        return lives <= 0;
    }

    @Override
    public int calculateScore() {
        // TODO Phase 4
        return 0;
    }

    @Override
    public long getTimeLimitMillis() {
        return WORD_TIME_LIMIT_MILLIS;
    }

    @Override
    public void onTimeUp() {
        // TODO Phase 4: kurangi nyawa, ganti kata.
    }

    public int getLives() {
        return lives;
    }
}
