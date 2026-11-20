package mode;

import java.util.ArrayList;

import data.WordBank;

/*
 * CodeMode: pemain mengetik potongan kode Java baris demi baris (5 baris per ronde),
 * tekan Enter untuk mengirim satu baris. Harus sama persis (spasi, huruf besar/kecil, simbol).
 * CodeMode -> ClassicMode -> GameMode: multi-level inheritance. Rumus skornya memakai
 * rumus Classic dikali 1.5.
 * Owner: Person A
 * Materi: Inheritance (multi-level, super), Polymorphism runtime (@Override), String
 */
// [MATERI: Inheritance - multilevel]
public class CodeMode extends ClassicMode {

    public static final int LINES_PER_ROUND = 5;
    public static final double SCORE_MULTIPLIER = 1.5;

    private ArrayList<String> lines;
    private int currentLineIndex;

    public CodeMode(WordBank wordBank) {
        super(wordBank);
        // TODO Phase 5: nama mode = MODE_CODE, ambil potongan kode acak, trim setiap baris.
        lines = new ArrayList<>();
        currentLineIndex = 0;
    }

    @Override
    public String getDescription() {
        // TODO Phase 5
        return "";
    }

    @Override
    public String getCurrentTarget() {
        // TODO Phase 5
        return "";
    }

    @Override
    public boolean submit(String typed) {
        // TODO Phase 5
        return false;
    }

    @Override
    public boolean isOver() {
        // TODO Phase 5
        return false;
    }

    @Override
    public int calculateScore() {
        // TODO Phase 5: Math.round(super.calculateScore() * SCORE_MULTIPLIER)
        return 0;
    }
}
