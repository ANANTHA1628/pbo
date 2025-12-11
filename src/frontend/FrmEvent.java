package frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import backend.EventBackend;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FrmEvent extends JFrame {
    JTextField txtNama, txtPoolPrize, txtHargaRegistrasi, txtCari;
    JSpinner spnTanggal;
    JComboBox<ComboItem> cmbVenue;
    DefaultTableModel model;
    JTable table;
    
    // Variabel logika
    int selectedEventId = -1; 
    EventBackend backend = new EventBackend(); // PANGGIL BACKEND

    public FrmEvent() {
        setTitle("Form Kelola Event");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel Input ---
        JPanel pInput = new JPanel(new GridLayout(6, 2, 10, 10));
        pInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pInput.add(new JLabel("Nama Event:"));
        txtNama = new JTextField(); pInput.add(txtNama);

        pInput.add(new JLabel("Tanggal:"));
        spnTanggal = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnTanggal, "yyyy-MM-dd");
        spnTanggal.setEditor(dateEditor);
        pInput.add(spnTanggal);

        pInput.add(new JLabel("Pool Prize (Rp):"));
        txtPoolPrize = new JTextField(); pInput.add(txtPoolPrize);

        pInput.add(new JLabel("Harga Registrasi (Rp):"));
        txtHargaRegistrasi = new JTextField(); pInput.add(txtHargaRegistrasi);

        pInput.add(new JLabel("Pilih Venue:"));
        cmbVenue = new JComboBox<>(); pInput.add(cmbVenue);

        JButton btnSimpan = new JButton("Simpan Event");
        pInput.add(new JLabel("")); pInput.add(btnSimpan);
        
        add(pInput, BorderLayout.NORTH);

        // --- Panel Tabel & Cari ---
        JPanel pCenter = new JPanel(new BorderLayout());
        JPanel pCari = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtCari = new JTextField(20);
        JButton btnCari = new JButton("Cari");
        JButton btnReset = new JButton("Reset Form");
        JButton btnHapus = new JButton("Hapus");
        
        pCari.add(new JLabel("Cari:")); pCari.add(txtCari); pCari.add(btnCari);
        pCari.add(btnReset); pCari.add(btnHapus);
        pCenter.add(pCari, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Event", "Tanggal", "Pool Prize", "Harga Reg", "Lokasi"}, 0);
        table = new JTable(model);
        pCenter.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pCenter, BorderLayout.CENTER);

        // --- INIT DATA ---
        isiComboVenue();
        tampilkanData("");

        // --- ACTION LISTENER ---
        
        // 1. Tombol Simpan
        btnSimpan.addActionListener(e -> prosesSimpan());

        // 2. Tombol Cari
        btnCari.addActionListener(e -> tampilkanData(txtCari.getText()));

        // 3. Tombol Hapus
        btnHapus.addActionListener(e -> prosesHapus());

        // 4. Tombol Reset
        btnReset.addActionListener(e -> resetForm());

        // 5. Klik Tabel (Edit Mode)
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        selectedEventId = (Integer) model.getValueAt(row, 0);
                        isiFormDariData(selectedEventId);
                    }
                }
            }
        });
    }

    // --- Helper UI ke Backend ---

    void isiComboVenue() {
        // Minta data ke backend
        ArrayList<ComboItem> list = backend.getVenueList();
        cmbVenue.removeAllItems();
        for (ComboItem item : list) {
            cmbVenue.addItem(item);
        }
    }

    void tampilkanData(String keyword) {
        model.setRowCount(0);
        ArrayList<Object[]> list = backend.getEventList(keyword);
        for (Object[] row : list) {
            model.addRow(row);
        }
    }

    void prosesSimpan() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama event wajib diisi!");
            return;
        }

        // Cek Duplikasi lewat Backend
        if (backend.isNamaAda(nama, selectedEventId)) {
            JOptionPane.showMessageDialog(this, "Nama event sudah ada! Ganti yang lain.");
            return;
        }

        try {
            // Parsing Data
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tglStr = sdf.format((Date) spnTanggal.getValue());
            
            // Hapus titik jika user input format uang (misal 10.000)
            long prize = Long.parseLong(txtPoolPrize.getText().replaceAll("\\.", ""));
            long harga = Long.parseLong(txtHargaRegistrasi.getText().replaceAll("\\.", ""));
            int venueId = ((ComboItem) cmbVenue.getSelectedItem()).getValue();

            boolean sukses;
            if (selectedEventId == -1) {
                // INSERT
                sukses = backend.insertEvent(nama, tglStr, prize, harga, venueId);
            } else {
                // UPDATE
                sukses = backend.updateEvent(selectedEventId, nama, tglStr, prize, harga, venueId);
            }

            if (sukses) {
                JOptionPane.showMessageDialog(this, "Berhasil disimpan!");
                resetForm();
                tampilkanData("");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Prize dan Harga harus angka valid!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void prosesHapus() {
        if (selectedEventId == -1 && table.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data dulu di tabel!");
            return;
        }
        
        // Ambil ID dari seleksi jika selectedEventId masih -1 tapi tabel diklik
        if (selectedEventId == -1) {
            selectedEventId = (Integer) model.getValueAt(table.getSelectedRow(), 0);
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Hapus Event ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (backend.deleteEvent(selectedEventId)) {
                JOptionPane.showMessageDialog(this, "Terhapus!");
                resetForm();
                tampilkanData("");
            }
        }
    }

    void isiFormDariData(int id) {
        Object[] data = backend.getEventById(id);
        if (data != null) {
            // data urutan: [nama, tanggal, pool, harga, venueId]
            txtNama.setText((String) data[0]);
            
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d = sdf.parse((String) data[1]);
                spnTanggal.setValue(d);
            } catch (Exception e) {}

            txtPoolPrize.setText(String.valueOf(data[2]));
            txtHargaRegistrasi.setText(String.valueOf(data[3]));

            // Set ComboBox Venue
            int vId = (Integer) data[4];
            for (int i = 0; i < cmbVenue.getItemCount(); i++) {
                if (cmbVenue.getItemAt(i).getValue() == vId) {
                    cmbVenue.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    void resetForm() {
        txtNama.setText("");
        txtPoolPrize.setText("");
        txtHargaRegistrasi.setText("");
        spnTanggal.setValue(new Date());
        txtCari.setText("");
        selectedEventId = -1; // Reset mode jadi Insert
        if(cmbVenue.getItemCount() > 0) cmbVenue.setSelectedIndex(0);
    }
    
    public static void main(String[] args) {
        new FrmEvent().setVisible(true);
    }
}