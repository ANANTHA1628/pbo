package backend;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class PesertaBackend {

    public boolean simpanPesertaManual(String nama, String asal, String noHp, int eventId) {
        Connection c = null;
        try {
            c = Koneksi.getKoneksi();
            c.setAutoCommit(false);

            PreparedStatement ps1 = c.prepareStatement("INSERT INTO peserta (nama_peserta, asal, no_hp) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, nama);
            ps1.setString(2, asal);
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
                    String asal = data[1].trim();
                    String noHp = data[2].trim();

                    PreparedStatement ps1 = c.prepareStatement("INSERT INTO peserta (nama_peserta, asal, no_hp) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    ps1.setString(1, nama);
                    ps1.setString(2, asal);
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
}
