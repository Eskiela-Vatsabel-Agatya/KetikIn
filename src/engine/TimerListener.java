package engine;

// TimerListener: kontrak untuk kelas yang mau dikabari tiap timer berdetak
// Owner: Eskiela Vatsabel Agatya
// Materi: abstraction, multithreading
// [MATERI: Abstraction - interface]
public interface TimerListener {

    // dipanggil dari thread timer kira-kira tiap 100 ms
    void onTick(long elapsedMillis);
}
