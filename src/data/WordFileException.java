package data;

// WordFileException: dilempar kalau file kata tidak ketemu atau kosong
// Owner: Satrio Ananda Widiyanto
// Materi: exception
// [MATERI: Exception - custom exception]
// checked exception, jadi pemanggil loadWords() wajib menanganinya
public class WordFileException extends Exception {

    public WordFileException(String message) {
        super(message);
    }
}
