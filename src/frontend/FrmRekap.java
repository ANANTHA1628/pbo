package frontend;

import backend.Koneksi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * Class FrmRekap
 * Deskripsi: Form untuk menampilkan rekapitulasi data Event.
 * Menampilkan jumlah peserta, sisa slot, dan status event.
 * Dilengkapi filter tanggal untuk range waktu tertentu.
 */
public class FrmRekap extends JFrame {

    // --- Komponen GUI ---
    DefaultTableModel model;
    JTable table;
    JButton btnRefresh, btnTutup, btnFilter;
    JSpinner spinMulai, spinAkhir;
    JCheckBox chkFilter;

    public FrmRekap() {
        setTitle("Form Rekapitulasi Event");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel Header & Filter (Utara) ---
        JPanel pNorth = new JPanel(new BorderLayout());

        // 1. Judul Header
        JLabel lblHeader = new JLabel("REKAP DATA EVENT & PARTISIPASI");
        lblHeader.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        pNorth.add(lblHeader, BorderLayout.NORTH);

        // 2. Panel Filter Tanggal
        JPanel pFilter = new JPanel(new FlowLayout());

        pFilter.add(new JLabel("Mulai:"));
        spinMulai = new JSpinner(new SpinnerDateModel());
        spinMulai.setEditor(new JSpinner.DateEditor(spinMulai, "yyyy-MM-dd"));
        pFilter.add(spinMulai);

        pFilter.add(new JLabel("Sampai:"));
        spinAkhir = new JSpinner(new SpinnerDateModel());
        spinAkhir.setEditor(new JSpinner.DateEditor(spinAkhir, "yyyy-MM-dd"));
        pFilter.add(spinAkhir);

        chkFilter = new JCheckBox("Aktifkan Filter");
        pFilter.add(chkFilter);

        btnFilter = new JButton("Cari");
        pFilter.add(btnFilter);

        pNorth.add(pFilter, BorderLayout.SOUTH);
        add(pNorth, BorderLayout.NORTH);

        // --- Tabel Data (Tengah) ---
        String[] columns = { "ID", "Nama Event", "Tanggal", "Venue", "Kapasitas", "Peserta", "Sisa Slot", "Jml Panitia",
                "Status" };
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Tombol Footer (Selatan) ---
        JPanel pBtn = new JPanel();
        btnRefresh = new JButton("Refresh Data");
        btnTutup = new JButton("Tutup");
        pBtn.add(btnRefresh);
        pBtn.add(btnTutup);
        add(pBtn, BorderLayout.SOUTH);

        // --- Event Listeners ---

        // Refresh: matikan filter dan load ulang
        btnRefresh.addActionListener(e -> {
            chkFilter.setSelected(false);
            loadData();
        });

        // Tutup
        btnTutup.addActionListener(e -> dispose());

        // Filter: load data dengan filter aktif
        btnFilter.addActionListener(e -> loadData());

        // Load Initial Data
        loadData();
    }

    // Method Utama untuk Load Data dari DB
    private void loadData() {
        model.setRowCount(0);
        try {
            Connection c = Koneksi.getKoneksi();
            boolean isFilter = chkFilter.isSelected();

            // Konstruksi Query SQL
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT e.id, e.nama_event, e.tanggal, v.nama_venue, v.kapasitas, ");
            sql.append("(SELECT COUNT(*) FROM event_peserta ep WHERE ep.event_id = e.id) AS jum_peserta, ");
            sql.append("(SELECT COUNT(*) FROM panitia p WHERE p.event_id = e.id) AS jum_panitia ");
            sql.append("FROM event e ");
            sql.append("JOIN venue v ON e.venue_id = v.id ");

            // Jika checkbox filter aktif, tambahkan WHERE clause
            if (isFilter) {
                sql.append("WHERE e.tanggal BETWEEN ? AND ? ");
            }

            sql.append("ORDER BY e.tanggal DESC");

            PreparedStatement ps = c.prepareStatement(sql.toString());

            // Set parameter tanggal jika filter aktif
            if (isFilter) {
                java.util.Date d1 = (java.util.Date) spinMulai.getValue();
                java.util.Date d2 = (java.util.Date) spinAkhir.getValue();

                ps.setDate(1, new java.sql.Date(d1.getTime()));
                ps.setDate(2, new java.sql.Date(d2.getTime()));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String namaEvent = rs.getString("nama_event");
                Date tanggal = rs.getDate("tanggal");
                String venue = rs.getString("nama_venue");
                int kapasitas = rs.getInt("kapasitas");
                int peserta = rs.getInt("jum_peserta");
                int panitia = rs.getInt("jum_panitia");

                // Hitung sisa slot
                int sisa = kapasitas - peserta;
                String status = (sisa <= 0) ? "PENUH" : "OPEN";

                model.addRow(new Object[] {
                        id, namaEvent, tanggal, venue, kapasitas, peserta, sisa, panitia, status
                });
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal meload data: " + e.getMessage());
        }
    }
}
