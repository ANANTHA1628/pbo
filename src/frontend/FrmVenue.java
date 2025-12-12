package frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import backend.VenueBackend;

import java.awt.*;
import java.util.ArrayList;

public class FrmVenue extends JFrame {
    // Komponen GUI
    JTextField txtNama, txtAlamat, txtKapasitas;
    DefaultTableModel model;
    JTable table;
    
    // Panggil Backend
    VenueBackend backend = new VenueBackend();

    public FrmVenue() {
        setTitle("Form Master Venue"); 
        setSize(500, 450);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());

        // --- Panel Input ---
        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        p.add(new JLabel("Nama Venue:")); 
        txtNama = new JTextField(); 
        p.add(txtNama);
        
        p.add(new JLabel("Alamat:")); 
        txtAlamat = new JTextField(); 
        p.add(txtAlamat);
        
        p.add(new JLabel("Kapasitas (Angka):")); 
        txtKapasitas = new JTextField(); 
        p.add(txtKapasitas);
        
        JButton btn = new JButton("Simpan"); 
        p.add(new JLabel("")); 
        p.add(btn);
        
        add(p, BorderLayout.NORTH);

        // --- Panel Tabel ---
        model = new DefaultTableModel(new String[]{"ID", "Venue", "Alamat", "Kapasitas"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // --- Event Handling ---
        
        // Load data awal
        loadData();

        // Aksi Tombol Simpan
        btn.addActionListener(e -> {
            prosesSimpan();
        });
    }

    void prosesSimpan() {
        // Validasi input kosong
        if (txtNama.getText().isEmpty() || txtKapasitas.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Kapasitas harus diisi!");
            return;
        }

        try {
            // Konversi kapasitas jadi angka (int)
            int kap = Integer.parseInt(txtKapasitas.getText());
            
            // Panggil Backend
            boolean sukses = backend.simpanVenue(txtNama.getText(), txtAlamat.getText(), kap);
            
            if (sukses) {
                JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan!");
                loadData();
                // Reset form
                txtNama.setText("");
                txtAlamat.setText("");
                txtKapasitas.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data.");
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kapasitas harus berupa angka!");
        }
    }

    void loadData() {
        model.setRowCount(0);
        // Ambil data dari Backend
        ArrayList<Object[]> list = backend.getVenueList();
        
        // Masukkan ke Tabel GUI
        for (Object[] row : list) {
            model.addRow(row);
        }
    }
    
    public static void main(String[] args) {
        new FrmVenue().setVisible(true);
    }
}