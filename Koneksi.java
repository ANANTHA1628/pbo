import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {
    private static Connection koneksi;

    public static Connection getKoneksi() {
        if (koneksi == null) {
            try {
                String url = "jdbc:postgresql://localhost:5432/PBO_Event";
                String user = "postgres";
                String password = "nanta"; 
                
                Class.forName("org.postgresql.Driver");
                koneksi = DriverManager.getConnection(url, user, password);
            } catch (ClassNotFoundException | SQLException e) {
                JOptionPane.showMessageDialog(null, "Gagal Koneksi Database: " + e.getMessage());
            }
        }
        return koneksi;
    }
}