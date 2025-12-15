package frontend;
import backend.PesertaBackend;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class FrmPeserta extends JFrame {
    JComboBox<ComboItem> cmbEvent;
    JTextField txtNama, txtEmail, txtHp;
    JRadioButton rbManual, rbImport;
    JButton btnDaftar, btnImport, btnRefresh;
    JTable tblPeserta;
    DefaultTableModel tableModel;
    PesertaBackend backend;

    public FrmPeserta() {
        backend = new PesertaBackend();
        setTitle("Form Peserta");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Panel untuk form input
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Pilih Event:"));
        cmbEvent = new JComboBox<>();
        inputPanel.add(cmbEvent);

        inputPanel.add(new JLabel("Pilihan Input:"));
        JPanel panelRadio = new JPanel();
        rbManual = new JRadioButton("Manual", true);
        rbImport = new JRadioButton("Import CSV");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbManual);
        bg.add(rbImport);
        panelRadio.add(rbManual);
        panelRadio.add(rbImport);
        inputPanel.add(panelRadio);

        inputPanel.add(new JLabel("Nama Peserta:"));
        txtNama = new JTextField();
        inputPanel.add(txtNama);

        inputPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        inputPanel.add(txtEmail);

        inputPanel.add(new JLabel("No HP:"));
        txtHp = new JTextField();
        inputPanel.add(txtHp);

        btnDaftar = new JButton("Daftar");
        inputPanel.add(btnDaftar);

        btnImport = new JButton("Pilih File CSV");
        inputPanel.add(btnImport);
        
        // Panel untuk tabel
        JPanel tablePanel = new JPanel(new BorderLayout());
        
        // Membuat model tabel
        String[] columnNames = {"ID", "Nama Peserta", "Email", "No HP", "Event"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Membuat sel tidak bisa diedit
            }
        };
        tblPeserta = new JTable(tableModel);
        
        // Mengatur lebar kolom
        tblPeserta.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tblPeserta.getColumnModel().getColumn(1).setPreferredWidth(150); // Nama
        tblPeserta.getColumnModel().getColumn(2).setPreferredWidth(150); // Email
        tblPeserta.getColumnModel().getColumn(3).setPreferredWidth(100); // No HP
        tblPeserta.getColumnModel().getColumn(4).setPreferredWidth(150); // Nama Event
        
        // Menambahkan scroll pane untuk tabel
        JScrollPane scrollPane = new JScrollPane(tblPeserta);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Tombol refresh
        btnRefresh = new JButton("Refresh Data");
        btnRefresh.addActionListener(e -> refreshTable());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnRefresh);
        
        // Menambahkan komponen ke frame
        add(inputPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadEvent();
        setupListeners();
        refreshTable();
    }

    void loadEvent() {
        ArrayList<Object[]> events = backend.getListEvent();
        cmbEvent.removeAllItems();
        for (Object[] event : events) {
            cmbEvent.addItem(new ComboItem((String) event[1], (Integer) event[0]));
        }
        // Add listener for event selection change
        cmbEvent.addActionListener(e -> refreshTable());
    }

    void setupListeners() {
        rbManual.addActionListener(e -> toggleInputMode(true));
        rbImport.addActionListener(e -> toggleInputMode(false));

        btnDaftar.addActionListener(e -> {
            daftarManual();
            refreshTable(); // Refresh tabel setelah menambah data
        });
        btnImport.addActionListener(e -> {
            importCSV();
            refreshTable(); // Refresh tabel setelah import
        });
    }

    void toggleInputMode(boolean manual) {
        txtNama.setEnabled(manual);
        txtEmail.setEnabled(manual);
        txtHp.setEnabled(manual);
        btnDaftar.setEnabled(manual);
        btnImport.setEnabled(!manual);
    }

    void refreshTable() {
        // Kosongkan tabel
        tableModel.setRowCount(0);
        
        try {
            if (cmbEvent.getSelectedItem() != null) {
                int selectedEventId = ((ComboItem) cmbEvent.getSelectedItem()).getValue();
                ArrayList<Object[]> dataPeserta = backend.getListPeserta(selectedEventId);
                for (Object[] row : dataPeserta) {
                    // Hanya ambil kolom yang diperlukan (ID, Nama, Email, No HP, Nama Event)
                    Object[] rowData = {row[0], row[1], row[2], row[3], row[5]};
                    tableModel.addRow(rowData);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error memuat data: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    void daftarManual() {
        if (cmbEvent.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih event terlebih dahulu!");
            return;
        }
        String nama = txtNama.getText();
        String email = txtEmail.getText();
        String hp = txtHp.getText();
        int eventId = ((ComboItem) cmbEvent.getSelectedItem()).getValue();

        if (backend.simpanPesertaManual(nama, email, hp, eventId)) {
            JOptionPane.showMessageDialog(this, "Berhasil Daftar!");
            txtNama.setText("");
            txtEmail.setText("");
            txtHp.setText("");
        }
    }

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
                JOptionPane.showMessageDialog(this, "Berhasil Import!");
            }
        }
    }
}
