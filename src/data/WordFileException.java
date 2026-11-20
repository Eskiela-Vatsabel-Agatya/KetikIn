package data;

/*
 * WordFileException: dilempar ketika file kata atau file potongan kode tidak bisa dibaca
 * (tidak ditemukan atau isinya kosong).
 * Owner: Person B
 * Materi: Exception (custom exception, checked exception)
 */
// [MATERI: Exception - custom exception]
public class WordFileException extends Exception {

    // TODO Phase 2: komentar kenapa dibuat checked (extends Exception).
    public WordFileException(String message) {
        super(message);
    }
}
