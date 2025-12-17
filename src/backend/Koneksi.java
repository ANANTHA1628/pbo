package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Class Koneksi
 * Deskripsi: Mengatur koneksi ke database PostgreSQL.
 * Menggunakan Pola Singleton agar hanya ada satu instance koneksi.
 */
public class Koneksi {
    private static Connection koneksi;

    public static Connection getKoneksi() {
        if (koneksi == null) {
            try {
                // Konfigurasi Database
                String url = "jdbc:postgresql://localhost:5433/PBO_Event"; // Pastikan PORT sesuai
                String user = "USER"; // Username database
                String password = "Nada140125@"; // Password database

                // Load Driver PostgreSQL
                Class.forName("org.postgresql.Driver");

                // Buat Koneksi
                koneksi = DriverManager.getConnection(url, user, password);

            } catch (ClassNotFoundException | SQLException e) {
                JOptionPane.showMessageDialog(null, "Gagal Koneksi Database: " + e.getMessage());
            }
        }
        return koneksi;
    }
}