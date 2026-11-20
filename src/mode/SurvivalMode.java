package mode;

import data.WordBank;

/*
 * SurvivalMode: satu kata setiap kali, dengan batas waktu per kata (mulai 5 detik).
 * 3 nyawa. Salah ketik atau kehabisan waktu = nyawa berkurang satu.
 * Setiap kata benar membuat batas waktu 0.1 detik lebih pendek (paling cepat 1.5 detik).
 * Permainan selesai ketika nyawa habis. Skor = kata benar x 15 + jumlah huruf benar.
 * Owner: Person A
 * Materi: Inheritance (extends GameMode), Abstraction (implements Timed),
 *         Polymorphism runtime (@Override), Assertion
 */
// [MATERI: Inheritance] + [MATERI: Abstraction - interface]
public class SurvivalMode extends GameMode implements Timed {

    public static final int START_LIVES = 3;
    public static final long START_WORD_TIME_MILLIS = 5000;
    public static final long MIN_WORD_TIME_MILLIS = 1500;
    // Batas waktu berkurang sebanyak ini setiap kata benar.
    public static final long TIME_STEP_MILLIS = 100;
    public static final int POINTS_PER_WORD = 15;

    // Simbol hati (kode Unicode 2665) untuk menampilkan nyawa.
    private static final String HEART = "♥";

    private int lives;
    private int wordsSurvived;
    private String currentWord;
    // Batas waktu untuk kata sekarang (makin lama makin pendek).
    private long wordTimeLimitMillis;
    // Jam komputer (System.currentTimeMillis) saat kata sekarang muncul.
    // Survival menghitung waktu per KATA, jadi mode ini mencatat sendiri kapan kata dimulai.
    private long wordStartMillis;

    public SurvivalMode(WordBank wordBank) {
        super(MODE_SURVIVAL, wordBank);
        lives = START_LIVES;
        wordsSurvived = 0;
        wordTimeLimitMillis = START_WORD_TIME_MILLIS;
        nextWord();
    }

    // Mengambil kata baru dan memulai hitungan waktunya dari nol.
    private void nextWord() {
        currentWord = wordBank.randomWord();
        wordStartMillis = System.currentTimeMillis();
    }

    private void loseLife() {
        lives--;
        // [MATERI: Assertion] asumsi internal: nyawa tidak pernah negatif, karena GamePanel
        // berhenti memanggil mode ini begitu isOver() bernilai true.
        assert lives >= 0 : "Nyawa tidak boleh negatif: " + lives;
    }

    // Batas waktu berikutnya sedikit lebih pendek, tapi tidak kurang dari MIN_WORD_TIME_MILLIS.
    private void shortenTimeLimit() {
        wordTimeLimitMillis = wordTimeLimitMillis - TIME_STEP_MILLIS;
        if (wordTimeLimitMillis < MIN_WORD_TIME_MILLIS) {
            wordTimeLimitMillis = MIN_WORD_TIME_MILLIS;
        }
    }

    @Override
    public String getDescription() {
        return "Ketik kata sebelum waktunya habis. Spasi atau Enter untuk kirim. Salah/telat = nyawa -1.";
    }

    @Override
    public String getCurrentTarget() {
        return currentWord;
    }

    @Override
    public boolean isWordMode() {
        return true;
    }

    // Contoh: 2 nyawa -> "Nyawa: ♥ ♥"
    @Override
    public String getStatusText() {
        StringBuilder builder = new StringBuilder("Nyawa:");
        for (int i = 0; i < lives; i++) {
            builder.append(" ").append(HEART);
        }
        return builder.toString();
    }

    @Override
    public boolean submit(String typed) {
        boolean correct = typed.equals(currentWord);
        if (correct) {
            wordsSurvived++;
            shortenTimeLimit();
        } else {
            loseLife();
        }
        nextWord();
        return correct;
    }

    @Override
    public boolean isOver() {
        return lives <= 0;
    }

    @Override
    public int calculateScore() {
        return wordsSurvived * POINTS_PER_WORD + stats.getCorrectKeystrokes();
    }

    @Override
    public long getTimeLimitMillis() {
        return wordTimeLimitMillis;
    }

    // Sisa waktu untuk KATA SEKARANG. elapsedMillis (waktu seluruh ronde) tidak dipakai di sini,
    // karena yang penting adalah berapa lama sejak kata ini muncul.
    @Override
    public long getTimeLeftMillis(long elapsedMillis) {
        long wordElapsedMillis = System.currentTimeMillis() - wordStartMillis;
        return wordTimeLimitMillis - wordElapsedMillis;
    }

    // Waktu kata ini habis: nyawa berkurang dan kata baru muncul.
    @Override
    public void onTimeUp() {
        loseLife();
        nextWord();
    }

    public int getLives() {
        return lives;
    }

    public int getWordsSurvived() {
        return wordsSurvived;
    }
}
