package frontend;
import backend.JadwalBackend;
import backend.JadwalBackend.EventItem;
import backend.JadwalBackend.Jadwal;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmJadwal extends JFrame {
    JComboBox<EventItem> cmbEvent;
    JTextField txtAgenda, txtPengisi, txtMulai, txtSelesai, txtSearch;
    DefaultTableModel model;
    JTable table;
    JadwalBackend dao = new JadwalBackend();
    int selectedRow = -1;
    int selectedId = -1;

    public FrmJadwal() {
        setTitle("Form Jadwal");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel Input
        JPanel pInput = new JPanel(new GridLayout(6, 2, 5, 5));
        pInput.setBorder(BorderFactory.createTitledBorder("Input Data"));
        pInput.add(new JLabel("Pilih Event:"));
        cmbEvent = new JComboBox<>();
        pInput.add(cmbEvent);

        pInput.add(new JLabel("Nama Agenda:"));
        txtAgenda = new JTextField();
        pInput.add(txtAgenda);

        pInput.add(new JLabel("Pengisi Acara:"));
        txtPengisi = new JTextField();
        pInput.add(txtPengisi);

        pInput.add(new JLabel("Jam Mulai:"));
        txtMulai = new JTextField();
        pInput.add(txtMulai);

        pInput.add(new JLabel("Jam Selesai:"));
        txtSelesai = new JTextField();
        pInput.add(txtSelesai);

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton btnSimpan = new JButton("Simpan");
        JButton btnHapus = new JButton("Hapus");
        JButton btnReset = new JButton("Reset");
        btnSimpan.addActionListener(e -> simpan());
        btnHapus.addActionListener(e -> hapus());
        btnReset.addActionListener(e -> reset());
        pBtn.add(btnSimpan);
        pBtn.add(btnHapus);
        pBtn.add(btnReset);
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
        model = new DefaultTableModel(new String[]{"ID", "Event", "Agenda", "Pengisi Acara", "Jam Mulai", "Jam Selesai"}, 0) {
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

        loadEvent();
        loadData();
    }

    void loadEvent() {
        try {
            List<EventItem> events = dao.getAllEvent();
            cmbEvent.removeAllItems();
            for (EventItem e : events) {
                cmbEvent.addItem(e);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    void loadData() {
        try {
            model.setRowCount(0);
            List<Jadwal> jadwals = dao.getAllJadwal();
            for (Jadwal j : jadwals) {
                model.addRow(new Object[]{j.id, j.nama_event, j.nama_agenda, j.pengisi_acara, j.waktu_mulai, j.waktu_selesai});
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
            
            List<Jadwal> jadwals = dao.searchJadwal(keyword);
            if (jadwals.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan!");
                loadData();
                return;
            }
            
            for (Jadwal j : jadwals) {
                model.addRow(new Object[]{j.id, j.nama_event, j.nama_agenda, j.pengisi_acara, j.waktu_mulai, j.waktu_selesai});
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
                Jadwal j = dao.getJadwalById(selectedId);
                if (j != null) {
                    // Set combo box sesuai event_id
                    for (int i = 0; i < cmbEvent.getItemCount(); i++) {
                        if (cmbEvent.getItemAt(i).id == j.event_id) {
                            cmbEvent.setSelectedIndex(i);
                            break;
                        }
                    }
                    txtAgenda.setText(j.nama_agenda);
                    txtPengisi.setText(j.pengisi_acara);
                    txtMulai.setText(j.waktu_mulai);
                    txtSelesai.setText(j.waktu_selesai);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    void simpan() {
        try {
            if (txtAgenda.getText().isEmpty() || txtPengisi.getText().isEmpty() ||
                txtMulai.getText().isEmpty() || txtSelesai.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
                return;
            }

            EventItem selected = (EventItem) cmbEvent.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Pilih event terlebih dahulu!");
                return;
            }

            if (selectedId == -1) {
                // Insert
                dao.insertJadwal(selected.id, txtAgenda.getText(), txtPengisi.getText(),
                        txtMulai.getText(), txtSelesai.getText());
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            } else {
                // Update
                dao.updateJadwal(selectedId, selected.id, txtAgenda.getText(), txtPengisi.getText(),
                        txtMulai.getText(), txtSelesai.getText());
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
                dao.deleteJadwal(selectedId);
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
        txtAgenda.setText("");
        txtPengisi.setText("");
        txtMulai.setText("");
        txtSelesai.setText("");
        txtSearch.setText("");
        table.clearSelection();
        selectedRow = -1;
        selectedId = -1;
        loadData();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmJadwal().setVisible(true));
    }
}