package mode;

/*
 * Timed: interface untuk mode yang punya batas waktu (Time Attack dan Survival).
 *
 * Kenapa interface, bukan bagian dari GameMode?
 * - Tidak semua mode punya batas waktu. Classic dan Code tidak punya. Kalau method ini
 *   ditaruh di GameMode, Classic juga terpaksa punya method waktu yang tidak ada artinya.
 * - Sebuah kelas hanya boleh extends SATU kelas, tapi boleh implements BANYAK interface.
 *   Jadi TimeAttackMode bisa "extends GameMode" sekaligus "implements Timed".
 * GamePanel mengecek "mode instanceof Timed" untuk tahu apakah mode ini punya batas waktu.
 *
 * Owner: Person A
 * Materi: Abstraction (interface)
 */
// [MATERI: Abstraction - interface]
public interface Timed {

    // Batas waktu dalam milidetik.
    long getTimeLimitMillis();

    // Sisa waktu dalam milidetik, kalau ronde sudah berjalan elapsedMillis.
    // Setiap mode menghitungnya dengan caranya sendiri:
    // Time Attack = sisa waktu satu ronde, Survival = sisa waktu kata yang sekarang.
    long getTimeLeftMillis(long elapsedMillis);

    // Dipanggil ketika sisa waktu habis.
    void onTimeUp();
}
