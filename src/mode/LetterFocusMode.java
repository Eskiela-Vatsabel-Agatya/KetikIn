package mode;

import data.WordBank;

// LetterFocusMode: latihan satu huruf, 24 dari 30 kata berisi huruf itu
// Owner: Eskiela Vatsabel Agatya
// Materi: inheritance, polymorphism
// [MATERI: Inheritance - multi-level]
// GameMode <- ClassicMode <- LetterFocusMode, cara ketik dan skor diwarisi dari ClassicMode
public class LetterFocusMode extends ClassicMode {

    public static final int FOCUS_WORD_COUNT = 24;

    // selalu huruf kecil, sama seperti daftar kata
    private char focusLetter;

    public LetterFocusMode(WordBank wordBank, char letter) {
        super("Fokus huruf " + Character.toUpperCase(letter), wordBank);
        focusLetter = Character.toLowerCase(letter);
        targetText = wordBank.randomWordsWithFocus(focusLetter, WORD_COUNT, FOCUS_WORD_COUNT);
    }

    @Override
    public String getDescription() {
        return FOCUS_WORD_COUNT + " dari " + WORD_COUNT + " kata berisi huruf "
                + getFocusLabel() + ". Huruf itu ditulis tebal.";
    }

    @Override
    public char getFocusLetter() {
        return focusLetter;
    }

    @Override
    public String getFocusLabel() {
        return String.valueOf(Character.toUpperCase(focusLetter));
    }
}
