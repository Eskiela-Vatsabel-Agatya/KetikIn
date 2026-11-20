package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/*
 * WordBank: memuat daftar kata dari file teks di dalam jar, lalu memberikan kata acak
 * untuk permainan: kata acak biasa, atau kata-kata yang kebanyakan berisi satu huruf fokus.
 * Owner: Person B
 * Materi: Java IO (BufferedReader), Exception (try-with-resources, throws, custom exception),
 *         Collections (ArrayList, Collections.shuffle), String (indexOf, StringBuilder, trim),
 *         Assertion
 */
public class WordBank {

    // Lokasi file di dalam jar (folder src/resources ikut masuk ke jar saat build).
    // Garis miring di depan = dicari dari "akar" jar.
    public static final String WORDS_PATH = "/resources/words_en.txt";

    // [MATERI: Collections - ArrayList]
    private ArrayList<String> words;
    private Random random;

    public WordBank() {
        words = new ArrayList<>();
        random = new Random();
    }

    // Membaca semua kata dari file resource, satu kata per baris. Baris kosong dilewati.
    public void loadWords(String resourcePath) throws WordFileException {
        ArrayList<String> lines = readLines(resourcePath);

        words.clear();
        for (String line : lines) {
            String word = line.trim();
            if (!word.isEmpty()) {
                words.add(word);
            }
        }

        if (words.isEmpty()) {
            throw new WordFileException("File kata kosong: " + resourcePath);
        }
    }

    // [MATERI: Java IO] Membaca semua baris dari file yang ada DI DALAM jar.
    // File di dalam jar bukan file biasa di harddisk, jadi FileReader tidak bisa membukanya.
    // getResourceAsStream membuka file itu sebagai aliran byte (InputStream),
    // lalu InputStreamReader mengubahnya menjadi aliran karakter (UTF-8) untuk BufferedReader.
    private ArrayList<String> readLines(String resourcePath) throws WordFileException {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        // Hasilnya null kalau file tidak ada di dalam jar.
        if (stream == null) {
            throw new WordFileException("File tidak ditemukan: " + resourcePath);
        }

        ArrayList<String> lines = new ArrayList<>();
        // [MATERI: Exception - try-with-resources]
        // reader otomatis ditutup (close) setelah blok try selesai, walaupun terjadi error.
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line;
            // Pola baca baris seperti di materi Java IO: readLine() mengembalikan null di akhir file.
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            // Ubah IOException menjadi WordFileException, supaya pemanggil cukup menangani satu jenis error.
            throw new WordFileException("Gagal membaca " + resourcePath + ": " + e.getMessage());
        }
        return lines;
    }

    // Satu kata acak dari daftar mana saja.
    private String randomFrom(ArrayList<String> list) {
        // [MATERI: Assertion] asumsi internal: daftarnya tidak kosong.
        // (Untuk daftar semua kata: loadWords() sudah berhasil dipanggil sebelumnya.)
        assert !list.isEmpty() : "Daftar kata kosong";
        int index = random.nextInt(list.size());
        return list.get(index);
    }

    // Satu kata acak dari semua kata.
    public String randomWord() {
        return randomFrom(words);
    }

    // Beberapa kata acak yang digabung dengan satu spasi, contoh: "house green river".
    public String randomWords(int count) {
        ArrayList<String> chosen = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            chosen.add(randomWord());
        }
        return joinWords(chosen);
    }

    // Semua kata yang berisi huruf tertentu, di posisi mana saja.
    // Contoh untuk 'g': "garden", "big", "bloggers"... letter harus huruf kecil.
    // [MATERI: String - indexOf] indexOf mengembalikan posisi huruf itu di dalam kata,
    // atau -1 kalau hurufnya tidak ada. Jadi ">= 0" artinya "hurufnya ada".
    public ArrayList<String> wordsContaining(char letter) {
        ArrayList<String> result = new ArrayList<>();
        for (String word : words) {
            if (word.indexOf(letter) >= 0) {
                result.add(word);
            }
        }
        return result;
    }

    // Kata-kata untuk latihan satu huruf: 'focusCount' kata berisi huruf itu,
    // sisanya (count - focusCount) kata acak biasa, lalu urutannya diacak.
    // Contoh: randomWordsWithFocus('j', 30, 24) = 24 kata berisi j + 6 kata biasa.
    public String randomWordsWithFocus(char letter, int count, int focusCount) {
        ArrayList<String> chosen = new ArrayList<>();

        // Langkah 1: ambil 'focusCount' kata acak yang berisi huruf fokus.
        ArrayList<String> focusWords = wordsContaining(letter);
        for (int i = 0; i < focusCount; i++) {
            chosen.add(randomFrom(focusWords));
        }

        // Langkah 2: tambahkan kata acak biasa sampai jumlahnya 'count'.
        for (int i = focusCount; i < count; i++) {
            chosen.add(randomWord());
        }

        // Langkah 3: acak urutannya, supaya 6 kata biasa tidak selalu ada di akhir.
        // [MATERI: Collections - shuffle]
        Collections.shuffle(chosen);

        // Langkah 4: gabungkan menjadi satu teks dengan spasi.
        return joinWords(chosen);
    }

    // Menggabungkan daftar kata dengan satu spasi di antaranya.
    // [MATERI: String - StringBuilder] lebih hemat daripada + di dalam loop.
    private String joinWords(ArrayList<String> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            // Spasi hanya DI ANTARA kata, bukan sebelum kata pertama.
            if (i > 0) {
                builder.append(" ");
            }
            builder.append(list.get(i));
        }
        return builder.toString();
    }

    public int getWordCount() {
        return words.size();
    }
}
