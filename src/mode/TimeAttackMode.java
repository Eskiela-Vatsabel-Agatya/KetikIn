package mode;

import java.util.ArrayList;

import data.WordBank;

/*
 * TimeAttackMode: ketik kata sebanyak-banyaknya dalam 60 detik. Tekan Spasi (atau Enter)
 * untuk mengirim satu kata. Kata sekarang ditampilkan bersama beberapa kata berikutnya.
 * Skor = kata benar x 10 + jumlah huruf benar.
 * Owner: Person A
 * Materi: Inheritance (extends GameMode), Abstraction (implements Timed),
 *         Polymorphism runtime (@Override), Collections (ArrayList), String (StringBuilder)
 */
// [MATERI: Inheritance] + [MATERI: Abstraction - interface]
public class TimeAttackMode extends GameMode implements Timed {

    public static final long TIME_LIMIT_MILLIS = 60000;
    public static final int POINTS_PER_WORD = 10;
    // Berapa kata berikutnya yang ditampilkan abu-abu.
    private static final int WORDS_SHOWN_AHEAD = 4;
    // Antrean diisi ulang supaya selalu berisi minimal sekian kata.
    private static final int MIN_QUEUE_SIZE = 10;

    // [MATERI: Collections - ArrayList]
    // Antrean kata. Kata pertama (index 0) = kata yang sedang diketik.
    private ArrayList<String> upcomingWords;
    private int correctWords;
    private boolean timeUp;

    public TimeAttackMode(WordBank wordBank) {
        super(MODE_TIME_ATTACK, wordBank);
        upcomingWords = new ArrayList<>();
        correctWords = 0;
        timeUp = false;
        fillQueue();
    }

    // Menambah kata acak sampai antrean berisi minimal MIN_QUEUE_SIZE kata.
    private void fillQueue() {
        while (upcomingWords.size() < MIN_QUEUE_SIZE) {
            upcomingWords.add(wordBank.randomWord());
        }
    }

    @Override
    public String getDescription() {
        return "Ketik kata sebanyak-banyaknya dalam 60 detik. Spasi atau Enter untuk kirim.";
    }

    @Override
    public String getCurrentTarget() {
        return upcomingWords.get(0);
    }

    // Beberapa kata setelah kata sekarang, digabung dengan spasi.
    @Override
    public String getUpcomingText() {
        StringBuilder builder = new StringBuilder();
        // Mulai dari index 1, karena index 0 adalah kata yang sedang diketik.
        for (int i = 1; i <= WORDS_SHOWN_AHEAD; i++) {
            if (i > 1) {
                builder.append(" ");
            }
            builder.append(upcomingWords.get(i));
        }
        return builder.toString();
    }

    @Override
    public boolean isWordMode() {
        return true;
    }

    // Membandingkan kata yang diketik dengan kata sekarang, lalu pindah ke kata berikutnya.
    @Override
    public boolean submit(String typed) {
        boolean correct = typed.equals(getCurrentTarget());
        if (correct) {
            correctWords++;
        }
        // Buang kata pertama, kata kedua otomatis menjadi kata sekarang.
        upcomingWords.remove(0);
        fillQueue();
        return correct;
    }

    @Override
    public boolean isOver() {
        return timeUp;
    }

    @Override
    public int calculateScore() {
        return correctWords * POINTS_PER_WORD + stats.getCorrectKeystrokes();
    }

    @Override
    public long getTimeLimitMillis() {
        return TIME_LIMIT_MILLIS;
    }

    // Satu batas waktu untuk seluruh ronde.
    @Override
    public long getTimeLeftMillis(long elapsedMillis) {
        return TIME_LIMIT_MILLIS - elapsedMillis;
    }

    @Override
    public void onTimeUp() {
        timeUp = true;
    }

    public int getCorrectWords() {
        return correctWords;
    }
}
