package mode;

/*
 * Timed: interface untuk mode yang punya batas waktu (Time Attack dan Survival).
 * TODO Phase 4: komentar kenapa ini interface dan bukan bagian dari GameMode.
 * Owner: Person A
 * Materi: Abstraction (interface)
 */
// [MATERI: Abstraction - interface]
public interface Timed {

    // Batas waktu dalam milidetik.
    long getTimeLimitMillis();

    // Dipanggil ketika waktu habis.
    void onTimeUp();
}
