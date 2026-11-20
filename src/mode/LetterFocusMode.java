package mode;

import data.WordBank;

/*
 * LetterFocusMode: latihan satu huruf. Sama seperti mode normal (30 kata, timer naik, skor sama),
 * tapi 24 dari 30 kata (80%) berisi huruf yang dipilih pemain, misalnya J.
 * Huruf itu ditebalkan di teks target oleh GamePanel.
 * Owner: Person A
 * Materi: Inheritance multi-level (GameMode <- ClassicMode <- LetterFocusMode, super),
 *         Polymorphism runtime (@Override getDescription, getFocusLetter, getFocusLabel)
 */
// [MATERI: Inheritance - multi-level] LetterFocusMode adalah anak dari ClassicMode,
// dan ClassicMode adalah anak dari GameMode. Jadi LetterFocusMode juga sebuah GameMode.
// Yang diwarisi dari ClassicMode TANPA ditulis ulang: getCurrentTarget(), submit(),
// isOver(), dan calculateScore(). Cara mengetik, cara selesai, dan rumus skornya sama persis.
// Yang berbeda hanya cara memilih 30 kata, nama, penjelasan, dan huruf fokusnya.
public class LetterFocusMode extends ClassicMode {

    // 24 dari 30 kata = 80% kata berisi huruf fokus.
    public static final int FOCUS_WORD_COUNT = 24;

    // Huruf yang dilatih, selalu huruf kecil (daftar kata juga huruf kecil semua).
    private char focusLetter;

    public LetterFocusMode(WordBank wordBank, char letter) {
        // Constructor ClassicMode yang protected: nama mode dipilih sendiri, target masih kosong.
        // Contoh nama: "Fokus huruf J".
        super("Fokus huruf " + Character.toUpperCase(letter), wordBank);
        focusLetter = Character.toLowerCase(letter);
        // targetText adalah field protected milik ClassicMode, jadi boleh diisi di sini.
        targetText = wordBank.randomWordsWithFocus(focusLetter, WORD_COUNT, FOCUS_WORD_COUNT);
    }

    @Override
    public String getDescription() {
        return FOCUS_WORD_COUNT + " dari " + WORD_COUNT + " kata berisi huruf "
                + getFocusLabel() + ". Huruf itu ditulis tebal.";
    }

    // Menimpa versi GameMode yang mengembalikan NO_FOCUS.
    @Override
    public char getFocusLetter() {
        return focusLetter;
    }

    // Menimpa versi GameMode yang mengembalikan "Normal". Hasilnya huruf besar, misalnya "J".
    @Override
    public String getFocusLabel() {
        return String.valueOf(Character.toUpperCase(focusLetter));
    }
}
