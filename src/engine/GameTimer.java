package engine;

/*
 * GameTimer: thread terpisah yang "berdetak" setiap 100 ms dan memberi tahu listener
 * berapa lama waktu sudah berjalan. Dipakai untuk jam dan WPM live di layar game.
 * Kelas ini sengaja TIDAK memakai Swing; ia berbicara ke UI hanya lewat TimerListener.
 * Owner: Person A
 * Materi: Multithreading (extends Thread, sleep, InterruptedException), Inheritance
 */
// [MATERI: Multithreading - extends Thread]
public class GameTimer extends Thread {

    // Jarak antar detak dalam milidetik.
    private static final long TICK_MILLIS = 100;

    // volatile = setiap thread selalu melihat nilai terbaru dari variabel ini.
    // Diubah oleh thread UI (stopTimer), dibaca oleh thread timer (run).
    private volatile boolean running = true;

    private TimerListener listener;
    private long startTimeMillis;

    public GameTimer(TimerListener listener) {
        this.listener = listener;
    }

    // Isi pekerjaan thread. Dijalankan otomatis setelah start() dipanggil.
    @Override
    public void run() {
        // TODO Phase 4: catat waktu mulai, lalu loop while (running):
        //   sleep(TICK_MILLIS), hitung elapsed, panggil listener.onTick(elapsed).
    }

    // Menghentikan loop di run() dengan aman.
    public void stopTimer() {
        // TODO Phase 4: running = false; lalu interrupt().
    }
}
