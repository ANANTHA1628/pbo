package frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import backend.KaryawanBackend;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FrmKaryawan extends JFrame {
    // Komponen GUI
    JTextField txtId, txtNama, txtAlamat, txtKontak, txtCari;
    JButton btnSimpan, btnUbah, btnHapus, btnReset, btnCari;
    JTable table;
    DefaultTableModel model;
    
    // PANGGIL FILE BACKEND DI SINI
    KaryawanBackend backend = new KaryawanBackend();

    public FrmKaryawan() {
        setTitle("Form Karyawan (Arsitektur Terpisah)");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel Input ---
        JPanel pInput = new JPanel(new GridLayout(5, 2, 10, 10));
        pInput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        pInput.add(new JLabel("ID Karyawan:"));
        txtId = new JTextField(); txtId.setEditable(false); txtId.setBackground(Color.LIGHT_GRAY);
        pInput.add(txtId);

        pInput.add(new JLabel("Nama:")); txtNama = new JTextField(); pInput.add(txtNama);
        pInput.add(new JLabel("Alamat:")); txtAlamat = new JTextField(); pInput.add(txtAlamat);
        pInput.add(new JLabel("Kontak:")); txtKontak = new JTextField(); pInput.add(txtKontak);

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

        model = new DefaultTableModel(new String[]{"ID", "Nama", "Alamat", "Kontak"}, 0);
        table = new JTable(model);
        pTabel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pTabel, BorderLayout.CENTER);

        // --- EVENT HANDLING (Panggil Backend) ---
        
        tampilkanData(""); // Load awal

        // 1. Tombol Simpan
        btnSimpan.addActionListener(e -> {
            if(validasiInput()) {
                // PANGGIL BACKEND
                boolean sukses = backend.simpanKaryawan(txtNama.getText(), txtAlamat.getText(), txtKontak.getText());
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
                // PANGGIL BACKEND
                int id = Integer.parseInt(txtId.getText());
                boolean sukses = backend.ubahKaryawan(id, txtNama.getText(), txtAlamat.getText(), txtKontak.getText());
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
                // PANGGIL BACKEND
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
                btnSimpan.setEnabled(false); btnUbah.setEnabled(true); btnHapus.setEnabled(true);
            }
        });
        
        resetForm();
    }

    // --- Helper Methods ---

    // Fungsi mengambil data dari Backend dan memasukkan ke Tabel GUI
    void tampilkanData(String keyword) {
        model.setRowCount(0);
        // Minta data ke Backend (List of Array)
        ArrayList<Object[]> data = backend.getListKaryawan(keyword);
        
        // Looping data dari backend untuk dimasukkan ke tabel GUI
        for(Object[] row : data) {
            model.addRow(row);
        }
    }

    void resetForm() {
        txtId.setText(""); txtNama.setText(""); txtAlamat.setText(""); txtKontak.setText("");
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