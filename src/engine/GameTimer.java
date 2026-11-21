package engine;

// GameTimer: thread yang berdetak tiap 100 ms untuk jam dan WPM live
// Owner: Eskiela Vatsabel Agatya
// Materi: multithreading, inheritance, abstraction
// [MATERI: Multithreading - extends Thread]
// satu objek per ronde, karena thread cuma bisa di-start sekali
public class GameTimer extends Thread {

    private static final long TICK_MILLIS = 100;

    // [MATERI: Multithreading - volatile]
    // volatile biar thread timer lihat nilai terbaru dari EDT
    private volatile boolean running = true;

    private TimerListener listener;
    private long startTimeMillis;

    public GameTimer(TimerListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        startTimeMillis = System.currentTimeMillis();

        while (running) {
            try {
                Thread.sleep(TICK_MILLIS);
            } catch (InterruptedException e) {
                // dibangunkan oleh stopTimer(), berarti disuruh berhenti
                break;
            }

            // cek lagi, bisa saja stopTimer() dipanggil pas sleep selesai
            if (running) {
                long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
                listener.onTick(elapsedMillis);
            }
        }
    }

    // dipanggil dari EDT, interrupt biar tidak nunggu sleep selesai
    public void stopTimer() {
        running = false;
        interrupt();
    }
}
