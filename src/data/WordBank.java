package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

// WordBank: muat daftar kata dari jar dan kasih kata acak
// Owner: Satrio Ananda Widiyanto
// Materi: Java IO, exception, collections, string, assertion
public class WordBank {

    public static final String WORDS_PATH = "/resources/words_en.txt";

    // [MATERI: Collections - ArrayList]
    private ArrayList<String> words;
    private Random random;

    public WordBank() {
        words = new ArrayList<>();
        random = new Random();
    }

    // satu kata per baris, baris kosong dilewati
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

    // [MATERI: Java IO]
    // file di dalam jar harus dibaca lewat getResourceAsStream, bukan FileReader
    private ArrayList<String> readLines(String resourcePath) throws WordFileException {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new WordFileException("File tidak ditemukan: " + resourcePath);
        }

        ArrayList<String> lines = new ArrayList<>();
        // [MATERI: Exception - try-with-resources]
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            // ubah jadi WordFileException biar pemanggil cukup tangani satu jenis error
            throw new WordFileException("Gagal membaca " + resourcePath + ": " + e.getMessage());
        }
        return lines;
    }

    private String randomFrom(ArrayList<String> list) {
        // [MATERI: Assertion]
        assert !list.isEmpty() : "Daftar kata kosong";
        int index = random.nextInt(list.size());
        return list.get(index);
    }

    public String randomWord() {
        return randomFrom(words);
    }

    // contoh hasil: "house green river"
    public String randomWords(int count) {
        ArrayList<String> chosen = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            chosen.add(randomWord());
        }
        return joinWords(chosen);
    }

    // [MATERI: String - indexOf]
    // semua kata yang berisi huruf itu di posisi mana saja
    public ArrayList<String> wordsContaining(char letter) {
        ArrayList<String> result = new ArrayList<>();
        for (String word : words) {
            if (word.indexOf(letter) >= 0) {
                result.add(word);
            }
        }
        return result;
    }

    // contoh: ('j', 30, 24) = 24 kata berisi j + 6 kata biasa, lalu diacak
    public String randomWordsWithFocus(char letter, int count, int focusCount) {
        ArrayList<String> chosen = new ArrayList<>();

        // kata yang berisi huruf fokus
        ArrayList<String> focusWords = wordsContaining(letter);
        for (int i = 0; i < focusCount; i++) {
            chosen.add(randomFrom(focusWords));
        }

        // sisanya kata acak biasa
        for (int i = focusCount; i < count; i++) {
            chosen.add(randomWord());
        }

        // [MATERI: Collections - shuffle]
        Collections.shuffle(chosen);

        return joinWords(chosen);
    }

    // [MATERI: String - StringBuilder]
    private String joinWords(ArrayList<String> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
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
