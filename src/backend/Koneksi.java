package backend;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {
    private static final String URL = "jdbc:postgresql://localhost:5432/PBO_Event";
    private static final String USER = "postgres";
    private static final String PASSWORD = "raihan100";
    private static Connection koneksi = null;

    public static Connection getKoneksi() {
        try {
            // Cek apakah koneksi masih valid
            if (koneksi == null || koneksi.isClosed() || !koneksi.isValid(2)) {
                try {
                    Class.forName("org.postgresql.Driver");
                    koneksi = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Koneksi database berhasil dibuat");
                } catch (ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(null, "Driver tidak ditemukan: " + e.getMessage());
                    return null;
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Gagal terhubung ke database: " + e.getMessage() + 
                            "\nPastikan PostgreSQL berjalan dan konfigurasi benar." +
                            "\nURL: " + URL + 
                            "\nUser: " + USER);
                    return null;
                }
            }
            return koneksi;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error memeriksa koneksi: " + e.getMessage());
            return null;
        }
    }
    
    // Method untuk menutup koneksi
    public static void closeConnection() {
        try {
            if (koneksi != null && !koneksi.isClosed()) {
                koneksi.close();
                System.out.println("Koneksi database ditutup");
            }
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi: " + e.getMessage());
        }
    }
}