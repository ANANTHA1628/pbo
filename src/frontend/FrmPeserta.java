package frontend;

import backend.PesertaBackend;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

/**
 * Class FrmPeserta
 * Deskripsi: Form untuk pendaftaran peserta ke dalam sebuah Event.
 * Mendukung pendaftaran manual (satu per satu) atau Import dari CSV.
 */
public class FrmPeserta extends JFrame {

    // --- Komponen GUI ---
    JComboBox<ComboItem> cmbEvent;
    JTextField txtNama, txtEmail, txtHp;
    JRadioButton rbManual, rbImport;
    JButton btnDaftar, btnImport, btnRefresh, btnHapus;
    JTable tblPeserta;
    DefaultTableModel tableModel;

    // --- Backend ---
    PesertaBackend backend;

    public FrmPeserta() {
        backend = new PesertaBackend();
        setTitle("Form Pendaftaran Peserta");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel untuk form input
        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 10, 10));

        // 1. Pilih Event
        inputPanel.add(new JLabel("Pilih Event:"));
        cmbEvent = new JComboBox<>();
        inputPanel.add(cmbEvent);

        // 2. Mode Input (Radio Button)
        inputPanel.add(new JLabel("Mode Input:"));
        JPanel panelRadio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbManual = new JRadioButton("Manual", true);
        rbImport = new JRadioButton("Import CSV");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbManual);
        bg.add(rbImport);
        panelRadio.add(rbManual);
        panelRadio.add(rbImport);
        inputPanel.add(panelRadio);

        // 3. Konfigurasi Field Manual
        inputPanel.add(new JLabel("Nama Peserta:"));
        txtNama = new JTextField();
        inputPanel.add(txtNama);

        inputPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        inputPanel.add(txtEmail);

        inputPanel.add(new JLabel("No HP:"));
        txtHp = new JTextField();
        inputPanel.add(txtHp);

        // 4. Tombol Aksi
        btnDaftar = new JButton("Daftar Sekarang");
        inputPanel.add(new JLabel(""));
        inputPanel.add(btnDaftar);

        btnImport = new JButton("Pilih File CSV...");
        btnImport.setEnabled(false); // Default matikan tombol import
        inputPanel.add(new JLabel("Import Data:"));
        inputPanel.add(btnImport);
        
        // Panel untuk tabel peserta
        JPanel tablePanel = new JPanel(new BorderLayout());
        
        // Panel untuk tombol aksi
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Tombol refresh
        btnRefresh = new JButton("Refresh Data");
        buttonPanel.add(btnRefresh);
        
        // Tombol hapus
        btnHapus = new JButton("Hapus Peserta");
        btnHapus.setBackground(new Color(220, 20, 60));
        btnHapus.setForeground(Color.WHITE);
        buttonPanel.add(btnHapus);
        
        // Tabel peserta
        String[] columnNames = {"ID", "Nama Peserta", "Email", "No HP"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tblPeserta = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblPeserta);
        
        tablePanel.add(buttonPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Gabungkan semua komponen
        add(inputPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);

        // --- Init Data & Listeners ---
        loadEvent();
        setupListeners();
        
        // Load data awal jika ada event yang dipilih
        if (cmbEvent.getItemCount() > 0) {
            loadPesertaByEvent();
        }
    }

    // Mengisi ComboBox Event
    void loadEvent() {
        ArrayList<Object[]> events = backend.getListEvent();
        for (Object[] event : events) {
            // event[0] = id, event[1] = nama_event
            cmbEvent.addItem(new ComboItem((String) event[1], (Integer) event[0]));
        }
    }

    // Mengatur Event Listener
    void setupListeners() {
        // Logika Ganti Mode
        rbManual.addActionListener(e -> toggleInputMode(true));
        rbImport.addActionListener(e -> toggleInputMode(false));

        // Tombol Daftar (Manual)
        btnDaftar.addActionListener(e -> daftarManual());

        // Tombol Import (CSV)
        btnImport.addActionListener(e -> importCSV());
        
        // Tombol Refresh
        btnRefresh.addActionListener(e -> loadPesertaByEvent());
        
        // Tombol Hapus
        btnHapus.addActionListener(e -> hapusPeserta());
        
        // Event saat memilih event
        cmbEvent.addActionListener(e -> loadPesertaByEvent());
    }

    // Mengubah status aktif/tidak aktif komponen berdasarkan mode
    void toggleInputMode(boolean manual) {
        txtNama.setEnabled(manual);
        txtEmail.setEnabled(manual);
        txtHp.setEnabled(manual);
        btnDaftar.setEnabled(manual);
        btnImport.setEnabled(!manual);
    }

    // Proses Pendaftaran Manual
    void daftarManual() {
        if (cmbEvent.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih event terlebih dahulu!");
            return;
        }

        String nama = txtNama.getText();
        String email = txtEmail.getText();
        String hp = txtHp.getText();
        int eventId = ((ComboItem) cmbEvent.getSelectedItem()).getValue();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama wajib diisi!");
            return;
        }

        if (backend.simpanPesertaManual(nama, email, hp, eventId)) {
            JOptionPane.showMessageDialog(this, "Pendaftaran Berhasil!");
            txtNama.setText("");
            txtEmail.setText("");
            txtHp.setText("");
            loadPesertaByEvent(); // Refresh tabel setelah pendaftaran
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mendaftar.");
        }
    }

    // Memuat data peserta berdasarkan event yang dipilih
    void loadPesertaByEvent() {
        if (cmbEvent.getSelectedItem() == null) return;
        
        int eventId = ((ComboItem) cmbEvent.getSelectedItem()).getValue();
        ArrayList<Object[]> dataPeserta = backend.getDaftarPesertaByEvent(eventId);
        
        // Kosongkan tabel
        tableModel.setRowCount(0);
        
        // Isi tabel dengan data baru
        for (Object[] row : dataPeserta) {
            tableModel.addRow(row);
        }
    }
    
    // Method untuk menghapus peserta yang dipilih
    void hapusPeserta() {
        int selectedRow = tblPeserta.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih peserta yang akan dihapus terlebih dahulu!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Apakah Anda yakin ingin menghapus peserta ini?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int pesertaId = (int) tblPeserta.getValueAt(selectedRow, 0);
                
                if (backend.hapusPeserta(pesertaId)) {
                    JOptionPane.showMessageDialog(this, "Data peserta berhasil dihapus!");
                    loadPesertaByEvent(); // Refresh tabel
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data peserta!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
            }
        }
    }
    
    // Proses Import dari File CSV
    void importCSV() {
        if (cmbEvent.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih event terlebih dahulu!");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = chooser.getSelectedFile().getAbsolutePath();
            int eventId = ((ComboItem) cmbEvent.getSelectedItem()).getValue();

            if (backend.importPesertaDariCSV(filePath, eventId)) {
                JOptionPane.showMessageDialog(this, "Import Berhasil! Data peserta telah masuk.");
                loadPesertaByEvent(); // Refresh tabel setelah import
            } else {
                JOptionPane.showMessageDialog(this, "Gagal Melakukan Import.");
            }
        }
    }
}
