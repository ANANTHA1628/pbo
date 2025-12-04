import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Dashboard Admin Event");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 3, 15, 15)); // Grid 2 Baris, 3 Kolom

        // Membuat Tombol Menu
        JButton btnKaryawan = new JButton("1. FrmKaryawan (Master)");
        JButton btnVenue = new JButton("2. FrmVenue (Master)");
        JButton btnEvent = new JButton("3. FrmEvent (Utama)");
        JButton btnJadwal = new JButton("4. FrmJadwal");
        JButton btnPanitia = new JButton("5. FrmPanitia");
        JButton btnPeserta = new JButton("6. FrmPeserta");

        // Navigasi ke Form Lain (Nama Class SUDAH DISESUAIKAN)
        btnKaryawan.addActionListener(e -> new FrmKaryawan().setVisible(true));
        btnVenue.addActionListener(e -> new FrmVenue().setVisible(true));
        btnEvent.addActionListener(e -> new FrmEvent().setVisible(true));
        btnJadwal.addActionListener(e -> new FrmJadwal().setVisible(true));
        btnPanitia.addActionListener(e -> new FrmPanitia().setVisible(true));
        btnPeserta.addActionListener(e -> new FrmPeserta().setVisible(true));

        // Masukkan tombol ke layar
        add(btnKaryawan); 
        add(btnVenue); 
        add(btnEvent);
        add(btnJadwal); 
        add(btnPanitia); 
        add(btnPeserta);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}