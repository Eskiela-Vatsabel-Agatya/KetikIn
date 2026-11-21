package mode;

import data.WordBank;

// ClassicMode: mode normal, ketik 30 kata acak, timer naik
// Owner: Eskiela Vatsabel Agatya
// Materi: inheritance, polymorphism
// [MATERI: Inheritance]
public class ClassicMode extends GameMode {

    public static final int WORD_COUNT = 30;
    public static final String NAME = "Mode normal";
    private static final double SCORE_FACTOR = 10;

    protected String targetText;
    protected boolean finished;

    public ClassicMode(WordBank wordBank) {
        super(NAME, wordBank);
        targetText = wordBank.randomWords(WORD_COUNT);
        finished = false;
    }

    // [MATERI: Polymorphism - compile-time]
    // constructor kedua untuk LetterFocusMode, target diisi sendiri oleh anaknya
    protected ClassicMode(String modeName, WordBank wordBank) {
        super(modeName, wordBank);
        targetText = "";
        finished = false;
    }

    @Override
    public String getDescription() {
        return "Ketik " + WORD_COUNT + " kata acak secepat dan setepat mungkin.";
    }

    @Override
    public String getCurrentTarget() {
        return targetText;
    }

    // dipanggil GamePanel kalau panjang ketikan sudah sama dengan target
    @Override
    public boolean submit(String typed) {
        finished = true;
        return typed.equals(targetText);
    }

    @Override
    public boolean isOver() {
        return finished;
    }

    // skor = WPM x akurasi / 100 x 10
    @Override
    public int calculateScore() {
        double rawScore = stats.getWpm() * stats.getAccuracy() / 100 * SCORE_FACTOR;
        return (int) Math.round(rawScore);
    }
}
