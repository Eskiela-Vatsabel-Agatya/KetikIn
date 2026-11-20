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

    // Nama-nama yang tidak boleh dipakai (dicek tanpa memperhatikan huruf besar/kecil).
    private static final String[] FORBIDDEN_NAMES = {"admin", "null", "guest"};

    // private: kelas lain tidak bisa mengubah nama secara langsung, harus lewat setName().
    private String name;

    public Player(String name) throws InvalidUsernameException {
        // Constructor juga lewat setName, supaya nama dari constructor ikut dicek.
        setName(name);
    }

    public String getName() {
        return name;
    }

    // [MATERI: Encapsulation] setter yang mengecek data dulu sebelum menyimpannya.
    public void setName(String name) throws InvalidUsernameException {
        this.name = validate(name);
    }

    // Mengecek semua aturan nama satu per satu. Kalau valid, mengembalikan nama yang sudah di-trim.
    // Kalau tidak valid, melempar InvalidUsernameException dengan pesan aturan yang dilanggar.
    // static: method ini hanya memakai parameter 'name', tidak memakai field objek,
    // jadi tidak perlu objek Player untuk memanggilnya. Dipakai oleh setName di atas.
    // [MATERI: String] validasi dengan loop + Character, tanpa regex.
    public static String validate(String name) throws InvalidUsernameException {
        // Aturan 1: tidak boleh kosong.
        if (name == null) {
            throw new InvalidUsernameException("Nama tidak boleh kosong.");
        }
        // trim() = buang spasi di awal dan akhir.
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidUsernameException("Nama tidak boleh kosong.");
        }

        // Aturan 2: panjang 3 sampai 15 karakter.
        if (trimmed.length() < MIN_NAME_LENGTH) {
            throw new InvalidUsernameException("Nama minimal " + MIN_NAME_LENGTH + " karakter.");
        }
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new InvalidUsernameException("Nama maksimal " + MAX_NAME_LENGTH + " karakter.");
        }

        // Aturan 3: karakter pertama harus huruf.
        if (!Character.isLetter(trimmed.charAt(0))) {
            throw new InvalidUsernameException("Nama harus diawali huruf.");
        }

        // Aturan 4: hanya huruf, angka, dan garis bawah (_). Dicek satu per satu dengan loop.
        // (Aturan ini juga menjamin nama tidak berisi koma, jadi aman disimpan di file CSV.)
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                throw new InvalidUsernameException(
                        "Nama hanya boleh berisi huruf, angka, dan garis bawah (_).");
            }
        }

        // Aturan 5: bukan nama terlarang.
        for (int i = 0; i < FORBIDDEN_NAMES.length; i++) {
            if (trimmed.equalsIgnoreCase(FORBIDDEN_NAMES[i])) {
                throw new InvalidUsernameException("Nama \"" + trimmed + "\" tidak boleh dipakai.");
            }
        }

        return trimmed;
    }
}
