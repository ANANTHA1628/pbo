package frontend;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import backend.Koneksi;

import java.awt.*;
import java.sql.*;

public class FrmJadwal extends JFrame {
    JComboBox<ComboItem> cmbEvent;
    JTextField txtAgenda, txtMulai, txtSelesai;
    DefaultTableModel model;
    JTable table;

    public FrmJadwal() {
        setTitle("Form Jadwal"); setSize(600, 500);
        setLocationRelativeTo(null); setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(5, 2));
        p.add(new JLabel("Pilih Event:")); cmbEvent = new JComboBox<>(); p.add(cmbEvent);
        p.add(new JLabel("Nama Agenda:")); txtAgenda = new JTextField(); p.add(txtAgenda);
        p.add(new JLabel("Jam Mulai:")); txtMulai = new JTextField(); p.add(txtMulai);
        p.add(new JLabel("Jam Selesai:")); txtSelesai = new JTextField(); p.add(txtSelesai);
        JButton btn = new JButton("Simpan Jadwal"); p.add(new JLabel("")); p.add(btn);
        add(p, BorderLayout.NORTH);

        loadEvent();
        btn.addActionListener(e -> simpan());

        model = new DefaultTableModel(new String[]{"Event", "Agenda", "Jam"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    void loadEvent() {
        try {
            ResultSet rs = Koneksi.getKoneksi().createStatement().executeQuery("SELECT id, nama_event FROM event");
            while(rs.next()) cmbEvent.addItem(new ComboItem(rs.getString("nama_event"), rs.getInt("id")));
        } catch (Exception e) {}
    }

    void simpan() {
        try {
            Connection c = Koneksi.getKoneksi();
            PreparedStatement ps = c.prepareStatement("INSERT INTO jadwal (event_id, nama_agenda, waktu_mulai, waktu_selesai) VALUES (?, ?, ?, ?)");
            ps.setInt(1, ((ComboItem) cmbEvent.getSelectedItem()).getValue());
            ps.setString(2, txtAgenda.getText());
            ps.setString(3, txtMulai.getText());
            ps.setString(4, txtSelesai.getText());
            ps.executeUpdate();
            loadData();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    void loadData() {
        model.setRowCount(0);
        try {
            String sql = "SELECT e.nama_event, j.nama_agenda, j.waktu_mulai FROM jadwal j JOIN event e ON j.event_id = e.id";
            ResultSet rs = Koneksi.getKoneksi().createStatement().executeQuery(sql);
            while(rs.next()) model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3)});
        } catch (Exception e) {}
    }
}