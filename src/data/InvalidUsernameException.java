package data;

// InvalidUsernameException: dilempar kalau nama pemain tidak sesuai aturan
// Owner: Satrio Ananda Widiyanto
// Materi: exception
// [MATERI: Exception - custom exception]
// checked exception, jadi pemanggil wajib try/catch atau throws
public class InvalidUsernameException extends Exception {

    public InvalidUsernameException(String message) {
        super(message);
    }
}
