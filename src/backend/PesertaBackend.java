package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;


public class PesertaBackend {

    // --- 1. PENDAFTARAN MANUAL ---
    public boolean simpanPesertaManual(String nama, String email, String noHp, int eventId) {
        Connection c = null;
        try {
            c = Koneksi.getKoneksi();
            c.setAutoCommit(false); // Mulai Transaksi

            // A. Insert ke Tabel Peserta
            PreparedStatement ps1 = c.prepareStatement(
                    "INSERT INTO peserta (nama_peserta, email, no_hp) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, nama);
            ps1.setString(2, email);
            ps1.setString(3, noHp);
            ps1.executeUpdate();

            // B. Ambil ID Peserta yang baru dibuat
            ResultSet rs = ps1.getGeneratedKeys();
            int idPeserta = 0;
            if (rs.next())
                idPeserta = rs.getInt(1);

            // C. Insert ke Tabel Relasi (Event - Peserta)
            PreparedStatement ps2 = c
                    .prepareStatement("INSERT INTO event_peserta (event_id, peserta_id) VALUES (?, ?)");
            ps2.setInt(1, eventId);
            ps2.setInt(2, idPeserta);
            ps2.executeUpdate();

            c.commit(); // Simpan Permanen
            return true;
        } catch (Exception e) {
            try {
                if (c != null)
                    c.rollback();
            } catch (Exception ex) {
            } // Batalkan jika error
            JOptionPane.showMessageDialog(null, "Gagal Simpan Manual: " + e.getMessage());
            return false;
        }
    }

    // --- 2. IMPORT DARI CSV ---
    // Membaca file CSV dan memasukkan banyak peserta sekaligus dalam satu
    // transaksi.
    public boolean importPesertaDariCSV(String filePath, int eventId) {
        Connection c = null;
        try {
            c = Koneksi.getKoneksi();
            c.setAutoCommit(false); // Mulai Transaksi Bulk

            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                // Lewati baris header jika ada
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 3) { // Pastikan kolom cukup (Nama, Email, HP)
                    String nama = data[0].trim();
                    String email = data[1].trim();
                    String noHp = data[2].trim();

                    // A. Insert Peserta
                    PreparedStatement ps1 = c.prepareStatement(
                            "INSERT INTO peserta (nama_peserta, email, no_hp) VALUES (?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps1.setString(1, nama);
                    ps1.setString(2, email);
                    ps1.setString(3, noHp);
                    ps1.executeUpdate();

                    // B. Ambil ID
                    ResultSet rs = ps1.getGeneratedKeys();
                    int idPeserta = 0;
                    if (rs.next())
                        idPeserta = rs.getInt(1);

                    // C. Hubungkan ke Event
                    PreparedStatement ps2 = c
                            .prepareStatement("INSERT INTO event_peserta (event_id, peserta_id) VALUES (?, ?)");
                    ps2.setInt(1, eventId);
                    ps2.setInt(2, idPeserta);
                    ps2.executeUpdate();
                }
            }

            br.close();
            c.commit(); // Commit semua data CSV
            return true;
        } catch (Exception e) {
            try {
                if (c != null)
                    c.rollback();
            } catch (Exception ex) {
            }
            JOptionPane.showMessageDialog(null, "Gagal Import CSV: " + e.getMessage());
            return false;
        }
    }

    // Mengambil daftar Event untuk dropdown
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

    // Method untuk mengambil daftar peserta berdasarkan event
    public ArrayList<Object[]> getDaftarPesertaByEvent(int eventId) {
        ArrayList<Object[]> dataList = new ArrayList<>();
        try {
            Connection c = Koneksi.getKoneksi();
            String sql = "SELECT p.id, p.nama_peserta, p.email, p.no_hp " +
                        "FROM peserta p " +
                        "JOIN event_peserta ep ON p.id = ep.peserta_id " +
                        "WHERE ep.event_id = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Object[] baris = {
                    rs.getInt("id"),
                    rs.getString("nama_peserta"),
                    rs.getString("email"),
                    rs.getString("no_hp")
                };
                dataList.add(baris);
            }
        } catch (Exception e) {
            System.out.println("Error Load Peserta: " + e.getMessage());
        }
        return dataList;
    }
    
    // Method untuk menghapus data peserta berdasarkan ID
    public boolean hapusPeserta(int pesertaId) {
        Connection c = null;
        try {
            c = Koneksi.getKoneksi();
            c.setAutoCommit(false); // Mulai transaksi
            
            // Hapus dari tabel event_peserta terlebih dahulu karena ada foreign key constraint
            String sql1 = "DELETE FROM event_peserta WHERE peserta_id = ?";
            PreparedStatement ps1 = c.prepareStatement(sql1);
            ps1.setInt(1, pesertaId);
            ps1.executeUpdate();
            
            // Kemudian hapus dari tabel peserta
            String sql2 = "DELETE FROM peserta WHERE id = ?";
            PreparedStatement ps2 = c.prepareStatement(sql2);
            ps2.setInt(1, pesertaId);
            int affectedRows = ps2.executeUpdate();
            
            c.commit(); // Commit transaksi
            return affectedRows > 0;
            
        } catch (Exception e) {
            try {
                if (c != null) c.rollback(); // Rollback jika terjadi error
            } catch (Exception ex) {
                System.out.println("Error saat rollback: " + ex.getMessage());
            }
            System.out.println("Error Hapus Peserta: " + e.getMessage());
            return false;
        } finally {
            try {
                if (c != null) c.setAutoCommit(true); // Kembalikan ke mode auto-commit
            } catch (Exception e) {
                System.out.println("Error mengembalikan auto-commit: " + e.getMessage());
            }
        }
    }
}
