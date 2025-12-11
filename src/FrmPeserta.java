import javax.swing.*;

import backend.Koneksi;

import java.awt.*;
import java.sql.*;

public class FrmPeserta extends JFrame {
    JComboBox<ComboItem> cmbEvent;
    JTextField txtNama, txtEmail, txtHp;

    public FrmPeserta() {
        setTitle("Form Peserta"); setSize(400, 350);
        setLocationRelativeTo(null); setLayout(new GridLayout(5, 2));

        add(new JLabel("Pilih Event:")); cmbEvent = new JComboBox<>(); add(cmbEvent);
        add(new JLabel("Nama Peserta:")); txtNama = new JTextField(); add(txtNama);
        add(new JLabel("Email:")); txtEmail = new JTextField(); add(txtEmail);
        add(new JLabel("No HP:")); txtHp = new JTextField(); add(txtHp);
        JButton btn = new JButton("Daftar"); add(new JLabel("")); add(btn);

        loadEvent();
        btn.addActionListener(e -> daftar());
    }

    void loadEvent() {
        try {
            ResultSet rs = Koneksi.getKoneksi().createStatement().executeQuery("SELECT id, nama_event FROM event");
            while(rs.next()) cmbEvent.addItem(new ComboItem(rs.getString("nama_event"), rs.getInt("id")));
        } catch (Exception e) {}
    }

    void daftar() {
        Connection c = null;
        try {
            c = Koneksi.getKoneksi();
            c.setAutoCommit(false); 

            // 1. Simpan Peserta
            PreparedStatement ps1 = c.prepareStatement("INSERT INTO peserta (nama_peserta, email, no_hp) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, txtNama.getText());
            ps1.setString(2, txtEmail.getText());
            ps1.setString(3, txtHp.getText());
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            int idPeserta = 0;
            if (rs.next()) idPeserta = rs.getInt(1);

            // 2. Hubungkan ke Event
            PreparedStatement ps2 = c.prepareStatement("INSERT INTO event_peserta (event_id, peserta_id) VALUES (?, ?)");
            ps2.setInt(1, ((ComboItem) cmbEvent.getSelectedItem()).getValue());
            ps2.setInt(2, idPeserta);
            ps2.executeUpdate();

            c.commit(); 
            JOptionPane.showMessageDialog(this, "Berhasil Daftar!");
            txtNama.setText(""); txtEmail.setText(""); txtHp.setText("");
        } catch (Exception e) {
            try { if (c != null) c.rollback(); } catch (Exception ex) {}
            JOptionPane.showMessageDialog(this, "Gagal: " + e.getMessage());
        }
    }
}