package mode;

import data.WordBank;

/*
 * ClassicMode: pemain mengetik satu paragraf berisi 30 kata acak. Selesai ketika
 * panjang ketikan sama dengan panjang teks target. Timer menghitung naik.
 * Skor = WPM x akurasi / 100 x 10, dibulatkan.
 * Owner: Person A
 * Materi: Inheritance (extends GameMode, super), Polymorphism runtime (@Override)
 */
// [MATERI: Inheritance] ClassicMode adalah anak dari GameMode.
// Ia mewarisi field name, wordBank, stats dan method getName(), getStats(),
// lalu WAJIB mengisi semua method abstract milik GameMode.
public class ClassicMode extends GameMode {

    public static final int WORD_COUNT = 30;
    private static final double SCORE_FACTOR = 10;

    // protected: nanti CodeMode (anak dari ClassicMode) juga bisa memakainya.
    protected String targetText;
    protected boolean finished;

    public ClassicMode(WordBank wordBank) {
        // super(...) memanggil constructor GameMode dulu (mengisi name, wordBank, stats).
        super(MODE_CLASSIC, wordBank);
        targetText = wordBank.randomWords(WORD_COUNT);
        finished = false;
    }

    // Constructor kedua, KHUSUS untuk kelas anak (CodeMode).
    // Bedanya dengan constructor di atas: nama mode bisa dipilih sendiri, dan teks target
    // belum diisi (CodeMode mengisinya dengan baris kode, bukan kata acak).
    // protected = hanya kelas anak (dan kelas di package yang sama) yang boleh memakainya.
    // [MATERI: Polymorphism - compile-time] dua constructor dengan parameter berbeda = overloading.
    protected ClassicMode(String modeName, WordBank wordBank) {
        super(modeName, wordBank);
        targetText = "";
        finished = false;
    }

    // @Override = "method ini menimpa method milik kelas induk".
    // Kalau nama atau parameternya salah ketik, compiler langsung memberi error.
    @Override
    public String getDescription() {
        return "Ketik " + WORD_COUNT + " kata acak secepat dan setepat mungkin.";
    }

    @Override
    public String getCurrentTarget() {
        return targetText;
    }

    // Dipanggil oleh GamePanel ketika panjang ketikan sudah sama dengan panjang target.
    // Ronde Classic langsung selesai. Hasilnya true kalau semua huruf benar.
    @Override
    public boolean submit(String typed) {
        finished = true;
        return typed.equals(targetText);
    }

    @Override
    public boolean isOver() {
        return finished;
    }

    @Override
    public int calculateScore() {
        double rawScore = stats.getWpm() * stats.getAccuracy() / 100 * SCORE_FACTOR;
        // Math.round menghasilkan long, jadi diubah (cast) ke int.
        return (int) Math.round(rawScore);
    }
}
