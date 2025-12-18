package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class JadwalBackend
 * Deskripsi: Menangani operasi data untuk Tabel Jadwal (Rundown Acara).
 */
public class JadwalBackend {

    // Inner Class untuk merepresentasikan data data tabel jadwal (Data Transfer
    // Object)
    public static class Jadwal {
        public int id;
        public int event_id;
        public int panitia_id;
        public String nama_event;
        public String nama_agenda;
        public String nama_karyawan;
        public String jabatan;
        public String waktu_mulai;
        public String waktu_selesai;

        public Jadwal(int id, int event_id, int panitia_id, String nama_event, String nama_agenda,
                String nama_karyawan, String jabatan, String waktu_mulai, String waktu_selesai) {
            this.id = id;
            this.event_id = event_id;
            this.panitia_id = panitia_id;
            this.nama_event = nama_event;
            this.nama_agenda = nama_agenda;
            this.nama_karyawan = nama_karyawan;
            this.jabatan = jabatan;
            this.waktu_mulai = waktu_mulai;
            this.waktu_selesai = waktu_selesai;
        }
    }

    // Inner Class helper untuk item ComboBox Event
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

    // Inner Class helper untuk item ComboBox Panitia
    public static class PanitiaItem {
        public int id;
        public String nama_karyawan;
        public String jabatan;

        public PanitiaItem(int id, String nama_karyawan, String jabatan) {
            this.id = id;
            this.nama_karyawan = nama_karyawan;
            this.jabatan = jabatan;
        }

        @Override
        public String toString() {
            return nama_karyawan + " (" + jabatan + ")";
        }
    }

    // Mendapatkan daftar semua event untuk ComboBox
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

    // Mengambil semua data jadwal dengan join ke tabel event dan panitia
    public List<Jadwal> getAllJadwal() throws Exception {
        List<Jadwal> list = new ArrayList<>();
        try {
            String sql = "SELECT j.id, j.event_id, j.panitia_id, e.nama_event, j.nama_agenda, k.nama, p.jabatan, j.waktu_mulai, j.waktu_selesai "
                    + "FROM jadwal j JOIN event e ON j.event_id = e.id "
                    + "JOIN panitia p ON j.panitia_id = p.id "
                    + "JOIN karyawan k ON p.karyawan_id = k.id ORDER BY j.waktu_mulai ASC";
            ResultSet rs = Koneksi.getKoneksi().createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(new Jadwal(rs.getInt("id"), rs.getInt("event_id"), rs.getInt("panitia_id"),
                        rs.getString("nama_event"), rs.getString("nama_agenda"),
                        rs.getString("nama"), rs.getString("jabatan"),
                        rs.getString("waktu_mulai"), rs.getString("waktu_selesai")));
            }
        } catch (Exception e) {
            throw new Exception("Error loading data: " + e.getMessage());
        }
        return list;
    }

    // Mencari jadwal berdasarkan keyword (Nama Event, Agenda, atau Nama Karyawan)
    public List<Jadwal> searchJadwal(String keyword) throws Exception {
        List<Jadwal> list = new ArrayList<>();
        try {
            String sql = "SELECT j.id, j.event_id, j.panitia_id, e.nama_event, j.nama_agenda, k.nama, p.jabatan, j.waktu_mulai, j.waktu_selesai "
                    + "FROM jadwal j JOIN event e ON j.event_id = e.id "
                    + "JOIN panitia p ON j.panitia_id = p.id "
                    + "JOIN karyawan k ON p.karyawan_id = k.id "
                    + "WHERE LOWER(e.nama_event) LIKE LOWER(?) OR LOWER(j.nama_agenda) LIKE LOWER(?) OR LOWER(k.nama) LIKE LOWER(?) "
                    + "ORDER BY j.waktu_mulai ASC";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            String param = "%" + keyword + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Jadwal(rs.getInt("id"), rs.getInt("event_id"), rs.getInt("panitia_id"),
                        rs.getString("nama_event"), rs.getString("nama_agenda"),
                        rs.getString("nama"), rs.getString("jabatan"),
                        rs.getString("waktu_mulai"), rs.getString("waktu_selesai")));
            }
        } catch (Exception e) {
            throw new Exception("Error searching: " + e.getMessage());
        }
        return list;
    }

    // Filter jadwal berdasarkan event_id
    public List<Jadwal> getJadwalByEventId(int eventId) throws Exception {
        List<Jadwal> list = new ArrayList<>();
        try {
            String sql = "SELECT j.id, j.event_id, j.panitia_id, e.nama_event, j.nama_agenda, k.nama, p.jabatan, j.waktu_mulai, j.waktu_selesai "
                    + "FROM jadwal j JOIN event e ON j.event_id = e.id "
                    + "JOIN panitia p ON j.panitia_id = p.id "
                    + "JOIN karyawan k ON p.karyawan_id = k.id "
                    + "WHERE j.event_id = ? ORDER BY j.waktu_mulai ASC";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Jadwal(rs.getInt("id"), rs.getInt("event_id"), rs.getInt("panitia_id"),
                        rs.getString("nama_event"), rs.getString("nama_agenda"),
                        rs.getString("nama"), rs.getString("jabatan"),
                        rs.getString("waktu_mulai"), rs.getString("waktu_selesai")));
            }
        } catch (Exception e) {
            throw new Exception("Error filtering by event: " + e.getMessage());
        }
        return list;
    }

    // Menambahkan jadwal baru
    public void insertJadwal(int eventId, int panitiaId, String namaAgenda,
            String waktuMulai, String waktuSelesai) throws Exception {
        try {
            String sql = "INSERT INTO jadwal (event_id, panitia_id, nama_agenda, waktu_mulai, waktu_selesai) VALUES (?, ?, ?, CAST(? AS TIME), CAST(? AS TIME))";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setInt(2, panitiaId);
            ps.setString(3, namaAgenda);
            ps.setString(4, waktuMulai);
            ps.setString(5, waktuSelesai);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Error inserting: " + e.getMessage());
        }
    }

    // Memperbarui jadwal
    public void updateJadwal(int id, int eventId, int panitiaId, String namaAgenda,
            String waktuMulai, String waktuSelesai) throws Exception {
        try {
            String sql = "UPDATE jadwal SET event_id=?, panitia_id=?, nama_agenda=?, waktu_mulai=CAST(? AS TIME), waktu_selesai=CAST(? AS TIME) WHERE id=?";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setInt(2, panitiaId);
            ps.setString(3, namaAgenda);
            ps.setString(4, waktuMulai);
            ps.setString(5, waktuSelesai);
            ps.setInt(6, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Error updating: " + e.getMessage());
        }
    }

    // Menghapus jadwal
    public void deleteJadwal(int id) throws Exception {
        try {
            String sql = "DELETE FROM jadwal WHERE id=?";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Error deleting: " + e.getMessage());
        }
    }

    // Mengambil satu data jadwal (untuk ditampilkan di form saat edit)
    public Jadwal getJadwalById(int id) throws Exception {
        try {
            String sql = "SELECT j.id, j.event_id, j.panitia_id, e.nama_event, j.nama_agenda, k.nama, p.jabatan, j.waktu_mulai, j.waktu_selesai "
                    + "FROM jadwal j JOIN event e ON j.event_id = e.id "
                    + "JOIN panitia p ON j.panitia_id = p.id "
                    + "JOIN karyawan k ON p.karyawan_id = k.id WHERE j.id=?";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Jadwal(rs.getInt("id"), rs.getInt("event_id"), rs.getInt("panitia_id"),
                        rs.getString("nama_event"), rs.getString("nama_agenda"),
                        rs.getString("nama"), rs.getString("jabatan"),
                        rs.getString("waktu_mulai"), rs.getString("waktu_selesai"));
            }
        } catch (Exception e) {
            throw new Exception("Error getting data: " + e.getMessage());
        }
        return null;
    }

    // Mengambil panitia berdasarkan event_id untuk ComboBox
    public List<PanitiaItem> getPanitiaByEventId(int eventId) throws Exception {
        List<PanitiaItem> list = new ArrayList<>();
        try {
            String sql = "SELECT p.id, k.nama, p.jabatan FROM panitia p "
                    + "JOIN karyawan k ON p.karyawan_id = k.id WHERE p.event_id = ? ORDER BY p.id ASC";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new PanitiaItem(rs.getInt("id"), rs.getString("nama"), rs.getString("jabatan")));
            }
        } catch (Exception e) {
            throw new Exception("Error loading panitia: " + e.getMessage());
        }
        return list;
    }
}