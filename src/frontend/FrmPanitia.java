package frontend;
import backend.PanitiaBackend;
import backend.PanitiaBackend.EventItem;
import backend.PanitiaBackend.KaryawanItem;
import backend.PanitiaBackend.Panitia;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmPanitia extends JFrame {
    JComboBox<EventItem> cmbEvent;
    JComboBox<KaryawanItem> cmbKaryawan;
    JTextField txtJabatan, txtKeahlian, txtSearch;
    DefaultTableModel model;
    JTable table;
    PanitiaBackend dao = new PanitiaBackend();
    int selectedRow = -1;
    int selectedId = -1;

    public FrmPanitia() {
        setTitle("Form Panitia");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel Input
        JPanel pInput = new JPanel(new GridLayout(5, 2, 5, 5));
        pInput.setBorder(BorderFactory.createTitledBorder("Input Data"));
        
        pInput.add(new JLabel("Event:"));
        cmbEvent = new JComboBox<>();
        cmbEvent.addActionListener(e -> filterByEvent());
        pInput.add(cmbEvent);

        pInput.add(new JLabel("Karyawan:"));
        cmbKaryawan = new JComboBox<>();
        cmbKaryawan.addActionListener(e -> updateKeahlian());
        pInput.add(cmbKaryawan);

        pInput.add(new JLabel("Keahlian:"));
        txtKeahlian = new JTextField();
        txtKeahlian.setEditable(false);
        pInput.add(txtKeahlian);

        pInput.add(new JLabel("Jabatan:"));
        txtJabatan = new JTextField();
        pInput.add(txtJabatan);

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton btnSimpan = new JButton("Simpan");
        JButton btnHapus = new JButton("Hapus");
        JButton btnReset = new JButton("Reset");
        JButton btnBack = new JButton("Kembali");
        btnSimpan.addActionListener(e -> simpan());
        btnHapus.addActionListener(e -> hapus());
        btnReset.addActionListener(e -> reset());
        btnBack.setFont(new Font("Arial", Font.BOLD, 12));
        btnBack.setBackground(new Color(105, 105, 105));
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> kembaliKeMain());
        pBtn.add(btnSimpan);
        pBtn.add(btnHapus);
        pBtn.add(btnReset);
        pBtn.add(btnBack);
        pInput.add(pBtn);

        JPanel pTop = new JPanel(new BorderLayout());
        pTop.add(pInput, BorderLayout.CENTER);
        add(pTop, BorderLayout.NORTH);

        // Panel Search
        JPanel pSearch = new JPanel(new BorderLayout(5, 5));
        pSearch.setBorder(BorderFactory.createTitledBorder("Pencarian"));
        pSearch.add(new JLabel("Cari Data:"), BorderLayout.WEST);
        txtSearch = new JTextField();
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                cari();
            }
        });
        pSearch.add(txtSearch, BorderLayout.CENTER);
        JButton btnClear = new JButton("Bersihkan");
        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });
        pSearch.add(btnClear, BorderLayout.EAST);
        add(pSearch, BorderLayout.SOUTH);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Event", "Karyawan", "Keahlian", "Jabatan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pilihBaris();
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadCombos();
        loadData();
    }

    void loadCombos() {
        try {
            cmbEvent.removeAllItems();
            List<EventItem> events = dao.getAllEvent();
            for (EventItem e : events) {
                cmbEvent.addItem(e);
            }

            cmbKaryawan.removeAllItems();
            List<KaryawanItem> karyawans = dao.getAllKaryawan();
            for (KaryawanItem k : karyawans) {
                cmbKaryawan.addItem(k);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    void loadData() {
        try {
            model.setRowCount(0);
            List<Panitia> panitias = dao.getAllPanitia();
            for (Panitia p : panitias) {
                model.addRow(new Object[]{p.id, p.nama_event, p.nama_karyawan, p.keahlian, p.jabatan});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    void cari() {
        try {
            String keyword = txtSearch.getText().trim();
            model.setRowCount(0);
            
            if (keyword.isEmpty()) {
                loadData();
                return;
            }
            
            List<Panitia> panitias = dao.searchPanitia(keyword);
            if (panitias.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan!");
                loadData();
                return;
            }
            
            for (Panitia p : panitias) {
                model.addRow(new Object[]{p.id, p.nama_event, p.nama_karyawan, p.jabatan});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    void pilihBaris() {
        selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            selectedId = (int) model.getValueAt(selectedRow, 0);
            try {
                Panitia p = dao.getPanitiaById(selectedId);
                if (p != null) {
                    // Set combo event
                    for (int i = 0; i < cmbEvent.getItemCount(); i++) {
                        if (cmbEvent.getItemAt(i).id == p.event_id) {
                            cmbEvent.setSelectedIndex(i);
                            break;
                        }
                    }
                    // Set combo karyawan
                    for (int i = 0; i < cmbKaryawan.getItemCount(); i++) {
                        if (cmbKaryawan.getItemAt(i).id == p.karyawan_id) {
                            cmbKaryawan.setSelectedIndex(i);
                            break;
                        }
                    }
                    txtJabatan.setText(p.jabatan);
                    txtKeahlian.setText(p.keahlian);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    void updateKeahlian() {
        KaryawanItem selected = (KaryawanItem) cmbKaryawan.getSelectedItem();
        if (selected != null) {
            txtKeahlian.setText(selected.keahlian != null ? selected.keahlian : "");
        }
    }

    void filterByEvent() {
        EventItem selected = (EventItem) cmbEvent.getSelectedItem();
        if (selected == null) {
            return;
        }
        
        try {
            model.setRowCount(0);
            List<Panitia> panitias = dao.getPanitiaByEventId(selected.id);
            for (Panitia p : panitias) {
                model.addRow(new Object[]{p.id, p.nama_event, p.nama_karyawan, p.keahlian, p.jabatan});
            }
            
            // Jika tidak ada data untuk event ini
            if (panitias.isEmpty()) {
                // Optional: Uncomment jika ingin tampilkan message
                // JOptionPane.showMessageDialog(this, "Tidak ada panitia untuk event ini");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    void simpan() {
        try {
            if (txtJabatan.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
                return;
            }

            EventItem eventSelected = (EventItem) cmbEvent.getSelectedItem();
            KaryawanItem karyawanSelected = (KaryawanItem) cmbKaryawan.getSelectedItem();
            
            if (eventSelected == null || karyawanSelected == null) {
                JOptionPane.showMessageDialog(this, "Pilih event dan karyawan terlebih dahulu!");
                return;
            }

            if (selectedId == -1) {
                // Insert
                dao.insertPanitia(eventSelected.id, karyawanSelected.id, txtJabatan.getText());
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            } else {
                // Update
                dao.updatePanitia(selectedId, eventSelected.id, karyawanSelected.id, txtJabatan.getText());
                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!");
            }
            reset();
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    void hapus() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.deletePanitia(selectedId);
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                reset();
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    void reset() {
        cmbEvent.setSelectedIndex(0);
        cmbKaryawan.setSelectedIndex(0);
        txtJabatan.setText("");
        txtKeahlian.setText("");
        txtSearch.setText("");
        table.clearSelection();
        selectedRow = -1;
        selectedId = -1;
        loadData();
    }

    void kembaliKeMain() {
        new MainFrame().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmPanitia().setVisible(true));
    }
}