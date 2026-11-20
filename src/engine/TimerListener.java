package engine;

/*
 * TimerListener: "kontrak" untuk kelas yang ingin diberi tahu setiap kali timer berdetak.
 * Dengan interface ini, GameTimer tidak perlu tahu apa-apa tentang Swing; ia hanya
 * memanggil onTick(). GamePanel yang mengimplementasikannya.
 * Owner: Person A
 * Materi: Abstraction (interface), Multithreading
 */
// [MATERI: Abstraction - interface]
public interface TimerListener {

    // Dipanggil oleh thread timer kira-kira setiap 100 ms.
    // elapsedMillis = berapa milidetik sudah berlalu sejak timer mulai.
    void onTick(long elapsedMillis);
}
