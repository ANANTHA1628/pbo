import backend.PesertaBackend;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class FrmPeserta extends JFrame {
    JComboBox<ComboItem> cmbEvent;
    JTextField txtNama, txtAsal, txtHp;
    JRadioButton rbManual, rbImport;
    JButton btnDaftar, btnImport;
    PesertaBackend backend;

    public FrmPeserta() {
        backend = new PesertaBackend();
        setTitle("Form Peserta");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2));

        add(new JLabel("Pilih Event:"));
        cmbEvent = new JComboBox<>();
        add(cmbEvent);

        add(new JLabel("Pilihan Input:"));
        JPanel panelRadio = new JPanel();
        rbManual = new JRadioButton("Manual", true);
        rbImport = new JRadioButton("Import CSV");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbManual);
        bg.add(rbImport);
        panelRadio.add(rbManual);
        panelRadio.add(rbImport);
        add(panelRadio);

        add(new JLabel("Nama Peserta:"));
        txtNama = new JTextField();
        add(txtNama);

        add(new JLabel("Asal:"));
        txtAsal = new JTextField();
        add(txtAsal);

        add(new JLabel("No HP:"));
        txtHp = new JTextField();
        add(txtHp);

        btnDaftar = new JButton("Daftar");
        add(btnDaftar);

        btnImport = new JButton("Pilih File CSV");
        add(btnImport);

        loadEvent();
        setupListeners();
    }

    void loadEvent() {
        ArrayList<Object[]> events = backend.getListEvent();
        for (Object[] event : events) {
            cmbEvent.addItem(new ComboItem((String) event[1], (Integer) event[0]));
        }
    }

    void setupListeners() {
        rbManual.addActionListener(e -> toggleInputMode(true));
        rbImport.addActionListener(e -> toggleInputMode(false));

        btnDaftar.addActionListener(e -> daftarManual());
        btnImport.addActionListener(e -> importCSV());
    }

    void toggleInputMode(boolean manual) {
        txtNama.setEnabled(manual);
        txtAsal.setEnabled(manual);
        txtHp.setEnabled(manual);
        btnDaftar.setEnabled(manual);
        btnImport.setEnabled(!manual);
    }

    void daftarManual() {
        String nama = txtNama.getText();
        String asal = txtAsal.getText();
        String hp = txtHp.getText();
        int eventId = ((ComboItem) cmbEvent.getSelectedItem()).getValue();

        if (backend.simpanPesertaManual(nama, asal, hp, eventId)) {
            JOptionPane.showMessageDialog(this, "Berhasil Daftar!");
            txtNama.setText("");
            txtAsal.setText("");
            txtHp.setText("");
        }
    }

    void importCSV() {
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
