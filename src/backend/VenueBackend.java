package backend;

import java.sql.*;
import java.util.ArrayList;

public class VenueBackend {

    // --- 1. SIMPAN DATA (INSERT) ---
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
    public ArrayList<Object[]> getVenueList() {
        ArrayList<Object[]> data = new ArrayList<>();
        try {
            Connection c = Koneksi.getKoneksi();
            String sql = "SELECT * FROM venue ORDER BY id ASC";
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(sql);
            
            while (rs.next()) {
                data.add(new Object[]{
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