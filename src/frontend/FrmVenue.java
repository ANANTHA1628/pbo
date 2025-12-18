package frontend;

import backend.Koneksi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Class FrmVenue
 * Deskripsi: Form untuk mengelola Master Data Venue (Tempat Event).
 * Memiliki fitur Tambah, Ubah, Hapus, dan Pencarian.
 * Langsung menggunakan query JDBC di sini (sebagai variasi atau contoh direct
 * access).
 */
public class FrmVenue extends JFrame {

    // --- Komponen GUI ---
    JTextField txtNama, txtAlamat, txtKapasitas, txtCari;
    DefaultTableModel model;
    JTable table;

    // --- Variabel Logika ---
    int selectedId = 0; // 0 artinya mode INSERT (Data Baru)

    /**
     * Konstruktor FrmVenue
     * Deskripsi: Menginisialisasi komponen GUI, mengatur layout, dan menambahkan
     * event listener.
     */
    public FrmVenue() {
        setTitle("Form Kelola Venue");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Panel Input Data (Utara)
        JPanel p = new JPanel(new GridLayout(4, 2, 5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        p.add(new JLabel("Nama Venue:"));
        txtNama = new JTextField();
        p.add(txtNama);

        p.add(new JLabel("Alamat:"));
        txtAlamat = new JTextField();
        p.add(txtAlamat);

        p.add(new JLabel("Kapasitas:"));
        txtKapasitas = new JTextField();
        p.add(txtKapasitas);

        // Panel untuk tombol-tombol
        JPanel pBtn = new JPanel();
        JButton btnSimpan = new JButton("Simpan");
        JButton btnHapus = new JButton("Hapus");
        JButton btnBatal = new JButton("Batal");
        pBtn.add(btnSimpan);
        pBtn.add(btnHapus);
        pBtn.add(btnBatal);

        p.add(new JLabel("")); // Dummy label for alignment
        p.add(pBtn);
        add(p, BorderLayout.NORTH);

        // Event Listeners Tombol
        btnSimpan.addActionListener(e -> simpan());
        btnHapus.addActionListener(e -> hapus());
        btnBatal.addActionListener(e -> kosongkan());

        // 2. Panel Tabel & Pencarian (Tengah)
        JPanel pCenter = new JPanel(new BorderLayout());

        // Panel Search
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pSearch.add(new JLabel("Cari Data:"));
        txtCari = new JTextField(20);
        pSearch.add(txtCari);
        pCenter.add(pSearch, BorderLayout.NORTH);

        // Event Listener untuk Live Search
        // Event Listener untuk Live Search
        txtCari.addKeyListener(new KeyAdapter() {
            /**
             * Method keyReleased
             * Deskripsi: Menangani event ketika tombol keyboard dilepas pada kolom
             * pencarian.
             * Memanggil method loadData() untuk memfilter data secara real-time.
             */
            public void keyReleased(KeyEvent e) {
                loadData();
            }
        });

        // Tabel
        model = new DefaultTableModel(new String[] { "ID", "Venue", "Alamat", "Kapasitas" }, 0);
        table = new JTable(model);
        pCenter.add(new JScrollPane(table), BorderLayout.CENTER);

        add(pCenter, BorderLayout.CENTER);

        // Event saat baris tabel diklik
        // Event saat baris tabel diklik
        table.addMouseListener(new MouseAdapter() {
            /**
             * Method mouseClicked
             * Deskripsi: Menangani event klik mouse pada baris tabel.
             * Mengambil data dari baris yang dipilih dan menampilkannya ke dalam form input
             * untuk diedit.
             */
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                selectedId = (int) model.getValueAt(row, 0);
                txtNama.setText(model.getValueAt(row, 1).toString());
                txtAlamat.setText(model.getValueAt(row, 2).toString());
                txtKapasitas.setText(model.getValueAt(row, 3).toString());
            }
        });

        loadData();
    }

    /**
     * Method kosongkan
     * Deskripsi: Mereset semua input form menjadi kosong dan mengembalikan mode ke
     * Insert (selectedId = 0).
     */
    void kosongkan() {
        txtNama.setText("");
        txtAlamat.setText("");
        txtKapasitas.setText("");
        selectedId = 0; // Reset ke mode insert
    }

    /**
     * Method simpan
     * Deskripsi: Menyimpan data venue ke database.
     * Jika selectedId == 0, maka lakukan INSERT (Data Baru).
     * Jika selectedId != 0, maka lakukan UPDATE (Data Lama).
     * Melakukan validasi input kapasitas sebelum menyimpan.
     */
    void simpan() {
        // Validasi input Kapasitas harus angka
        int kapasitas;
        try {
            kapasitas = Integer.parseInt(txtKapasitas.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kapasitas harus berupa angka valid!");
            return;
        }

        try {
            Connection c = Koneksi.getKoneksi();
            PreparedStatement ps;

            if (selectedId == 0) {
                // INSERT (Data Baru)
                ps = c.prepareStatement("INSERT INTO venue (nama_venue, alamat, kapasitas) VALUES (?, ?, ?)");
                ps.setString(1, txtNama.getText());
                ps.setString(2, txtAlamat.getText());
                ps.setInt(3, kapasitas);
            } else {
                // UPDATE (Data Lama)
                ps = c.prepareStatement("UPDATE venue SET nama_venue=?, alamat=?, kapasitas=? WHERE id=?");
                ps.setString(1, txtNama.getText());
                ps.setString(2, txtAlamat.getText());
                ps.setInt(3, kapasitas);
                ps.setInt(4, selectedId);
            }

            ps.executeUpdate();
            loadData();
            kosongkan(); // Reset form setelah simpan
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e.getMessage());
        }
    }

    /**
     * Method hapus
     * Deskripsi: Menghapus data venue yang sedang dipilih dari database.
     * Meminta konfirmasi pengguna sebelum melakukan penghapusan.
     */
    void hapus() {
        if (selectedId == 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection c = Koneksi.getKoneksi();
                PreparedStatement ps = c.prepareStatement("DELETE FROM venue WHERE id=?");
                ps.setInt(1, selectedId);
                ps.executeUpdate();
                loadData();
                kosongkan();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus: " + e.getMessage());
            }
        }
    }

    /**
     * Method loadData
     * Deskripsi: Mengambil data venue dari database dan menampilkannya pada tabel.
     * Mendukung fitur pencarian data berdasarkan nama event atau lokasi venue.
     */
    void loadData() {
        model.setRowCount(0);
        String keyword = txtCari.getText(); // Ambil kata kunci pencarian

        try {
            Connection c = Koneksi.getKoneksi();
            // Gunakan ILIKE untuk pencarian case-insensitive di PostgreSQL
            String sql = "SELECT * FROM venue WHERE nama_venue ILIKE ? OR alamat ILIKE ? ORDER BY id ASC";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next())
                model.addRow(new Object[] { rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4) });

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}