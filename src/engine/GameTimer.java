package engine;

/*
 * GameTimer: thread terpisah yang "berdetak" setiap 100 ms dan memberi tahu listener
 * berapa lama waktu sudah berjalan. Dipakai untuk jam dan WPM live di layar game.
 * Kelas ini sengaja TIDAK memakai Swing; ia berbicara ke UI hanya lewat TimerListener.
 * Satu objek GameTimer = satu ronde. Thread hanya bisa di-start() sekali,
 * jadi setiap ronde baru membuat objek GameTimer baru.
 * Owner: Person A
 * Materi: Multithreading (extends Thread, run, sleep, InterruptedException, volatile),
 *         Inheritance (extends Thread), Abstraction (memakai interface TimerListener)
 */
// [MATERI: Multithreading - extends Thread]
// Gaya ini sama seperti contoh pertama di materi Week 14: buat kelas yang extends Thread,
// lalu override method run().
public class GameTimer extends Thread {

    // Jarak antar detak dalam milidetik.
    private static final long TICK_MILLIS = 100;

    // volatile = setiap thread selalu melihat nilai TERBARU dari variabel ini.
    // Flag ini diubah oleh thread UI (stopTimer) dan dibaca oleh thread timer (run).
    // Tanpa volatile, thread timer bisa terus melihat nilai lama (true) dan tidak berhenti.
    private volatile boolean running = true;

    private TimerListener listener;
    private long startTimeMillis;

    public GameTimer(TimerListener listener) {
        this.listener = listener;
    }

    // Isi pekerjaan thread. Dijalankan otomatis (di thread baru) setelah start() dipanggil.
    @Override
    public void run() {
        startTimeMillis = System.currentTimeMillis();

        while (running) {
            try {
                // Tidur 100 ms. Selama tidur, thread ini tidak memakai CPU.
                Thread.sleep(TICK_MILLIS);
            } catch (InterruptedException e) {
                // stopTimer() memanggil interrupt(), yang membangunkan sleep() dengan exception ini.
                // Artinya: kita diminta berhenti. Keluar dari loop; setelah run() selesai, thread selesai.
                break;
            }

            // Bisa saja stopTimer() dipanggil tepat saat sleep selesai; cek lagi sebelum berdetak.
            if (running) {
                long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
                listener.onTick(elapsedMillis);
            }
        }
        // Baris tes: aktifkan untuk melihat kapan thread timer benar-benar berhenti.
        // System.out.println("[GameTimer] thread berhenti");
    }

    // Menghentikan loop di run() dengan aman. Dipanggil dari thread UI (EDT).
    public void stopTimer() {
        running = false;
        // Membangunkan thread kalau sedang sleep, supaya langsung berhenti (tidak menunggu 100 ms).
        interrupt();
    }
}
