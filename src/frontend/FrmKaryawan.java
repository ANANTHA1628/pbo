package frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import backend.KaryawanBackend;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Class FrmKaryawan
 * Deskripsi: Form untuk mengelola data Master Karyawan.
 * Dilengkapi field tambahan 'Keahlian' sesuai request sebelumnya.
 */
public class FrmKaryawan extends JFrame {

    // --- Komponen GUI ---
    JTextField txtId, txtNama, txtAlamat, txtKontak, txtKeahlian, txtCari;
    JButton btnSimpan, btnUbah, btnHapus, btnReset, btnCari;
    JTable table;
    DefaultTableModel model;

    // --- Backend ---
    KaryawanBackend backend = new KaryawanBackend();

    public FrmKaryawan() {
        setTitle("Form Master Karyawan");
        setSize(700, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Panel Input Data (Bagian Atas)
        JPanel pInput = new JPanel(new GridLayout(6, 2, 10, 10));
        pInput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Field ID (Read Only)
        pInput.add(new JLabel("ID Karyawan:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBackground(Color.LIGHT_GRAY);
        pInput.add(txtId);

        // Field Data Lain
        pInput.add(new JLabel("Nama:"));
        txtNama = new JTextField();
        pInput.add(txtNama);
        pInput.add(new JLabel("Alamat:"));
        txtAlamat = new JTextField();
        pInput.add(txtAlamat);
        pInput.add(new JLabel("Kontak:"));
        txtKontak = new JTextField();
        pInput.add(txtKontak);

        // Field Keahlian (Baru)
        pInput.add(new JLabel("Keahlian:"));
        txtKeahlian = new JTextField();
        pInput.add(txtKeahlian);

        // Panel Tombol Aksi (Simpan, Ubah, Hapus, Reset)
        JPanel pTombol = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnSimpan = new JButton("Simpan");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnReset = new JButton("Reset");

        pTombol.add(btnSimpan);
        pTombol.add(btnUbah);
        pTombol.add(btnHapus);
        pTombol.add(btnReset);
        pInput.add(new JLabel("Aksi:"));
        pInput.add(pTombol);
        add(pInput, BorderLayout.NORTH);

        // 2. Panel Tabel & Pencarian (Center)
        JPanel pTabel = new JPanel(new BorderLayout());
        JPanel pCari = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtCari = new JTextField(20);
        btnCari = new JButton("Cari");
        pCari.add(new JLabel("Cari Nama:"));
        pCari.add(txtCari);
        pCari.add(btnCari);
        pTabel.add(pCari, BorderLayout.NORTH);

        // Setup Tabel
        model = new DefaultTableModel(new String[] { "ID", "Nama", "Alamat", "Kontak", "Keahlian" }, 0);
        table = new JTable(model);
        pTabel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pTabel, BorderLayout.CENTER);

        // --- Event Listeners ---

        // Load data awal
        tampilkanData("");

        // Tombol Simpan
        btnSimpan.addActionListener(e -> {
            if (validasiInput()) {
                boolean sukses = backend.simpanKaryawan(
                        txtNama.getText(),
                        txtAlamat.getText(),
                        txtKontak.getText(),
                        txtKeahlian.getText());
                if (sukses) {
                    JOptionPane.showMessageDialog(this, "Data Karyawan Berhasil Disimpan!");
                    tampilkanData("");
                    resetForm();
                }
            }
        });

        // Tombol Ubah
        btnUbah.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data karyawan yang akan diubah!");
                return;
            }
            if (validasiInput()) {
                int id = Integer.parseInt(txtId.getText());
                boolean sukses = backend.ubahKaryawan(
                        id,
                        txtNama.getText(),
                        txtAlamat.getText(),
                        txtKontak.getText(),
                        txtKeahlian.getText());
                if (sukses) {
                    JOptionPane.showMessageDialog(this, "Data Karyawan Berhasil Diubah!");
                    tampilkanData("");
                    resetForm();
                }
            }
        });

        // Tombol Hapus
        btnHapus.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!");
                return;
            }
            ;
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus karyawan ini?", "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean sukses = backend.hapusKaryawan(Integer.parseInt(txtId.getText()));
                if (sukses) {
                    JOptionPane.showMessageDialog(this, "Data Karyawan Dihapus!");
                    tampilkanData("");
                    resetForm();
                }
            }
        });

        // Action Pencarian
        ActionListener cariAction = e -> tampilkanData(txtCari.getText());
        btnCari.addActionListener(cariAction);
        txtCari.addActionListener(cariAction);

        // Tombol Reset
        btnReset.addActionListener(e -> resetForm());

        // Klik Mouse pada Tabel
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(model.getValueAt(row, 0).toString());
                    txtNama.setText(model.getValueAt(row, 1).toString());
                    txtAlamat.setText(model.getValueAt(row, 2).toString());
                    txtKontak.setText(model.getValueAt(row, 3).toString());

                    Object keahlianObj = model.getValueAt(row, 4);
                    txtKeahlian.setText(keahlianObj != null ? keahlianObj.toString() : "");

                    // Atur state tombol
                    btnSimpan.setEnabled(false);
                    btnUbah.setEnabled(true);
                    btnHapus.setEnabled(true);
                }
            }
        });

        resetForm(); // Set initial state
    }

    // --- Helper Methods ---

    // Menampilkan data karyawan ke JTable
    void tampilkanData(String keyword) {
        model.setRowCount(0);
        ArrayList<Object[]> data = backend.getListKaryawan(keyword);
        for (Object[] row : data) {
            model.addRow(row);
        }
    }

    // Mengosongkan form input
    void resetForm() {
        txtId.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtKontak.setText("");
        txtKeahlian.setText("");

        btnSimpan.setEnabled(true);
        btnUbah.setEnabled(false);
        btnHapus.setEnabled(false);
    }

    // Validasi input sederhana
    boolean validasiInput() {
        if (txtNama.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama karyawan harus diisi!");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        new FrmKaryawan().setVisible(true);
    }
}