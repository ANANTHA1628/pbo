package frontend;

import backend.PesertaBackend;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

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
    JButton btnDaftar, btnImport;

    // --- Backend ---
    PesertaBackend backend;

    public FrmPeserta() {
        backend = new PesertaBackend();
        setTitle("Form Pendaftaran Peserta");
        setSize(500, 450);
        setLocationRelativeTo(null);
        // Menggunakan GridLayout sederhana 8 baris, 2 kolom
        setLayout(new GridLayout(8, 2, 10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Pilih Event
        add(new JLabel("Pilih Event:"));
        cmbEvent = new JComboBox<>();
        add(cmbEvent);

        // 2. Mode Input (Radio Button)
        add(new JLabel("Mode Input:"));
        JPanel panelRadio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbManual = new JRadioButton("Manual", true);
        rbImport = new JRadioButton("Import CSV");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbManual);
        bg.add(rbImport);
        panelRadio.add(rbManual);
        panelRadio.add(rbImport);
        add(panelRadio);

        // 3. Konfigurasi Field Manual
        add(new JLabel("Nama Peserta:"));
        txtNama = new JTextField();
        add(txtNama);

        add(new JLabel("Email:"));
        txtEmail = new JTextField();
        add(txtEmail);

        add(new JLabel("No HP:"));
        txtHp = new JTextField();
        add(txtHp);

        // 4. Tombol Aksi
        btnDaftar = new JButton("Daftar Sekarang");
        add(new JLabel(""));
        add(btnDaftar);

        btnImport = new JButton("Pilih File CSV...");
        btnImport.setEnabled(false); // Default matikan tombol import
        add(new JLabel("Import Data:"));
        add(btnImport);

        // --- Init Data & Listeners ---
        loadEvent();
        setupListeners();
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
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mendaftar.");
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
            } else {
                JOptionPane.showMessageDialog(this, "Gagal Melakukan Import.");
            }
        }
    }
}
