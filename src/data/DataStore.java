package data;

import java.util.List;

// DataStore: interface generik untuk tempat simpan data
// Owner: Satrio Ananda Widiyanto
// Materi: generics, abstraction, collections
// [MATERI: Generics - generic interface]
public interface DataStore<T> {

    List<T> loadAll();

    // true kalau berhasil, biar UI bisa kasih tahu pemain kalau gagal
    boolean saveAll(List<T> items);
}
