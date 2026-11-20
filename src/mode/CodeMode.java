package mode;

import java.util.ArrayList;

import data.WordBank;

/*
 * CodeMode: pemain mengetik potongan kode Java baris demi baris (maksimal 5 baris per ronde),
 * lalu menekan Enter untuk mengirim satu baris. Harus sama persis (spasi, huruf besar/kecil, simbol).
 * Owner: Person A
 * Materi: Inheritance (multi-level, super), Polymorphism runtime (@Override), String (trim),
 *         Collections (ArrayList)
 */
// [MATERI: Inheritance - multilevel]
// CodeMode -> ClassicMode -> GameMode: multi-level inheritance. Ia memakai ulang cara Classic
// menghitung skor (dan isOver, getCurrentTarget), dan hanya mengubah TEKS apa yang diketik.
public class CodeMode extends ClassicMode {

    public static final int LINES_PER_ROUND = 5;
    public static final double SCORE_MULTIPLIER = 1.5;

    // [MATERI: Collections - ArrayList] baris-baris kode untuk ronde ini.
    private ArrayList<String> lines;
    // Nomor baris yang sedang diketik (mulai dari 0).
    private int currentLineIndex;

    public CodeMode(WordBank wordBank) {
        // Memanggil constructor protected milik ClassicMode, dengan nama mode "Code".
        super(MODE_CODE, wordBank);
        lines = new ArrayList<>();
        currentLineIndex = 0;
        pickLines();

        // targetText (warisan dari ClassicMode) = baris yang sedang diketik.
        // Karena itu getCurrentTarget() milik ClassicMode bisa dipakai tanpa diubah.
        targetText = lines.get(currentLineIndex);
    }

    // Mengambil satu potongan kode acak, lalu memakai maksimal LINES_PER_ROUND baris pertamanya.
    // [MATERI: String - trim] trim() membuang spasi di awal (indentasi) dan di akhir baris.
    // Indentasi sengaja dibuang supaya pemain tidak perlu mengetik spasi di awal baris.
    // Spasi DI TENGAH baris (misalnya dua spasi sebelum komentar) tetap ada dan harus diketik.
    private void pickLines() {
        ArrayList<String> snippet = wordBank.randomSnippet();
        for (int i = 0; i < snippet.size() && i < LINES_PER_ROUND; i++) {
            lines.add(snippet.get(i).trim());
        }
        // [MATERI: Assertion] asumsi internal: WordBank tidak pernah memberi potongan kode kosong.
        assert !lines.isEmpty() : "Potongan kode tidak boleh kosong";
    }

    @Override
    public String getDescription() {
        return "Ketik kode Java baris demi baris, persis sama. Tekan Enter untuk kirim satu baris.";
    }

    // Pemain menekan Enter: bandingkan baris yang diketik, lalu pindah ke baris berikutnya.
    // Baris yang salah tetap dilewati (seperti Classic yang tetap selesai walaupun ada salah);
    // kesalahannya sudah tercatat di akurasi.
    @Override
    public boolean submit(String typed) {
        boolean correct = typed.equals(targetText);
        currentLineIndex++;
        if (currentLineIndex >= lines.size()) {
            // Semua baris sudah dikirim: ronde selesai. (isOver() milik ClassicMode membaca field ini.)
            finished = true;
        } else {
            targetText = lines.get(currentLineIndex);
        }
        return correct;
    }

    // Enter yang mengirim baris, bukan panjang ketikan (lihat GamePanel.handleEnter).
    @Override
    public boolean isLineMode() {
        return true;
    }

    // Contoh: "Baris 2/5"
    @Override
    public String getStatusText() {
        int shownLineNumber = currentLineIndex + 1;
        // Setelah baris terakhir dikirim, jangan tampilkan "Baris 6/5".
        if (shownLineNumber > lines.size()) {
            shownLineNumber = lines.size();
        }
        return "Baris " + shownLineNumber + "/" + lines.size();
    }

    // Kode lebih sulit diketik daripada kata biasa, jadi skornya dikali 1.5.
    // super.calculateScore() = memanggil versi milik kelas induk (ClassicMode) dengan super.
    @Override
    public int calculateScore() {
        int classicScore = super.calculateScore();
        // Math.round menghasilkan long, jadi diubah (cast) ke int.
        return (int) Math.round(classicScore * SCORE_MULTIPLIER);
    }

    public int getLineCount() {
        return lines.size();
    }
}
