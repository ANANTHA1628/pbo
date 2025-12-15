package backend;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class PesertaBackend {

    public boolean simpanPesertaManual(String nama, String email, String noHp, int eventId) {
        Connection c = null;
        try {
            c = Koneksi.getKoneksi();
            c.setAutoCommit(false);

            PreparedStatement ps1 = c.prepareStatement("INSERT INTO peserta (nama_peserta, email, no_hp) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, nama);
            ps1.setString(2, email);
            ps1.setString(3, noHp);
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            int idPeserta = 0;
            if (rs.next()) idPeserta = rs.getInt(1);


            PreparedStatement ps2 = c.prepareStatement("INSERT INTO event_peserta (event_id, peserta_id) VALUES (?, ?)");
            ps2.setInt(1, eventId);
            ps2.setInt(2, idPeserta);
            ps2.executeUpdate();

            c.commit();
            return true;
        } catch (Exception e) {
            try { if (c != null) c.rollback(); } catch (Exception ex) {}
            JOptionPane.showMessageDialog(null, "Gagal Simpan Manual: " + e.getMessage());
            return false;
        }
    }

    public boolean importPesertaDariCSV(String filePath, int eventId) {
        Connection c = null;
        try {
            c = Koneksi.getKoneksi();
            c.setAutoCommit(false);

            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; 
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 3) {
                    String nama = data[0].trim();
                    String email = data[1].trim();
                    String noHp = data[2].trim();

                    PreparedStatement ps1 = c.prepareStatement("INSERT INTO peserta (nama_peserta, email, no_hp) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    ps1.setString(1, nama);
                    ps1.setString(2, email);
                    ps1.setString(3, noHp);
                    ps1.executeUpdate();

                    ResultSet rs = ps1.getGeneratedKeys();
                    int idPeserta = 0;
                    if (rs.next()) idPeserta = rs.getInt(1);

                    PreparedStatement ps2 = c.prepareStatement("INSERT INTO event_peserta (event_id, peserta_id) VALUES (?, ?)");
                    ps2.setInt(1, eventId);
                    ps2.setInt(2, idPeserta);
                    ps2.executeUpdate();
                }
            }

            br.close();
            c.commit();
            return true;
        } catch (Exception e) {
            try { if (c != null) c.rollback(); } catch (Exception ex) {}
            JOptionPane.showMessageDialog(null, "Gagal Import CSV: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Object[]> getListEvent() {
        ArrayList<Object[]> dataList = new ArrayList<>();
        try {
            Connection c = Koneksi.getKoneksi();
            String sql = "SELECT id, nama_event FROM event";
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] baris = {
                    rs.getInt("id"),
                    rs.getString("nama_event")
                };
                dataList.add(baris);
            }
        } catch (Exception e) {
            System.out.println("Error Load Event: " + e.getMessage());
        }
        return dataList;
    }
    
    public ArrayList<Object[]> getListPeserta(int eventId) {
        ArrayList<Object[]> dataList = new ArrayList<>();
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            c = Koneksi.getKoneksi();
            if (c == null) {
                JOptionPane.showMessageDialog(null, "Tidak dapat terhubung ke database. Periksa koneksi database Anda.", "Error Koneksi", JOptionPane.ERROR_MESSAGE);
                return dataList;
            }
            
            String sql = "SELECT p.id, p.nama_peserta, p.email, p.no_hp, ep.event_id, e.nama_event " +
                        "FROM peserta p " +
                        "JOIN event_peserta ep ON p.id = ep.peserta_id " +
                        "JOIN event e ON ep.event_id = e.id " +
                        "WHERE ep.event_id = ?";
            
            ps = c.prepareStatement(sql);
            ps.setInt(1, eventId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Object[] baris = {
                    rs.getInt("id"),
                    rs.getString("nama_peserta"),  // Diperbaiki dari nombre_peserta ke nama_peserta
                    rs.getString("email"),
                    rs.getString("no_hp"),
                    rs.getInt("event_id"),
                    rs.getString("nama_event")    // Diperbaiki dari nombre_evento ke nama_event
                };
                dataList.add(baris);
            }
        } catch (SQLException e) {
            System.err.println("Error Load Peserta: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Gagal memuat data peserta: " + e.getMessage() + 
                "\nPastikan tabel-tabel sudah ada di database.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) { System.err.println("Error closing ResultSet: " + e.getMessage()); }
            try { if (ps != null) ps.close(); } catch (Exception e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); }
            // Jangan tutup koneksi di sini, biarkan di-manage oleh Koneksi class
        }
        return dataList;
    }
}
