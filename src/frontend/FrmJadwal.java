package frontend;
import backend.JadwalBackend;
import backend.JadwalBackend.EventItem;
import backend.JadwalBackend.Jadwal;
import backend.JadwalBackend.PanitiaItem;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;

public class FrmJadwal extends JFrame {
    JComboBox<EventItem> cmbEvent;
    JComboBox<PanitiaItem> cmbPanitia;
    JTextField txtAgenda, txtJabatan, txtMulai, txtSelesai, txtSearch;
    DefaultTableModel model;
    JTable table;
    JadwalBackend dao = new JadwalBackend();
    int selectedRow = -1;
    int selectedId = -1;

    public FrmJadwal() {
        setTitle("Form Jadwal");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ===== PANEL INPUT (TOP) =====
        JPanel pInput = new JPanel(new GridLayout(7, 2, 8, 8));
        pInput.setBorder(BorderFactory.createTitledBorder("Input Data Jadwal"));
        pInput.setBackground(new Color(240, 240, 240));

        // Event
        pInput.add(new JLabel("Pilih Event:"));
        cmbEvent = new JComboBox<>();
        cmbEvent.addActionListener(e -> {
            loadPanitia();
            filterByEvent();
        });
        pInput.add(cmbEvent);

        // Panitia
        pInput.add(new JLabel("Pilih Panitia:"));
        cmbPanitia = new JComboBox<>();
        cmbPanitia.addActionListener(e -> updateJabatan());
        pInput.add(cmbPanitia);

        // Nama Agenda
        pInput.add(new JLabel("Nama Agenda:"));
        txtAgenda = new JTextField();
        pInput.add(txtAgenda);

        // Jabatan
        pInput.add(new JLabel("Jabatan:"));
        txtJabatan = new JTextField();
        txtJabatan.setEditable(false);
        txtJabatan.setBackground(new Color(220, 220, 220));
        pInput.add(txtJabatan);

        // Jam Mulai
        pInput.add(new JLabel("Jam Mulai (HH:MM:SS):"));
        txtMulai = new JTextField();
        txtMulai.setText("08:00:00");
        txtMulai.setForeground(new Color(150, 150, 150));
        pInput.add(txtMulai);

        // Jam Selesai
        pInput.add(new JLabel("Jam Selesai (HH:MM:SS):"));
        txtSelesai = new JTextField();
        txtSelesai.setText("17:00:00");
        txtSelesai.setForeground(new Color(150, 150, 150));
        pInput.add(txtSelesai);

        // Button Panel
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        pBtn.setBackground(new Color(240, 240, 240));
        
        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        btnSimpan.setBackground(new Color(34, 139, 34));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.addActionListener(e -> simpan());

        JButton btnHapus = new JButton("Hapus");
        btnHapus.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        btnHapus.setBackground(new Color(220, 20, 60));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.addActionListener(e -> hapus());

        JButton btnReset = new JButton("Reset");
        btnReset.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        btnReset.setBackground(new Color(70, 130, 180));
        btnReset.setForeground(Color.WHITE);
        btnReset.addActionListener(e -> reset());

        JButton btnBack = new JButton("Kembali");
        btnBack.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        btnBack.setBackground(new Color(105, 105, 105));
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> kembaliKeMain());

        JButton btnExportPdf = new JButton("Export PDF");
        btnExportPdf.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        btnExportPdf.setBackground(new Color(220, 20, 60));
        btnExportPdf.setForeground(Color.WHITE);
        btnExportPdf.addActionListener(e -> exportToPdf());

        pBtn.add(btnSimpan);
        pBtn.add(btnHapus);
        pBtn.add(btnReset);
        pBtn.add(btnBack);
        pBtn.add(btnExportPdf);
        pInput.add(pBtn);

        JPanel pTop = new JPanel(new BorderLayout());
        pTop.add(pInput, BorderLayout.CENTER);
        pTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(pTop, BorderLayout.NORTH);

        // ===== PANEL TABLE (CENTER) =====
        model = new DefaultTableModel(new String[]{"ID", "Event", "Agenda", "Panitia", "Jabatan", "Jam Mulai", "Jam Selesai"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));
        table.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pilihBaris();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Data Jadwal"));
        add(scrollPane, BorderLayout.CENTER);

        // ===== PANEL SEARCH (BOTTOM) =====
        JPanel pSearch = new JPanel(new BorderLayout(5, 5));
        pSearch.setBorder(BorderFactory.createTitledBorder("Pencarian"));
        pSearch.setBackground(new Color(240, 240, 240));

        pSearch.add(new JLabel("Cari Data:"), BorderLayout.WEST);
        
        txtSearch = new JTextField();
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                cari();
            }
        });
        pSearch.add(txtSearch, BorderLayout.CENTER);

        JButton btnClear = new JButton("Bersihkan");
        btnClear.setBackground(new Color(100, 149, 237));
        btnClear.setForeground(Color.WHITE);
        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });
        pSearch.add(btnClear, BorderLayout.EAST);
        
        JPanel pSearchContainer = new JPanel(new BorderLayout());
        pSearchContainer.add(pSearch, BorderLayout.CENTER);
        pSearchContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(pSearchContainer, BorderLayout.SOUTH);

        // Tambah focus listener untuk placeholder jam
        addPlaceholderListener(txtMulai, "08:00:00");
        addPlaceholderListener(txtSelesai, "17:00:00");

        loadEvent();
        loadData();
    }

    void addPlaceholderListener(JTextField txt, String placeholder) {
        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txt.getText().equals(placeholder)) {
                    txt.setText("");
                    txt.setForeground(new Color(0, 0, 0));
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txt.getText().isEmpty()) {
                    txt.setText(placeholder);
                    txt.setForeground(new Color(150, 150, 150));
                }
            }
        });
    }

    void loadEvent() {
        try {
            cmbEvent.removeAllItems();
            List<EventItem> events = dao.getAllEvent();
            for (EventItem e : events) {
                cmbEvent.addItem(e);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    void loadPanitia() {
        EventItem selected = (EventItem) cmbEvent.getSelectedItem();
        if (selected == null) {
            return;
        }
        
        try {
            cmbPanitia.removeAllItems();
            List<PanitiaItem> panitias = dao.getPanitiaByEventId(selected.id);
            for (PanitiaItem p : panitias) {
                cmbPanitia.addItem(p);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    void updateJabatan() {
        PanitiaItem selected = (PanitiaItem) cmbPanitia.getSelectedItem();
        if (selected != null) {
            txtJabatan.setText(selected.jabatan);
        }
    }

    void loadData() {
        try {
            model.setRowCount(0);
            List<Jadwal> jadwals = dao.getAllJadwal();
            for (Jadwal j : jadwals) {
                model.addRow(new Object[]{j.id, j.nama_event, j.nama_agenda, j.nama_karyawan, j.jabatan, j.waktu_mulai, j.waktu_selesai});
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
                model.addRow(new Object[]{j.id, j.nama_event, j.nama_agenda, j.nama_karyawan, j.jabatan, j.waktu_mulai, j.waktu_selesai});
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
                    loadPanitia();
                    // Set panitia combo
                    for (int i = 0; i < cmbPanitia.getItemCount(); i++) {
                        if (cmbPanitia.getItemAt(i).id == j.panitia_id) {
                            cmbPanitia.setSelectedIndex(i);
                            break;
                        }
                    }
                    txtAgenda.setText(j.nama_agenda);
                    txtJabatan.setText(j.jabatan);
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
            // Validasi - cek jika masih placeholder
            String jamMulai = txtMulai.getText();
            String jamSelesai = txtSelesai.getText();
            
            if (txtAgenda.getText().isEmpty() || txtJabatan.getText().isEmpty() ||
                jamMulai.isEmpty() || jamMulai.equals("08:00:00") ||
                jamSelesai.isEmpty() || jamSelesai.equals("17:00:00")) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi dengan data yang benar!");
                return;
            }

            EventItem eventSelected = (EventItem) cmbEvent.getSelectedItem();
            PanitiaItem panitiaSelected = (PanitiaItem) cmbPanitia.getSelectedItem();
            
            if (eventSelected == null || panitiaSelected == null) {
                JOptionPane.showMessageDialog(this, "Pilih event dan panitia terlebih dahulu!");
                return;
            }

            if (selectedId == -1) {
                // Insert
                dao.insertJadwal(eventSelected.id, panitiaSelected.id, txtAgenda.getText(),
                        jamMulai, jamSelesai);
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            } else {
                // Update
                dao.updateJadwal(selectedId, eventSelected.id, panitiaSelected.id, txtAgenda.getText(),
                        jamMulai, jamSelesai);
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
        cmbPanitia.removeAllItems();
        txtAgenda.setText("");
        txtJabatan.setText("");
        txtMulai.setText("08:00:00");
        txtMulai.setForeground(new Color(150, 150, 150));
        txtSelesai.setText("17:00:00");
        txtSelesai.setForeground(new Color(150, 150, 150));
        txtSearch.setText("");
        table.clearSelection();
        selectedRow = -1;
        selectedId = -1;
        loadData();
    }

    void filterByEvent() {
        try {
            EventItem selected = (EventItem) cmbEvent.getSelectedItem();
            if (selected == null) {
                loadData();
                return;
            }
            
            model.setRowCount(0);
            List<Jadwal> jadwals = dao.getAllJadwal();
            for (Jadwal j : jadwals) {
                if (j.event_id == selected.id) {
                    model.addRow(new Object[]{j.id, j.nama_event, j.nama_agenda, j.nama_karyawan, j.jabatan, j.waktu_mulai, j.waktu_selesai});
                }
            }
    void exportToPdf() {
        java.io.FileOutputStream fileOut = null;
        Document document = null;
        
        try {
            // Cek apakah ada data untuk diekspor
            if (model == null || model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Tidak ada data untuk diekspor!");
                return;
            }

            // Konfigurasi file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Simpan File PDF");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));
            
            int result = fileChooser.showSaveDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            // Pastikan ekstensi .pdf
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            // Cek apakah file bisa ditulis
            File outputFile = new File(filePath);
            if (outputFile.exists() && !outputFile.canWrite()) {
                JOptionPane.showMessageDialog(this, "Tidak dapat menulis ke file yang dipilih!");
                return;
            }

            // Inisialisasi dokumen PDF
            document = new Document(PageSize.A4.rotate());
            fileOut = new java.io.FileOutputStream(filePath);
            PdfWriter writer = PdfWriter.getInstance(document, fileOut);
            
            document.open();

            // Judul dokumen
            Paragraph title = new Paragraph("Laporan Data Jadwal", 
                new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Info event yang dipilih
            EventItem eventSelected = (EventItem) cmbEvent.getSelectedItem();
            if (eventSelected != null) {
                Paragraph eventInfo = new Paragraph("Event: " + eventSelected.nama, 
                    new Font(Font.FontFamily.HELVETICA, 12));
                eventInfo.setAlignment(Element.ALIGN_CENTER);
                document.add(eventInfo);
            }
            
            // Spasi
            document.add(new Paragraph("\n"));

            // Buat tabel PDF
            PdfPTable pdfTable = new PdfPTable(7);
            pdfTable.setWidthPercentage(100);
            
            // Header tabel
            String[] headers = {"ID", "Event", "Agenda", "Panitia", "Jabatan", "Jam Mulai", "Jam Selesai"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, 
                    new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
                cell.setBackgroundColor(new BaseColor(200, 200, 200));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }

            // Isi tabel
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < 7; j++) {
                    try {
                        Object value = model.getValueAt(i, j);
                        PdfPCell cell = new PdfPCell(new Phrase(
                            value != null ? value.toString() : "-", 
                            new Font(Font.FontFamily.HELVETICA, 10)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setPadding(5);
                        pdfTable.addCell(cell);
                    } catch (Exception e) {
                        // Tangani error saat mengambil data dari model
                        PdfPCell cell = new PdfPCell(new Phrase("-", 
                            new Font(Font.FontFamily.HELVETICA, 10)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(cell);
                    }
                }
            }

            document.add(pdfTable);
            
            JOptionPane.showMessageDialog(this, 
                "<html>File PDF berhasil disimpan di:<br>" + 
                filePath.replace("\\", "\\\\") + "</html>");
                
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Gagal menyimpan file. Pastikan file tidak sedang digunakan oleh program lain.\n" +
                "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Terjadi kesalahan saat mengekspor ke PDF:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            // Pastikan dokumen ditutup dengan benar
            if (document != null && document.isOpen()) {
                document.close();
            }
            // Tutup file output
            if (fileOut != null) {
                try { fileOut.close(); } catch (Exception e) {}
            }
        }
    }

    void kembaliKeMain() {
        this.dispose();
        new MainFrame().setVisible(true);
    }
}