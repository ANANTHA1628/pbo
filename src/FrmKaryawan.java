import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FrmKaryawan extends JFrame {
    JTextField txtNama, txtKontak;
    DefaultTableModel model;
    JTable table;

    public FrmKaryawan() {
        setTitle("Form Karyawan"); setSize(500, 400);
        setLocationRelativeTo(null); setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(3, 2));
        p.add(new JLabel("Nama Karyawan:")); txtNama = new JTextField(); p.add(txtNama);
        p.add(new JLabel("Kontak:")); txtKontak = new JTextField(); p.add(txtKontak);
        JButton btn = new JButton("Simpan"); p.add(new JLabel("")); p.add(btn);
        add(p, BorderLayout.NORTH);

        btn.addActionListener(e -> simpan());

        model = new DefaultTableModel(new String[]{"ID", "Nama", "Kontak"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    void simpan() {
        try {
            Connection c = Koneksi.getKoneksi();
            c.prepareStatement("INSERT INTO karyawan (nama, kontak) VALUES ('"+txtNama.getText()+"','"+txtKontak.getText()+"')").executeUpdate();
            loadData(); txtNama.setText(""); txtKontak.setText("");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    void loadData() {
        model.setRowCount(0);
        try {
            ResultSet rs = Koneksi.getKoneksi().createStatement().executeQuery("SELECT * FROM karyawan");
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3)});
        } catch (Exception e) {}
    }
} 
