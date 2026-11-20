package data;

import java.util.List;

/*
 * DataStore<T>: interface generik untuk "tempat menyimpan data". T adalah tipe datanya.
 * Contoh: LeaderboardStore implements DataStore<ScoreEntry>, jadi di sana T = ScoreEntry.
 * Owner: Person B
 * Materi: Generics, Abstraction (interface), Collections (List)
 */
// [MATERI: Generics - generic interface]
public interface DataStore<T> {

    // Membaca semua data dari penyimpanan.
    List<T> loadAll();

    // Menulis semua data ke penyimpanan (menimpa isi lama).
    void saveAll(List<T> items);
}
