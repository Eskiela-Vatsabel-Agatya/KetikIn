package data;

import java.util.ArrayList;
import java.util.Random;

/*
 * WordBank: memuat daftar kata dan potongan kode Java dari file teks di dalam jar,
 * lalu memberikan kata/potongan kode secara acak untuk setiap mode permainan.
 * Owner: Person B
 * Materi: Java IO (BufferedReader), Exception (try-with-resources, throws),
 *         Collections (ArrayList), String (StringBuilder)
 */
public class WordBank {

    // Lokasi file di dalam jar (folder src/resources ikut masuk ke jar saat build).
    public static final String WORDS_PATH = "/resources/words_en.txt";
    public static final String SNIPPETS_PATH = "/resources/code_snippets.txt";

    // Baris yang memisahkan satu potongan kode dengan potongan berikutnya.
    private static final String SNIPPET_SEPARATOR = "---";

    private ArrayList<String> words;
    // Setiap potongan kode = daftar baris (ArrayList<String>).
    private ArrayList<ArrayList<String>> snippets;
    private Random random;

    public WordBank() {
        words = new ArrayList<>();
        snippets = new ArrayList<>();
        random = new Random();
    }

    // Membaca semua kata dari file resource, satu kata per baris.
    public void loadWords(String resourcePath) throws WordFileException {
        // TODO Phase 2
    }

    // Membaca potongan kode, dipisahkan oleh baris "---".
    public void loadSnippets(String resourcePath) throws WordFileException {
        // TODO Phase 2
    }

    // Satu kata acak.
    public String randomWord() {
        // TODO Phase 2
        return "";
    }

    // Beberapa kata acak yang digabung dengan satu spasi.
    public String randomWords(int count) {
        // TODO Phase 2
        return "";
    }

    // Satu potongan kode acak (daftar barisnya).
    public ArrayList<String> randomSnippet() {
        // TODO Phase 2
        return new ArrayList<>();
    }

    public int getWordCount() {
        return words.size();
    }

    public int getSnippetCount() {
        return snippets.size();
    }
}
