package data;

/*
 * InvalidUsernameException: dilempar ketika nama pemain melanggar salah satu aturan nama.
 * Pesannya menjelaskan aturan mana yang dilanggar, lalu ditampilkan di JOptionPane.
 * Owner: Person B
 * Materi: Exception (custom exception, checked exception)
 */
// [MATERI: Exception - custom exception]
public class InvalidUsernameException extends Exception {

    // TODO Phase 2: komentar kenapa dibuat checked (extends Exception).
    public InvalidUsernameException(String message) {
        super(message);
    }
}
