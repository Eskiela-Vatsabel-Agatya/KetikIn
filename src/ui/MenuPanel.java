package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/*
 * MenuPanel: layar pertama. Pemain mengisi nama, memilih mode permainan, atau membuka
 * Leaderboard / keluar.
 * Owner: Person C
 * Materi: GUI (layout, JTextField, JButton, event handling, JOptionPane), Exception, String
 */
public class MenuPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField nameField;

    public MenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // SEMENTARA (Phase 1): hanya label besar + satu tombol untuk mencoba CardLayout.
        // Diganti dengan menu sungguhan di Phase 3.
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        JLabel titleLabel = new JLabel("Menu", SwingConstants.CENTER);
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT);
        add(titleLabel, BorderLayout.CENTER);

        JButton nextButton = new JButton("Coba CardLayout: ke layar Game");
        // [MATERI: GUI - event handling] anonymous inner class, seperti di materi Week 13.
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showScreen(MainFrame.GAME);
            }
        });
        add(nextButton, BorderLayout.SOUTH);
    }

    // Dipanggil saat tombol mode diklik: cek nama dulu, lalu mulai permainan.
    private void startMode(String modeName) {
        // TODO Phase 3: Player.validate(nameField.getText()) dalam try/catch, lalu mainFrame.startGame(...).
    }

    // Tombol Keluar: tanya konfirmasi dulu, baru System.exit(0).
    private void confirmExit() {
        // TODO Phase 3
    }
}
