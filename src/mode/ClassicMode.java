package mode;

import data.WordBank;

/*
 * ClassicMode: pemain mengetik satu paragraf berisi 30 kata acak. Selesai ketika
 * panjang ketikan sama dengan panjang teks target. Timer menghitung naik.
 * Owner: Person A
 * Materi: Inheritance (extends GameMode, super), Polymorphism runtime (@Override)
 */
// [MATERI: Inheritance]
public class ClassicMode extends GameMode {

    public static final int WORD_COUNT = 30;

    protected String targetText;
    protected boolean finished;

    public ClassicMode(WordBank wordBank) {
        super(MODE_CLASSIC, wordBank);
        // TODO Phase 3: targetText = wordBank.randomWords(WORD_COUNT);
        targetText = "";
    }

    @Override
    public String getDescription() {
        // TODO Phase 3
        return "";
    }

    @Override
    public String getCurrentTarget() {
        return targetText;
    }

    @Override
    public boolean submit(String typed) {
        // TODO Phase 3
        return false;
    }

    @Override
    public boolean isOver() {
        return finished;
    }

    @Override
    public int calculateScore() {
        // TODO Phase 3: round(wpm * accuracy / 100 * 10)
        return 0;
    }
}
