package model;

import java.util.HashMap;

/*
 * TypingStats: menghitung statistik mengetik (jumlah ketukan, ketukan benar, akurasi, WPM)
 * dan mencatat huruf mana yang paling sering salah. Penghitungnya private dan hanya
 * bisa berubah lewat recordKeystroke, tidak ada setter.
 * Owner: Person A
 * Materi: Encapsulation, Collections (HashMap), Polymorphism compile-time (overload getWpm),
 *         Assertion
 */
// [MATERI: Encapsulation]
public class TypingStats {

    private int totalKeystrokes;
    private int correctKeystrokes;
    private long startTimeMillis;
    private long endTimeMillis;
    // Kunci = huruf yang seharusnya diketik, nilai = berapa kali salah di huruf itu.
    private HashMap<Character, Integer> mistakes;

    public TypingStats() {
        mistakes = new HashMap<>();
    }

    public void start() {
        // TODO Phase 2
    }

    public void stop() {
        // TODO Phase 2
    }

    public long getElapsedMillis() {
        // TODO Phase 2
        return 0;
    }

    // Satu-satunya cara mengubah penghitung.
    public void recordKeystroke(char typed, char expected) {
        // TODO Phase 2
    }

    public double getAccuracy() {
        // TODO Phase 2
        return 100;
    }

    public double getWpm() {
        // TODO Phase 2: return getWpm(getElapsedMillis());
        return 0;
    }

    // [MATERI: Polymorphism - compile-time] overload dari getWpm()
    public double getWpm(long elapsedMillis) {
        // TODO Phase 2
        return 0;
    }

    public HashMap<Character, Integer> getMistakes() {
        // TODO Phase 2: kembalikan salinan, bukan map aslinya.
        return new HashMap<>(mistakes);
    }

    public int getTotalKeystrokes() {
        return totalKeystrokes;
    }

    public int getCorrectKeystrokes() {
        return correctKeystrokes;
    }

    public int getWrongKeystrokes() {
        return totalKeystrokes - correctKeystrokes;
    }
}
