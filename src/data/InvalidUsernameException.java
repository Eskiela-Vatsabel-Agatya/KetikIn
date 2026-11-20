package data;

/*
 * InvalidUsernameException: dilempar ketika nama pemain melanggar salah satu aturan nama.
 * Pesannya menjelaskan aturan mana yang dilanggar, lalu ditampilkan di JOptionPane.
 * Owner: Person B
 * Materi: Exception (custom exception, checked exception)
 */
// [MATERI: Exception - custom exception]
// Kita pakai "extends Exception" (checked exception), bukan RuntimeException.
// Artinya compiler MEMAKSA siapa pun yang membuat Player atau memanggil Player.validate()
// untuk menangani kemungkinan nama salah (try/catch atau throws).
public class InvalidUsernameException extends Exception {

    public InvalidUsernameException(String message) {
        // super(message) = simpan pesan di kelas induk (Exception), nanti dibaca dengan getMessage().
        super(message);
    }
}
