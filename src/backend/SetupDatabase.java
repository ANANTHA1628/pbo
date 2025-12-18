package backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class SetupDatabase {
    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(new FileWriter("setup_log.txt"))) {
            Connection c = Koneksi.getKoneksi();
            if (c == null) {
                log.println("Gagal koneksi ke database. Pastikan database PBO_Event ada.");
                return;
            }

            File sqlFile = new File("DB/PBO_Event.sql");
            if (!sqlFile.exists()) {
                sqlFile = new File("e:/Tugas kuliah/pbo/DB/PBO_Event.sql");
            }

            if (!sqlFile.exists()) {
                log.println("File SQL tidak ditemukan: " + sqlFile.getAbsolutePath());
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(sqlFile));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("--"))
                    continue;
                if (line.trim().isEmpty())
                    continue;
                sb.append(line).append("\n");
            }
            br.close();

            String[] queries = sb.toString().split(";");
            Statement stmt = c.createStatement();

            for (String query : queries) {
                String q = query.trim();
                if (!q.isEmpty()) {
                    log.println("Executing: [" + q + "]");
                    try {
                        stmt.execute(q);
                    } catch (Exception e) {
                        log.println("FAILED: " + e.getMessage());
                    }
                }
            }

            stmt.close();

            try {
                Statement check = c.createStatement();
                check.execute("SELECT count(*) FROM karyawan");
                log.println("Verifikasi: Tabel karyawan ditemukan.");
            } catch (Exception e) {
                log.println("Verifikasi Gagal: " + e.getMessage());
            }

            log.println("Setup Database Selesai!");
            // JOptionPane.showMessageDialog(null, "Setup Database Selesai!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
