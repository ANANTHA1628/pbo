package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class PanitiaBackend
 * Deskripsi: Menangani operasi data untuk Tabel Panitia.
 * Panitia adalah tabel relasi (Many-to-Many) antara Event dan Karyawan,
 * dengan tambahan atribut 'Jabatan'.
 */
public class PanitiaBackend {

    // Data Transfer Object untuk Panitia
    public static class Panitia {
        public int id;
        public int event_id;
        public int karyawan_id;
        public String nama_event;
        public String nama_karyawan;
        public String jabatan;

        public Panitia(int id, int event_id, int karyawan_id, String nama_event,
                String nama_karyawan, String jabatan) {
            this.id = id;
            this.event_id = event_id;
            this.karyawan_id = karyawan_id;
            this.nama_event = nama_event;
            this.nama_karyawan = nama_karyawan;
            this.jabatan = jabatan;
        }
    }

    // Helper untuk ComboBox Event
    public static class EventItem {
        public int id;
        public String nama;

        public EventItem(int id, String nama) {
            this.id = id;
            this.nama = nama;
        }

        @Override
        public String toString() {
            return nama;
        }
    }

    // Helper untuk ComboBox Karyawan
    public static class KaryawanItem {
        public int id;
        public String nama;

        public KaryawanItem(int id, String nama) {
            this.id = id;
            this.nama = nama;
        }

        @Override
        public String toString() {
            return nama;
        }
    }

    // Ambil semua event untuk opsi pilihan
    public List<EventItem> getAllEvent() throws Exception {
        List<EventItem> list = new ArrayList<>();
        try {
            ResultSet rs = Koneksi.getKoneksi().createStatement()
                    .executeQuery("SELECT id, nama_event FROM event");
            while (rs.next()) {
                list.add(new EventItem(rs.getInt("id"), rs.getString("nama_event")));
            }
        } catch (Exception e) {
            throw new Exception("Error loading events: " + e.getMessage());
        }
        return list;
    }

    // Ambil semua karyawan untuk opsi pilihan
    public List<KaryawanItem> getAllKaryawan() throws Exception {
        List<KaryawanItem> list = new ArrayList<>();
        try {
            ResultSet rs = Koneksi.getKoneksi().createStatement()
                    .executeQuery("SELECT id, nama FROM karyawan");
            while (rs.next()) {
                list.add(new KaryawanItem(rs.getInt("id"), rs.getString("nama")));
            }
        } catch (Exception e) {
            throw new Exception("Error loading karyawan: " + e.getMessage());
        }
        return list;
    }

    // Ambil daftar panitia lengkap dengan nama event dan nama karyawan
    public List<Panitia> getAllPanitia() throws Exception {
        List<Panitia> list = new ArrayList<>();
        try {
            String sql = "SELECT p.id, p.event_id, p.karyawan_id, e.nama_event, k.nama, p.jabatan " +
                    "FROM panitia p JOIN event e ON p.event_id = e.id " +
                    "JOIN karyawan k ON p.karyawan_id = k.id ORDER BY p.id DESC";
            ResultSet rs = Koneksi.getKoneksi().createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(new Panitia(rs.getInt("id"), rs.getInt("event_id"), rs.getInt("karyawan_id"),
                        rs.getString("nama_event"), rs.getString("nama"), rs.getString("jabatan")));
            }
        } catch (Exception e) {
            throw new Exception("Error loading panitia: " + e.getMessage());
        }
        return list;
    }

    // Cari panitia berdasarkan keyword
    public List<Panitia> searchPanitia(String keyword) throws Exception {
        List<Panitia> list = new ArrayList<>();
        try {
            String sql = "SELECT p.id, p.event_id, p.karyawan_id, e.nama_event, k.nama, p.jabatan " +
                    "FROM panitia p JOIN event e ON p.event_id = e.id " +
                    "JOIN karyawan k ON p.karyawan_id = k.id " +
                    "WHERE LOWER(e.nama_event) LIKE LOWER(?) OR LOWER(k.nama) LIKE LOWER(?) OR LOWER(p.jabatan) LIKE LOWER(?) "
                    +
                    "ORDER BY p.id DESC";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            String param = "%" + keyword + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Panitia(rs.getInt("id"), rs.getInt("event_id"), rs.getInt("karyawan_id"),
                        rs.getString("nama_event"), rs.getString("nama"), rs.getString("jabatan")));
            }
        } catch (Exception e) {
            throw new Exception("Error searching: " + e.getMessage());
        }
        return list;
    }

    // Tambah Tugas Kepanitiaan
    public void insertPanitia(int eventId, int karyawanId, String jabatan) throws Exception {
        try {
            String sql = "INSERT INTO panitia (event_id, karyawan_id, jabatan) VALUES (?, ?, ?)";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setInt(2, karyawanId);
            ps.setString(3, jabatan);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Error inserting: " + e.getMessage());
        }
    }

    // Ubah Tugas Kepanitiaan
    public void updatePanitia(int id, int eventId, int karyawanId, String jabatan) throws Exception {
        try {
            String sql = "UPDATE panitia SET event_id=?, karyawan_id=?, jabatan=? WHERE id=?";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setInt(2, karyawanId);
            ps.setString(3, jabatan);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Error updating: " + e.getMessage());
        }
    }

    // Hapus Tugas Kepanitiaan
    public void deletePanitia(int id) throws Exception {
        try {
            String sql = "DELETE FROM panitia WHERE id=?";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Error deleting: " + e.getMessage());
        }
    }

    // Ambil detail satu data
    public Panitia getPanitiaById(int id) throws Exception {
        try {
            String sql = "SELECT p.id, p.event_id, p.karyawan_id, e.nama_event, k.nama, p.jabatan " +
                    "FROM panitia p JOIN event e ON p.event_id = e.id " +
                    "JOIN karyawan k ON p.karyawan_id = k.id WHERE p.id=?";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Panitia(rs.getInt("id"), rs.getInt("event_id"), rs.getInt("karyawan_id"),
                        rs.getString("nama_event"), rs.getString("nama"), rs.getString("jabatan"));
            }
        } catch (Exception e) {
            throw new Exception("Error getting data: " + e.getMessage());
        }
        return null;
    }
}