package model;

import java.util.HashMap;

/*
 * TypingStats: menghitung statistik mengetik (jumlah ketukan, ketukan benar, akurasi, WPM)
 * dan mencatat huruf mana yang paling sering salah. Penghitungnya private dan hanya
 * bisa berubah lewat recordKeystroke, tidak ada setter.
 * Owner: Person A
 * Materi: Encapsulation, Collections (HashMap), Polymorphism compile-time (overload getWpm),
 *         Assertion
 *
 * Rumus:
 *   Akurasi = ketukan benar / total ketukan x 100   (100 kalau belum ada ketukan)
 *   WPM     = (ketukan benar / 5) / menit berlalu   (0 kalau belum 1 detik)
 *   1 "kata" dihitung sebagai 5 karakter, cara standar menghitung WPM.
 */
// [MATERI: Encapsulation] semua field private, tidak ada setter.
public class TypingStats {

    private static final double CHARS_PER_WORD = 5.0;
    private static final double MILLIS_PER_MINUTE = 60000.0;
    // Di bawah 1 detik, WPM dianggap 0 (kalau tidak, angkanya melonjak aneh di awal).
    private static final long MIN_MILLIS_FOR_WPM = 1000;

    private int totalKeystrokes;
    private int correctKeystrokes;
    // 0 artinya "belum diisi".
    private long startTimeMillis;
    private long endTimeMillis;

    // [MATERI: Collections - HashMap]
    // Kunci = huruf yang SEHARUSNYA diketik, nilai = berapa kali salah di huruf itu.
    private HashMap<Character, Integer> mistakes;

    public TypingStats() {
        mistakes = new HashMap<>();
    }

    // Mulai menghitung waktu.
    public void start() {
        startTimeMillis = System.currentTimeMillis();
        endTimeMillis = 0;
    }

    // Berhenti menghitung waktu (permainan selesai).
    public void stop() {
        endTimeMillis = System.currentTimeMillis();
    }

    // Berapa milidetik sudah berlalu sejak start().
    public long getElapsedMillis() {
        // Belum start: belum ada waktu yang berlalu.
        if (startTimeMillis == 0) {
            return 0;
        }
        // Sudah start tapi belum stop: hitung sampai sekarang.
        if (endTimeMillis == 0) {
            return System.currentTimeMillis() - startTimeMillis;
        }
        // Sudah stop: hitung sampai waktu stop.
        return endTimeMillis - startTimeMillis;
    }

    // Satu-satunya cara mengubah penghitung dari luar kelas.
    // typed = huruf yang diketik pemain, expected = huruf yang seharusnya.
    public void recordKeystroke(char typed, char expected) {
        totalKeystrokes++;

        if (typed == expected) {
            correctKeystrokes++;
        } else {
            // Catat kesalahan pada huruf yang seharusnya, dalam huruf kecil ('A' dan 'a' = tombol sama).
            char key = Character.toLowerCase(expected);

            // Langkah 1: ambil jumlah salah yang lama. Hasilnya null kalau huruf ini belum pernah salah.
            // (char otomatis dibungkus jadi Character = autoboxing, materi Week 3.)
            Integer oldCount = mistakes.get(key);
            // Langkah 2: kalau null, anggap 0.
            if (oldCount == null) {
                oldCount = 0;
            }
            // Langkah 3: tambah 1 dan simpan lagi.
            mistakes.put(key, oldCount + 1);
        }
    }

    public double getAccuracy() {
        if (totalKeystrokes == 0) {
            return 100;
        }
        // (double) supaya pembagian tidak dibulatkan seperti pembagian int.
        double accuracy = (double) correctKeystrokes / totalKeystrokes * 100;

        // [MATERI: Assertion] ini asumsi internal: kalau rumusnya benar, hasilnya pasti 0..100.
        assert accuracy >= 0 && accuracy <= 100 : "Akurasi harus di antara 0 dan 100: " + accuracy;
        return accuracy;
    }

    // WPM sampai sekarang (memakai waktu dari start()).
    public double getWpm() {
        return getWpm(getElapsedMillis());
    }

    // [MATERI: Polymorphism - compile-time] overload: nama sama dengan getWpm(), parameternya beda.
    // Versi ini dipakai timer (yang sudah tahu elapsed) dan oleh SelfTest (waktu yang pasti).
    public double getWpm(long elapsedMillis) {
        if (elapsedMillis < MIN_MILLIS_FOR_WPM) {
            return 0;
        }
        double words = correctKeystrokes / CHARS_PER_WORD;
        double minutes = elapsedMillis / MILLIS_PER_MINUTE;
        return words / minutes;
    }

    // Mengembalikan SALINAN map, bukan map aslinya.
    // Kalau kita kembalikan map aslinya, kode lain bisa mengubah data kita (misalnya put/clear),
    // dan itu melanggar encapsulation. Dengan salinan, data asli hanya berubah lewat recordKeystroke.
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
