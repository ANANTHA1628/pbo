package frontend;

import java.awt.*;
import javax.swing.*;

/**
 * Class MainFrame
 * Deskripsi: Halaman Utama (Dashboard) aplikasi.
 * Berisi menu navigasi berupa tombol-tombol untuk mengakses form lainnya.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        // Konfigurasi dasar Frame
        setTitle("Dashboard Admin Event");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(0, 3, 15, 15)); // Grid Auto Baris, 3 Kolom

        // --- Membuat Tombol Menu ---
        JButton btnKaryawan = new JButton("1. FrmKaryawan (Master)");
        JButton btnVenue = new JButton("2. FrmVenue (Master)");
        JButton btnEvent = new JButton("3. FrmEvent (Utama)");
        JButton btnJadwal = new JButton("4. FrmJadwal");
        JButton btnPanitia = new JButton("5. FrmPanitia");
        JButton btnPeserta = new JButton("6. FrmPeserta");
        JButton btnRekap = new JButton("7. FrmRekap");

        // --- Event Listener (Navigasi) ---
        // Saat tombol diklik, buka form yang bersangkutan dan set Visible
        btnKaryawan.addActionListener(e -> new FrmKaryawan().setVisible(true));
        btnVenue.addActionListener(e -> new FrmVenue().setVisible(true));
        btnEvent.addActionListener(e -> new FrmEvent().setVisible(true));
        btnJadwal.addActionListener(e -> new FrmJadwal().setVisible(true));
        btnPanitia.addActionListener(e -> new FrmPanitia().setVisible(true));
        btnPeserta.addActionListener(e -> new FrmPeserta().setVisible(true));
        btnRekap.addActionListener(e -> new FrmRekap().setVisible(true));

        // --- Menambahkan Tombol ke dalam Frame ---
        add(btnKaryawan);
        add(btnVenue);
        add(btnEvent);
        add(btnJadwal);
        add(btnPanitia);
        add(btnPeserta);
        add(btnRekap);
    }

    // Method Main untuk menjalankan aplikasi pertama kali
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}