package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/*
 * WordBank: memuat daftar kata dan potongan kode Java dari file teks di dalam jar,
 * lalu memberikan kata/potongan kode secara acak untuk setiap mode permainan.
 * Owner: Person B
 * Materi: Java IO (BufferedReader), Exception (try-with-resources, throws, custom exception),
 *         Collections (ArrayList), String (StringBuilder, trim), Assertion
 */
public class WordBank {

    // Lokasi file di dalam jar (folder src/resources ikut masuk ke jar saat build).
    // Garis miring di depan = dicari dari "akar" jar.
    public static final String WORDS_PATH = "/resources/words_en.txt";
    public static final String SNIPPETS_PATH = "/resources/code_snippets.txt";

    // Baris yang memisahkan satu potongan kode dengan potongan berikutnya.
    private static final String SNIPPET_SEPARATOR = "---";

    // [MATERI: Collections - ArrayList]
    private ArrayList<String> words;
    // Setiap potongan kode = daftar baris (ArrayList<String>), jadi semuanya = daftar dari daftar.
    private ArrayList<ArrayList<String>> snippets;
    private Random random;

    public WordBank() {
        words = new ArrayList<>();
        snippets = new ArrayList<>();
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

    // Membaca potongan kode. Setiap potongan dipisahkan oleh baris "---".
    // Spasi di awal baris (indentasi) dibiarkan di sini; CodeMode yang memutuskan cara menampilkannya.
    public void loadSnippets(String resourcePath) throws WordFileException {
        ArrayList<String> lines = readLines(resourcePath);

        snippets.clear();
        ArrayList<String> currentSnippet = new ArrayList<>();
        for (String line : lines) {
            if (line.trim().equals(SNIPPET_SEPARATOR)) {
                // Ketemu pemisah: potongan yang sedang dikumpulkan sudah selesai.
                addSnippetIfNotEmpty(currentSnippet);
                currentSnippet = new ArrayList<>();
            } else if (!line.trim().isEmpty()) {
                // Baris kode biasa (baris kosong dilewati, karena tidak ada yang bisa diketik).
                currentSnippet.add(line);
            }
        }
        // Potongan terakhir tidak diakhiri "---", jadi tambahkan juga.
        addSnippetIfNotEmpty(currentSnippet);

        if (snippets.isEmpty()) {
            throw new WordFileException("File potongan kode kosong: " + resourcePath);
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

    private void addSnippetIfNotEmpty(ArrayList<String> snippet) {
        if (!snippet.isEmpty()) {
            snippets.add(snippet);
        }
    }

    // Satu kata acak.
    public String randomWord() {
        // [MATERI: Assertion] asumsi internal: loadWords() sudah berhasil dipanggil sebelumnya.
        assert !words.isEmpty() : "loadWords() belum dipanggil";
        int index = random.nextInt(words.size());
        return words.get(index);
    }

    // Beberapa kata acak yang digabung dengan satu spasi, contoh: "rumah besar pergi".
    // [MATERI: String - StringBuilder] lebih hemat daripada + di dalam loop.
    public String randomWords(int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            // Spasi hanya DI ANTARA kata, bukan sebelum kata pertama.
            if (i > 0) {
                builder.append(" ");
            }
            builder.append(randomWord());
        }
        return builder.toString();
    }

    // Satu potongan kode acak (daftar barisnya).
    public ArrayList<String> randomSnippet() {
        assert !snippets.isEmpty() : "loadSnippets() belum dipanggil";
        int index = random.nextInt(snippets.size());
        // Kembalikan SALINAN, supaya kalau CodeMode mengubah daftarnya, data asli di sini tetap utuh.
        return new ArrayList<>(snippets.get(index));
    }

    public int getWordCount() {
        return words.size();
    }

    public int getSnippetCount() {
        return snippets.size();
    }
}
