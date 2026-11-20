package model;

import data.InvalidUsernameException;

/*
 * Player: menyimpan nama pemain. Nama hanya bisa diisi lewat setName, yang selalu
 * mengecek aturan nama dulu, jadi objek Player tidak mungkin berisi nama yang salah.
 * Owner: Person C
 * Materi: Encapsulation (private + getter/setter), String (validasi dengan loop),
 *         Exception (throw/throws)
 */
// [MATERI: Encapsulation]
public class Player {

    public static final int MIN_NAME_LENGTH = 3;
    public static final int MAX_NAME_LENGTH = 15;

    private String name;

    public Player(String name) throws InvalidUsernameException {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidUsernameException {
        // TODO Phase 2: this.name = validate(name);
        this.name = name;
    }

    // Mengecek semua aturan nama. Kalau valid, mengembalikan nama yang sudah di-trim.
    // Kalau tidak valid, melempar InvalidUsernameException dengan pesan aturan yang dilanggar.
    public static String validate(String name) throws InvalidUsernameException {
        // TODO Phase 2: aturan di brief 5.6 (loop + Character.isLetter/isLetterOrDigit, tanpa regex).
        return name;
    }
}
