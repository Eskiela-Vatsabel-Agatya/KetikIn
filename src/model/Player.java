package model;

import data.InvalidUsernameException;

// Player: simpan nama pemain, selalu dicek dulu lewat setName
// Owner: Hanif Rafa Ramidzar
// Materi: encapsulation, string, exception
// [MATERI: Encapsulation]
public class Player {

    public static final int MIN_NAME_LENGTH = 3;
    public static final int MAX_NAME_LENGTH = 15;

    // dicek tanpa peduli huruf besar kecil
    private static final String[] FORBIDDEN_NAMES = {"admin", "null", "guest"};

    private String name;

    // lewat setName biar nama dari constructor ikut dicek
    public Player(String name) throws InvalidUsernameException {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidUsernameException {
        this.name = validate(name);
    }

    // [MATERI: String]
    // cek aturan nama satu per satu, hasilnya nama yang sudah di-trim
    public static String validate(String name) throws InvalidUsernameException {
        // tidak boleh kosong
        if (name == null) {
            throw new InvalidUsernameException("Nama tidak boleh kosong.");
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidUsernameException("Nama tidak boleh kosong.");
        }

        // panjang 3 sampai 15
        if (trimmed.length() < MIN_NAME_LENGTH) {
            throw new InvalidUsernameException("Nama minimal " + MIN_NAME_LENGTH + " karakter.");
        }
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new InvalidUsernameException("Nama maksimal " + MAX_NAME_LENGTH + " karakter.");
        }

        // harus diawali huruf
        if (!Character.isLetter(trimmed.charAt(0))) {
            throw new InvalidUsernameException("Nama harus diawali huruf.");
        }

        // cuma huruf, angka, dan _ (jadi aman juga untuk CSV)
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                throw new InvalidUsernameException(
                        "Nama hanya boleh berisi huruf, angka, dan garis bawah (_).");
            }
        }

        // bukan nama terlarang
        for (int i = 0; i < FORBIDDEN_NAMES.length; i++) {
            if (trimmed.equalsIgnoreCase(FORBIDDEN_NAMES[i])) {
                throw new InvalidUsernameException("Nama \"" + trimmed + "\" tidak boleh dipakai.");
            }
        }

        return trimmed;
    }
}
