package backend;

import frontend.ComboItem;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Class EventBackend
 * Deskripsi: Menangani logika bisnis dan operasi database untuk entitas Event.
 * Termasuk Create, Read, Update, Delete (CRUD), dan helper lainnya.
 */
public class EventBackend {

    // --- 1. AMBIL DATA VENUE (Untuk ComboBox) ---
    // Mengambil daftar venue (ID + Nama) untuk mengisi dropdown/combobox di form
    // event.
    public ArrayList<ComboItem> getVenueList() {
        ArrayList<ComboItem> list = new ArrayList<>();
        try {
            Connection c = Koneksi.getKoneksi();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT id, nama_venue FROM venue");
            while (rs.next()) {
                list.add(new ComboItem(rs.getString("nama_venue"), rs.getInt("id")));
            }
        } catch (Exception e) {
            System.out.println("Error Backend Venue: " + e.getMessage());
        }
        return list;
    }

    // --- 2. CEK DUPLIKASI NAMA ---
    // Memastikan nama event tidak kembar saat input data baru atau update.
    public boolean isNamaAda(String nama, int idKecuali) {
        try {
            Connection c = Koneksi.getKoneksi();
            String sql;
            // Jika ID -1, berarti Insert baru (cek semua)
            // Jika ID > 0, berarti Update (cek semua KECUALI yang memiliki ID tersebut)
            if (idKecuali == -1) {
                sql = "SELECT COUNT(*) FROM event WHERE LOWER(nama_event) = LOWER(?)";
            } else {
                sql = "SELECT COUNT(*) FROM event WHERE LOWER(nama_event) = LOWER(?) AND id != ?";
            }

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, nama);
            if (idKecuali != -1)
                ps.setInt(2, idKecuali);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Mengembalikan true jika ada duplikasi
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- 3. SIMPAN BARU (INSERT) ---
    // Menambahkan data event baru ke database.
    public boolean insertEvent(String nama, String tanggal, long poolPrize, long harga, int venueId) {
        try {
            Connection c = Koneksi.getKoneksi();
            String sql = "INSERT INTO event (nama_event, tanggal, pool_prize, harga_registrasi, venue_id) VALUES (?, CAST(? AS DATE), ?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, nama);
            ps.setString(2, tanggal); // Format YYYY-MM-DD
            ps.setLong(3, poolPrize);
            ps.setLong(4, harga);
            ps.setInt(5, venueId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Insert: " + e.getMessage());
            return false;
        }
    }

    // --- 4. UPDATE EVENT ---
    // Memperbarui data event yang sudah ada.
    public boolean updateEvent(int id, String nama, String tanggal, long poolPrize, long harga, int venueId) {
        try {
            Connection c = Koneksi.getKoneksi();
            String sql = "UPDATE event SET nama_event=?, tanggal=CAST(? AS DATE), pool_prize=?, harga_registrasi=?, venue_id=? WHERE id=?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, nama);
            ps.setString(2, tanggal);
            ps.setLong(3, poolPrize);
            ps.setLong(4, harga);
            ps.setInt(5, venueId);
            ps.setInt(6, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Update: " + e.getMessage());
            return false;
        }
    }

    // --- 5. DELETE EVENT ---
    // Menghapus data event berdasarkan ID.
    public boolean deleteEvent(int id) {
        try {
            Connection c = Koneksi.getKoneksi();
            PreparedStatement ps = c.prepareStatement("DELETE FROM event WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Delete: " + e.getMessage());
            return false;
        }
    }

    // --- 6. AMBIL SEMUA DATA (Untuk Tabel) ---
    // Mengambil daftar event, bisa difilter dengan kata kunci pencarian.
    public ArrayList<Object[]> getEventList(String keyword) {
        ArrayList<Object[]> data = new ArrayList<>();
        try {
            Connection c = Koneksi.getKoneksi();
            String sql = "SELECT e.id, e.nama_event, e.tanggal, e.pool_prize, e.harga_registrasi, v.nama_venue " +
                    "FROM event e JOIN venue v ON e.venue_id = v.id ";

            if (!keyword.isEmpty()) {
                sql += "WHERE LOWER(e.nama_event) LIKE LOWER(?) OR LOWER(v.nama_venue) LIKE LOWER(?)";
            }
            // Urutkan berdasarkan tanggal terbaru
            sql += " ORDER BY e.tanggal DESC";

            PreparedStatement ps = c.prepareStatement(sql);
            if (!keyword.isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("nama_event"),
                        rs.getString("tanggal"),
                        rs.getLong("pool_prize"),
                        rs.getLong("harga_registrasi"),
                        rs.getString("nama_venue")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // --- 7. AMBIL SATU DATA (Untuk Edit) ---
    // Mengambil detail satu event berdasarkan ID untuk ditampilkan di form edit.
    // Mengembalikan array Object: [nama, tanggal(String), pool, harga, venueId]
    public Object[] getEventById(int id) {
        try {
            Connection c = Koneksi.getKoneksi();
            String sql = "SELECT nama_event, tanggal, pool_prize, harga_registrasi, venue_id FROM event WHERE id=?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Object[] {
                        rs.getString("nama_event"),
                        rs.getString("tanggal"),
                        rs.getLong("pool_prize"),
                        rs.getLong("harga_registrasi"),
                        rs.getInt("venue_id")
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}