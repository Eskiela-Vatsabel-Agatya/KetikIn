package data;

/*
 * WordFileException: dilempar ketika file kata tidak bisa dibaca
 * (tidak ditemukan atau isinya kosong).
 * Owner: Person B
 * Materi: Exception (custom exception, checked exception)
 */
// [MATERI: Exception - custom exception]
// Kita pakai "extends Exception" (checked exception), bukan RuntimeException.
// Artinya compiler MEMAKSA pemanggil loadWords() untuk menangani error ini
// (try/catch atau throws). Jadi tidak mungkin ada yang lupa bahwa file kata bisa gagal dibaca.
public class WordFileException extends Exception {

    public WordFileException(String message) {
        // super(message) = simpan pesan di kelas induk (Exception), nanti dibaca dengan getMessage().
        super(message);
    }
}
