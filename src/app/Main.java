package app;

import javax.swing.SwingUtilities;

import ui.MainFrame;

// Main: titik awal program, bikin dan tampilkan MainFrame
// Owner: Hanif Rafa Ramidzar
// Materi: GUI, multithreading, interface
public class Main {

    public static void main(String[] args) {
        // look-and-feel sengaja tidak diganti, LAF Windows mengabaikan warna tombol dari Theme

        // [MATERI: Multithreading - EDT]
        // komponen Swing harus dibuat di EDT, jadi lewat invokeLater
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame();
                if (frame.isReady()) {
                    frame.setVisible(true);
                } else {
                    // file kata gagal dimuat, buang jendela biar program berhenti sendiri
                    frame.dispose();
                }
            }
        });
    }
}
