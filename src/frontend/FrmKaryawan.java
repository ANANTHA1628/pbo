package frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import backend.KaryawanBackend;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FrmKaryawan extends JFrame {
    // Komponen GUI (Tambah txtKeahlian)
    JTextField txtId, txtNama, txtAlamat, txtKontak, txtKeahlian, txtCari;
    JButton btnSimpan, btnUbah, btnHapus, btnReset, btnCari;
    JTable table;
    DefaultTableModel model;
    
    KaryawanBackend backend = new KaryawanBackend();

    public FrmKaryawan() {
        setTitle("Form Karyawan (+Keahlian)");
        setSize(700, 650); // Perbesar sedikit
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel Input ---
        // Ubah GridLayout jadi 6 baris (tambah 1 baris untuk keahlian)
        JPanel pInput = new JPanel(new GridLayout(6, 2, 10, 10));
        pInput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        pInput.add(new JLabel("ID Karyawan:"));
        txtId = new JTextField(); txtId.setEditable(false); txtId.setBackground(Color.LIGHT_GRAY);
        pInput.add(txtId);

        pInput.add(new JLabel("Nama:")); txtNama = new JTextField(); pInput.add(txtNama);
        pInput.add(new JLabel("Alamat:")); txtAlamat = new JTextField(); pInput.add(txtAlamat);
        pInput.add(new JLabel("Kontak:")); txtKontak = new JTextField(); pInput.add(txtKontak);
        
        // --- INPUT BARU: KEAHLIAN ---
        pInput.add(new JLabel("Keahlian:")); txtKeahlian = new JTextField(); pInput.add(txtKeahlian);

        // Panel Tombol
        JPanel pTombol = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnSimpan = new JButton("Simpan");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnReset = new JButton("Reset");
        
        pTombol.add(btnSimpan); pTombol.add(btnUbah); pTombol.add(btnHapus); pTombol.add(btnReset);
        pInput.add(new JLabel("Aksi:")); pInput.add(pTombol);
        add(pInput, BorderLayout.NORTH);

        // --- Panel Tabel & Cari ---
        JPanel pTabel = new JPanel(new BorderLayout());
        JPanel pCari = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtCari = new JTextField(20); btnCari = new JButton("Cari");
        pCari.add(new JLabel("Cari Nama:")); pCari.add(txtCari); pCari.add(btnCari);
        pTabel.add(pCari, BorderLayout.NORTH);

        // Update Kolom Tabel (+Keahlian)
        model = new DefaultTableModel(new String[]{"ID", "Nama", "Alamat", "Kontak", "Keahlian"}, 0);
        table = new JTable(model);
        pTabel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pTabel, BorderLayout.CENTER);

        // --- EVENT HANDLING ---
        
        tampilkanData(""); 

        // 1. Tombol Simpan
        btnSimpan.addActionListener(e -> {
            if(validasiInput()) {
                // Panggil Backend dengan parameter baru
                boolean sukses = backend.simpanKaryawan(
                        txtNama.getText(), 
                        txtAlamat.getText(), 
                        txtKontak.getText(),
                        txtKeahlian.getText() // Input Keahlian
                );
                if(sukses) {
                    JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan!");
                    tampilkanData("");
                    resetForm();
                }
            }
        });

        // 2. Tombol Ubah
        btnUbah.addActionListener(e -> {
            if(txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data dulu!"); return;
            }
            if(validasiInput()) {
                int id = Integer.parseInt(txtId.getText());
                // Panggil Backend dengan parameter baru
                boolean sukses = backend.ubahKaryawan(
                        id, 
                        txtNama.getText(), 
                        txtAlamat.getText(), 
                        txtKontak.getText(),
                        txtKeahlian.getText() // Input Keahlian
                );
                if(sukses) {
                    JOptionPane.showMessageDialog(this, "Data Berhasil Diubah!");
                    tampilkanData("");
                    resetForm();
                }
            }
        });

        // 3. Tombol Hapus
        btnHapus.addActionListener(e -> {
            if(txtId.getText().isEmpty()) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                boolean sukses = backend.hapusKaryawan(Integer.parseInt(txtId.getText()));
                if(sukses) {
                    JOptionPane.showMessageDialog(this, "Data Dihapus!");
                    tampilkanData("");
                    resetForm();
                }
            }
        });

        // 4. Cari
        ActionListener cariAction = e -> tampilkanData(txtCari.getText());
        btnCari.addActionListener(cariAction);
        txtCari.addActionListener(cariAction);

        btnReset.addActionListener(e -> resetForm());

        // Klik Tabel
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtId.setText(model.getValueAt(row, 0).toString());
                txtNama.setText(model.getValueAt(row, 1).toString());
                txtAlamat.setText(model.getValueAt(row, 2).toString());
                txtKontak.setText(model.getValueAt(row, 3).toString());
                // Ambil data keahlian dari tabel (kolom index 4)
                Object keahlianObj = model.getValueAt(row, 4);
                txtKeahlian.setText(keahlianObj != null ? keahlianObj.toString() : "");
                
                btnSimpan.setEnabled(false); btnUbah.setEnabled(true); btnHapus.setEnabled(true);
            }
        });
        
        resetForm();
    }

    // --- Helper Methods ---

    void tampilkanData(String keyword) {
        model.setRowCount(0);
        ArrayList<Object[]> data = backend.getListKaryawan(keyword);
        for(Object[] row : data) {
            model.addRow(row);
        }
    }

    void resetForm() {
        txtId.setText(""); 
        txtNama.setText(""); 
        txtAlamat.setText(""); 
        txtKontak.setText("");
        txtKeahlian.setText(""); // Reset keahlian
        
        btnSimpan.setEnabled(true); btnUbah.setEnabled(false); btnHapus.setEnabled(false);
    }

    boolean validasiInput() {
        if(txtNama.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama harus diisi!"); return false;
        }
        return true;
    }
    
    public static void main(String[] args) {
        new FrmKaryawan().setVisible(true);
    }
}