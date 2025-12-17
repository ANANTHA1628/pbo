package backend;

import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Class KaryawanBackend
 * Deskripsi: Menangani operasi CRUD untuk tabel Master Karyawan.
 * Termasuk fitur tambahan kolom 'Keahlian'.
 */
public class KaryawanBackend {

    // --- 1. INSERT (Simpan) ---
    // Menambahkan data karyawan baru termasuk keahlian.
    public boolean simpanKaryawan(String nama, String alamat, String kontak, String keahlian) {
        try {
            Connection c = Koneksi.getKoneksi();
            String sql = "INSERT INTO karyawan (nama, alamat, kontak, keahlian) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, nama);
            ps.setString(2, alamat);
            ps.setString(3, kontak);
            ps.setString(4, keahlian);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Backend Simpan: " + e.getMessage());
            return false;
        }
    }

    // --- 2. UPDATE (Ubah) ---
    // Mengubah data karyawan yang sudah ada.
    public boolean ubahKaryawan(int id, String nama, String alamat, String kontak, String keahlian) {
        try {
            Connection c = Koneksi.getKoneksi();
            String sql = "UPDATE karyawan SET nama=?, alamat=?, kontak=?, keahlian=? WHERE id=?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, nama);
            ps.setString(2, alamat);
            ps.setString(3, kontak);
            ps.setString(4, keahlian);
            ps.setInt(5, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Backend Ubah: " + e.getMessage());
            return false;
        }
    }

    // --- 3. DELETE (Hapus) ---
    // Menghapus data karyawan berdasarkan ID.
    public boolean hapusKaryawan(int id) {
        try {
            Connection c = Koneksi.getKoneksi();
            String sql = "DELETE FROM karyawan WHERE id=?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Backend Hapus: " + e.getMessage());
            return false;
        }
    }

    // --- 4. READ & SEARCH (Ambil Data) ---
    // Mengambil semua data atau mencari berdasarkan nama.
    public ArrayList<Object[]> getListKaryawan(String keyword) {
        ArrayList<Object[]> dataList = new ArrayList<>();
        try {
            Connection c = Koneksi.getKoneksi();
            String sql;
            PreparedStatement ps;

            if (keyword.isEmpty()) {
                sql = "SELECT * FROM karyawan ORDER BY id ASC";
                ps = c.prepareStatement(sql);
            } else {
                // Pencarian ILIKE (Case Insensitive)
                sql = "SELECT * FROM karyawan WHERE nama ILIKE ? ORDER BY id ASC";
                ps = c.prepareStatement(sql);
                ps.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] baris = {
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("alamat"),
                        rs.getString("kontak"),
                        rs.getString("keahlian")
                };
                dataList.add(baris);
            }
        } catch (Exception e) {
            System.out.println("Error Backend Read: " + e.getMessage());
        }
        return dataList;
    }
}