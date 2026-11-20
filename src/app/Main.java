package app;

import javax.swing.SwingUtilities;

import ui.MainFrame;

/*
 * Main: titik awal program KetikIn. Tugasnya hanya membuat jendela utama (MainFrame)
 * dan menampilkannya.
 * Owner: Person C
 * Materi: GUI (Swing), Multithreading (Event Dispatch Thread), Interface (Runnable)
 */
public class Main {

    public static void main(String[] args) {
        // Look-and-feel: sengaja TIDAK diganti, kita pakai tampilan bawaan Java ("Metal").
        // Look-and-feel Windows menggambar tombol dengan gayanya sendiri dan mengabaikan
        // warna latar dari Theme, sehingga tombol biru kita akan menjadi abu-abu.

        // [MATERI: Multithreading - EDT]
        // Swing tidak thread-safe: komponen Swing hanya boleh dibuat dan diubah oleh
        // satu thread khusus, yaitu Event Dispatch Thread (EDT).
        // Method main() berjalan di thread "main", bukan di EDT. Jadi kita titipkan
        // pekerjaan membuat jendela ke EDT lewat invokeLater.
        // Runnable di sini sama seperti di materi Multithreading: objek yang punya method run().
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame();
                if (frame.isReady()) {
                    frame.setVisible(true);
                } else {
                    // File kata gagal dimuat (pesan error sudah ditampilkan oleh MainFrame).
                    // Jendela dibuang tanpa pernah ditampilkan. Karena tidak ada jendela
                    // yang terbuka lagi, program Java berhenti dengan sendirinya.
                    frame.dispose();
                }
            }
        });
    }
}
