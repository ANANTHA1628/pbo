package backend;

import java.sql.*;
import java.util.ArrayList;

/**
 * Class VenueBackend
 * Deskripsi: Menangani operasi Insert dan Read untuk data Venue.
 * Venue adalah tempat pelaksanaan event dengan kapasitas tertentu.
 */
public class VenueBackend {

    // --- 1. SIMPAN DATA (INSERT) ---
    // Menambahkan venue baru ke database.
    public boolean simpanVenue(String nama, String alamat, int kapasitas) {
        try {
            Connection c = Koneksi.getKoneksi();
            String sql = "INSERT INTO venue (nama_venue, alamat, kapasitas) VALUES (?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, nama);
            ps.setString(2, alamat);
            ps.setInt(3, kapasitas);
            ps.executeUpdate();
            return true; // Berhasil
        } catch (Exception e) {
            System.out.println("Error Backend Simpan: " + e.getMessage());
            return false; // Gagal
        }
    }

    // --- 2. AMBIL DATA (READ) ---
    // Mengambil semua data venue untuk ditampilkan di tabel FrmVenue atau ComboBox.
    public ArrayList<Object[]> getVenueList() {
        ArrayList<Object[]> data = new ArrayList<>();
        try {
            Connection c = Koneksi.getKoneksi();
            String sql = "SELECT * FROM venue ORDER BY id ASC";
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                data.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("nama_venue"),
                        rs.getString("alamat"),
                        rs.getInt("kapasitas")
                });
            }
        } catch (Exception e) {
            System.out.println("Error Backend Read: " + e.getMessage());
        }
        return data;
    }
}