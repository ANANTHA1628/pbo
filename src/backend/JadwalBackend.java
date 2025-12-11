package backend;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JadwalBackend {
    
    public static class Jadwal {
        public int id;
        public int event_id;
        public String nama_event;
        public String nama_agenda;
        public String pengisi_acara;
        public String waktu_mulai;
        public String waktu_selesai;
        
        public Jadwal(int id, int event_id, String nama_event, String nama_agenda, 
                     String pengisi_acara, String waktu_mulai, String waktu_selesai) {
            this.id = id;
            this.event_id = event_id;
            this.nama_event = nama_event;
            this.nama_agenda = nama_agenda;
            this.pengisi_acara = pengisi_acara;
            this.waktu_mulai = waktu_mulai;
            this.waktu_selesai = waktu_selesai;
        }
    }
    
    public static class EventItem {
        public int id;
        public String nama;
        
        public EventItem(int id, String nama) {
            this.id = id;
            this.nama = nama;
        }
        
        @Override
        public String toString() { return nama; }
    }
    
    public List<EventItem> getAllEvent() throws Exception {
        List<EventItem> list = new ArrayList<>();
        try {
            ResultSet rs = Koneksi.getKoneksi().createStatement()
                .executeQuery("SELECT id, nama_event FROM event");
            while(rs.next()) {
                list.add(new EventItem(rs.getInt("id"), rs.getString("nama_event")));
            }
        } catch (Exception e) {
            throw new Exception("Error loading events: " + e.getMessage());
        }
        return list;
    }
    
    public List<Jadwal> getAllJadwal() throws Exception {
        List<Jadwal> list = new ArrayList<>();
        try {
            String sql = "SELECT j.id, j.event_id, e.nama_event, j.nama_agenda, j.pengisi_acara, j.waktu_mulai, j.waktu_selesai " +
                        "FROM jadwal j JOIN event e ON j.event_id = e.id ORDER BY j.id DESC";
            ResultSet rs = Koneksi.getKoneksi().createStatement().executeQuery(sql);
            while(rs.next()) {
                list.add(new Jadwal(rs.getInt("id"), rs.getInt("event_id"), rs.getString("nama_event"),
                        rs.getString("nama_agenda"), rs.getString("pengisi_acara"),
                        rs.getString("waktu_mulai"), rs.getString("waktu_selesai")));
            }
        } catch (Exception e) {
            throw new Exception("Error loading data: " + e.getMessage());
        }
        return list;
    }
    
    public List<Jadwal> searchJadwal(String keyword) throws Exception {
        List<Jadwal> list = new ArrayList<>();
        try {
            String sql = "SELECT j.id, j.event_id, e.nama_event, j.nama_agenda, j.pengisi_acara, j.waktu_mulai, j.waktu_selesai " +
                        "FROM jadwal j JOIN event e ON j.event_id = e.id " +
                        "WHERE LOWER(e.nama_event) LIKE LOWER(?) OR LOWER(j.nama_agenda) LIKE LOWER(?) OR LOWER(j.pengisi_acara) LIKE LOWER(?) " +
                        "ORDER BY j.id DESC";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            String param = "%" + keyword + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new Jadwal(rs.getInt("id"), rs.getInt("event_id"), rs.getString("nama_event"),
                        rs.getString("nama_agenda"), rs.getString("pengisi_acara"),
                        rs.getString("waktu_mulai"), rs.getString("waktu_selesai")));
            }
        } catch (Exception e) {
            throw new Exception("Error searching: " + e.getMessage());
        }
        return list;
    }
    
    public void insertJadwal(int eventId, String namaAgenda, String pengisiAcara, 
                           String waktuMulai, String waktuSelesai) throws Exception {
        try {
            String sql = "INSERT INTO jadwal (event_id, nama_agenda, pengisi_acara, waktu_mulai, waktu_selesai) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setString(2, namaAgenda);
            ps.setString(3, pengisiAcara);
            ps.setString(4, waktuMulai);
            ps.setString(5, waktuSelesai);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Error inserting: " + e.getMessage());
        }
    }
    
    public void updateJadwal(int id, int eventId, String namaAgenda, String pengisiAcara,
                           String waktuMulai, String waktuSelesai) throws Exception {
        try {
            String sql = "UPDATE jadwal SET event_id=?, nama_agenda=?, pengisi_acara=?, waktu_mulai=?, waktu_selesai=? WHERE id=?";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setString(2, namaAgenda);
            ps.setString(3, pengisiAcara);
            ps.setString(4, waktuMulai);
            ps.setString(5, waktuSelesai);
            ps.setInt(6, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Error updating: " + e.getMessage());
        }
    }
    
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
    
    public Jadwal getJadwalById(int id) throws Exception {
        try {
            String sql = "SELECT j.id, j.event_id, e.nama_event, j.nama_agenda, j.pengisi_acara, j.waktu_mulai, j.waktu_selesai " +
                        "FROM jadwal j JOIN event e ON j.event_id = e.id WHERE j.id=?";
            PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return new Jadwal(rs.getInt("id"), rs.getInt("event_id"), rs.getString("nama_event"),
                        rs.getString("nama_agenda"), rs.getString("pengisi_acara"),
                        rs.getString("waktu_mulai"), rs.getString("waktu_selesai"));
            }
        } catch (Exception e) {
            throw new Exception("Error getting data: " + e.getMessage());
        }
        return null;
    }
}