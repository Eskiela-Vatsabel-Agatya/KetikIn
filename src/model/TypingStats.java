package model;

import java.util.HashMap;

// TypingStats: hitung akurasi, WPM, dan huruf yang sering salah
// Owner: Eskiela Vatsabel Agatya
// Materi: encapsulation, collections, polymorphism, assertion
// [MATERI: Encapsulation]
public class TypingStats {

    private static final double CHARS_PER_WORD = 5.0;
    private static final double MILLIS_PER_MINUTE = 60000.0;
    // di bawah 1 detik WPM dianggap 0 biar tidak melonjak di awal
    private static final long MIN_MILLIS_FOR_WPM = 1000;

    private int totalKeystrokes;
    private int correctKeystrokes;
    // 0 = belum diisi
    private long startTimeMillis;
    private long endTimeMillis;

    // [MATERI: Collections - HashMap]
    // huruf yang seharusnya diketik -> berapa kali salah
    private HashMap<Character, Integer> mistakes;

    public TypingStats() {
        mistakes = new HashMap<>();
    }

    public void start() {
        startTimeMillis = System.currentTimeMillis();
        endTimeMillis = 0;
    }

    public void stop() {
        endTimeMillis = System.currentTimeMillis();
    }

    public long getElapsedMillis() {
        if (startTimeMillis == 0) {
            return 0;
        }
        // belum stop, hitung sampai sekarang
        if (endTimeMillis == 0) {
            return System.currentTimeMillis() - startTimeMillis;
        }
        return endTimeMillis - startTimeMillis;
    }

    // satu-satunya cara ubah penghitung dari luar
    public void recordKeystroke(char typed, char expected) {
        totalKeystrokes++;

        if (typed == expected) {
            correctKeystrokes++;
        } else {
            // catat salah di huruf yang seharusnya, huruf kecil biar A dan a dihitung sama
            char key = Character.toLowerCase(expected);
            Integer oldCount = mistakes.get(key);
            if (oldCount == null) {
                oldCount = 0;
            }
            mistakes.put(key, oldCount + 1);
        }
    }

    // akurasi = benar / total x 100
    public double getAccuracy() {
        if (totalKeystrokes == 0) {
            return 100;
        }
        double accuracy = (double) correctKeystrokes / totalKeystrokes * 100;

        // [MATERI: Assertion]
        assert accuracy >= 0 && accuracy <= 100 : "Akurasi harus di antara 0 dan 100: " + accuracy;
        return accuracy;
    }

    public double getWpm() {
        return getWpm(getElapsedMillis());
    }

    // [MATERI: Polymorphism - compile-time]
    // WPM = (benar / 5) / menit, dipakai timer dan SelfTest
    public double getWpm(long elapsedMillis) {
        if (elapsedMillis < MIN_MILLIS_FOR_WPM) {
            return 0;
        }
        double words = correctKeystrokes / CHARS_PER_WORD;
        double minutes = elapsedMillis / MILLIS_PER_MINUTE;
        return words / minutes;
    }

    // kembalikan salinan biar data asli tidak bisa diubah dari luar
    public HashMap<Character, Integer> getMistakes() {
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
