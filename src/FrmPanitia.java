import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import backend.Koneksi;

import java.awt.*;
import java.sql.*;

public class FrmPanitia extends JFrame {
    JComboBox<ComboItem> cmbEvent, cmbKaryawan;
    JTextField txtJabatan;
    DefaultTableModel model;
    JTable table;

    public FrmPanitia() {
        setTitle("Form Panitia"); setSize(600, 500);
        setLocationRelativeTo(null); setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(4, 2));
        p.add(new JLabel("Event:")); cmbEvent = new JComboBox<>(); p.add(cmbEvent);
        p.add(new JLabel("Karyawan:")); cmbKaryawan = new JComboBox<>(); p.add(cmbKaryawan);
        p.add(new JLabel("Jabatan:")); txtJabatan = new JTextField(); p.add(txtJabatan);
        JButton btn = new JButton("Simpan Panitia"); p.add(new JLabel("")); p.add(btn);
        add(p, BorderLayout.NORTH);

        loadCombos();
        btn.addActionListener(e -> simpan());

        model = new DefaultTableModel(new String[]{"Event", "Panitia", "Jabatan"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    void loadCombos() {
        try {
            Statement s = Koneksi.getKoneksi().createStatement();
            ResultSet rs1 = s.executeQuery("SELECT id, nama_event FROM event");
            while(rs1.next()) cmbEvent.addItem(new ComboItem(rs1.getString("nama_event"), rs1.getInt("id")));
            ResultSet rs2 = s.executeQuery("SELECT id, nama FROM karyawan");
            while(rs2.next()) cmbKaryawan.addItem(new ComboItem(rs2.getString("nama"), rs2.getInt("id")));
        } catch (Exception e) {}
    }

    void simpan() {
        try {
            Connection c = Koneksi.getKoneksi();
            PreparedStatement ps = c.prepareStatement("INSERT INTO panitia (event_id, karyawan_id, jabatan) VALUES (?, ?, ?)");
            ps.setInt(1, ((ComboItem) cmbEvent.getSelectedItem()).getValue());
            ps.setInt(2, ((ComboItem) cmbKaryawan.getSelectedItem()).getValue());
            ps.setString(3, txtJabatan.getText());
            ps.executeUpdate();
            loadData();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    void loadData() {
        model.setRowCount(0);
        try {
            String sql = "SELECT e.nama_event, k.nama, p.jabatan FROM panitia p JOIN event e ON p.event_id = e.id JOIN karyawan k ON p.karyawan_id = k.id";
            ResultSet rs = Koneksi.getKoneksi().createStatement().executeQuery(sql);
            while(rs.next()) model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3)});
        } catch (Exception e) {}
    }
}