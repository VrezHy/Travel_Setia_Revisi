/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package travelsetia;

import java.awt.Color;
import java.sql.Statement;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author dimas
 */
public class MenuPesawat1 extends javax.swing.JPanel {

    /**
     * Creates new form MenuPesawat1
     */
    private Connection conn;

    public MenuPesawat1() {
        initComponents();
        loadDataToTable();

        tabelPesawatAdmin.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && tabelPesawatAdmin.getSelectedRow() != -1) {
                updateTextFields();
            }
        });
    }

    private void loadDataToTable() {
    conn = Koneksi.bukaKoneksi();
    System.out.println(conn);
    String sql = "SELECT j.idPenerbangan, j.bandaraKeberangkatan, j.bandaraTujuan, j.tanggalKeberangkatan, j.tanggalKedatangan, p.namaPesawat, p.harga, k.kursiTersedia, t.tiketTersedia FROM jadwalpenerbangan j LEFT JOIN pesawat p ON j.idPesawat = p.idPesawat LEFT JOIN kapasitas_kursi k ON j.idPenerbangan = k.idPenerbangan LEFT JOIN ketersediaan_tiket t ON j.idPenerbangan = t.idKetersediaan;";
    try (PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
        // Inisialisasi DefaultTableModel dengan kolom yang ditentukan
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
            "Nomor Penerbangan",
            "Bandara Keberangkatan",
            "Bandara Tujuan",
            "Maskapai",
            "Tanggal Keberangkatan",
            "Tanggal Kedatangan",
            "Harga",
            "Kursi Tersedia",
            "Tiket",
        });

        // Memproses ResultSet dan menambahkan data ke model
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("idPenerbangan"),
                rs.getString("bandaraKeberangkatan"),
                rs.getString("bandaraTujuan"),
                rs.getString("namaPesawat"),
                rs.getString("tanggalKeberangkatan"),
                rs.getString("tanggalKedatangan"),
                rs.getDouble("harga"),
                rs.getInt("kursiTersedia"),
                rs.getInt("tiketTersedia"),
            });
        }

        // Set the model to your JTable
        tabelPesawatAdmin.setModel(model);
        tabelPesawatAdmin.setDefaultEditor(Object.class, null); // Nonaktifkan pengeditan sel
        tabelPesawatAdmin.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Hanya pilih satu baris

    } catch (SQLException ex) {
        System.out.println(ex);
    }
}


    private void insertRow() {
    String namaPesawat = tfMaskapai.getText();
    String bandaraKeberangkatan = tfBandaraKeberangkatan.getText();
    String bandaraTujuan = tfBandaraKedatangan.getText();
    String tanggalKeberangkatan = tfTanggalBerangkat.getText();
    String tanggalKedatangan = tfTanggalKedatangan.getText();
    int kursiTersedia = Integer.parseInt(tfKursiTersedia.getText());
    int harga = Integer.parseInt(tfHarga.getText());
    int tiketTersedia = Integer.parseInt(tfTiket.getText());

    // Query INSERT yang benar
    String sqlPesawat = "INSERT INTO pesawat (namaPesawat, harga) VALUES (?, ?)";
    try {
        // Menggunakan RETURN_GENERATED_KEYS untuk mendapatkan kunci yang dihasilkan
        PreparedStatement pstPesawat = conn.prepareStatement(sqlPesawat, Statement.RETURN_GENERATED_KEYS);
        pstPesawat.setString(1, namaPesawat);
        pstPesawat.setInt(2, harga);
        pstPesawat.executeUpdate();
        System.out.println("Row inserted successfully into pesawat.");

        // Dapatkan ID yang dihasilkan
        ResultSet rsPesawat = pstPesawat.getGeneratedKeys();
        if (rsPesawat.next()) {
            int idPesawat = rsPesawat.getInt(1);

            // Insert data into jadwalpenerbangan
            String sqlJadwal = "INSERT INTO jadwalpenerbangan (idPesawat, bandaraKeberangkatan, bandaraTujuan, tanggalKeberangkatan, tanggalKedatangan) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstJadwal = conn.prepareStatement(sqlJadwal, Statement.RETURN_GENERATED_KEYS);
            pstJadwal.setInt(1, idPesawat);
            pstJadwal.setString(2, bandaraKeberangkatan);
            pstJadwal.setString(3, bandaraTujuan);
            pstJadwal.setString(4, tanggalKeberangkatan);
            pstJadwal.setString(5, tanggalKedatangan);
            pstJadwal.executeUpdate();
            System.out.println("Row inserted successfully into jadwalpenerbangan.");

            ResultSet rsJadwal = pstJadwal.getGeneratedKeys();
            if (rsJadwal.next()) {
                int idPenerbangan = rsJadwal.getInt(1);

                // Insert data into kapasitas_kursi
                String sqlKursi = "INSERT INTO kapasitas_kursi (idPenerbangan, kursiTersedia) VALUES (?, ?)";
                PreparedStatement pstKursi = conn.prepareStatement(sqlKursi);
                pstKursi.setInt(1, idPenerbangan);
                pstKursi.setInt(2, kursiTersedia);
                pstKursi.executeUpdate();
                System.out.println("Row inserted successfully into kapasitas_kursi.");

                // Insert data into ketersediaan_tiket
                String sqlTiket = "INSERT INTO ketersediaan_tiket (idPenerbangan, tiketTersedia) VALUES (?, ?)";
                PreparedStatement pstTiket = conn.prepareStatement(sqlTiket);
                pstTiket.setInt(1, idPenerbangan);
                pstTiket.setInt(2, tiketTersedia);
                pstTiket.executeUpdate();
                System.out.println("Row inserted successfully into ketersediaan_tiket.");

                // Tambahkan baris baru ke model tabel
                DefaultTableModel model = (DefaultTableModel) tabelPesawatAdmin.getModel();
                model.addRow(new Object[]{idPenerbangan, bandaraKeberangkatan, bandaraTujuan, namaPesawat, tanggalKeberangkatan, tanggalKedatangan, harga, kursiTersedia, tiketTersedia});
            }
        }
    } catch (Exception ex) {
        System.out.println("Error : " + ex.getMessage());
    }
}


    private void updateTextFields() {
        int row = tabelPesawatAdmin.getSelectedRow();
        row = tabelPesawatAdmin.convertRowIndexToModel(row);
        DefaultTableModel model = (DefaultTableModel) tabelPesawatAdmin.getModel();

        // Ambil nilai berdasarkan urutan kolom yang sesuai
        String bandaraKeberangkatan = model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "";
        String bandaraTujuan = model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "";
        String namaPesawat = model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "";
        String tanggalKeberangkatan = model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "";
        String tanggalKedatangan = model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "";
        String totalHarga = model.getValueAt(row, 6) != null ? model.getValueAt(row, 6).toString() : "";
        String kursiTersedia = model.getValueAt(row, 7) != null ? model.getValueAt(row, 7).toString() : "";
        String tiket = model.getValueAt(row, 8) != null ? model.getValueAt(row, 8).toString() : "";
        

        tfBandaraKeberangkatan.setText(bandaraKeberangkatan);
        tfBandaraKedatangan.setText(bandaraTujuan);
        tfTanggalBerangkat.setText(tanggalKeberangkatan);
        tfTanggalKedatangan.setText(tanggalKedatangan); //
        tfMaskapai.setText(namaPesawat); //
        tfKursiTersedia.setText(kursiTersedia);
        tfHarga.setText(totalHarga);
        tfTiket.setText(tiket);
        

    }

    private void updateSelectedRow() {
    int row = tabelPesawatAdmin.getSelectedRow();
    if (row != -1) {
        DefaultTableModel model = (DefaultTableModel) tabelPesawatAdmin.getModel();
        int idPenerbangan = Integer.parseInt(model.getValueAt(row, 0).toString());  // Ensure correct type conversion

        String namaPesawat = tfMaskapai.getText();
        String bandaraKeberangkatan = tfBandaraKeberangkatan.getText();
        String bandaraTujuan = tfBandaraKedatangan.getText();
        String tanggalKeberangkatan = tfTanggalBerangkat.getText();
        String tanggalKedatangan = tfTanggalKedatangan.getText();
        int kursiTersedia = Integer.parseInt(tfKursiTersedia.getText());
        int harga = Integer.parseInt(tfHarga.getText());
        int tiketTersedia = Integer.parseInt(tfTiket.getText());

        try {
            // Update pesawat
            String sqlPesawat = "UPDATE pesawat SET namaPesawat = ?, harga = ? WHERE idPesawat = (SELECT idPesawat FROM jadwalpenerbangan WHERE idPenerbangan = ?)";
            PreparedStatement pstPesawat = conn.prepareStatement(sqlPesawat);
            pstPesawat.setString(1, namaPesawat);
            pstPesawat.setInt(2, harga);
            pstPesawat.setInt(3, idPenerbangan);
            pstPesawat.executeUpdate();

            // Update jadwalpenerbangan
            String sqlJadwal = "UPDATE jadwalpenerbangan SET bandaraKeberangkatan = ?, bandaraTujuan = ?, tanggalKeberangkatan = ?, tanggalKedatangan = ? WHERE idPenerbangan = ?";
            PreparedStatement pstJadwal = conn.prepareStatement(sqlJadwal);
            pstJadwal.setString(1, bandaraKeberangkatan);
            pstJadwal.setString(2, bandaraTujuan);
            pstJadwal.setString(3, tanggalKeberangkatan);
            pstJadwal.setString(4, tanggalKedatangan);
            pstJadwal.setInt(5, idPenerbangan);
            pstJadwal.executeUpdate();

            // Update kapasitas_kursi
            String sqlKursi = "UPDATE kapasitas_kursi SET kursiTersedia = ? WHERE idPenerbangan = ?";
            PreparedStatement pstKursi = conn.prepareStatement(sqlKursi);
            pstKursi.setInt(1, kursiTersedia);
            pstKursi.setInt(2, idPenerbangan);
            pstKursi.executeUpdate();

            // Update ketersediaan_tiket
            String sqlTiket = "UPDATE ketersediaan_tiket SET tiketTersedia = ? WHERE idPenerbangan = ?";
            PreparedStatement pstTiket = conn.prepareStatement(sqlTiket);
            pstTiket.setInt(1, tiketTersedia);
            pstTiket.setInt(2, idPenerbangan);
            pstTiket.executeUpdate();

            System.out.println("Row updated successfully.");

            // Update table model
            model.setValueAt(namaPesawat, row, 3);
            model.setValueAt(bandaraKeberangkatan, row, 1);
            model.setValueAt(bandaraTujuan, row, 2);
            model.setValueAt(tanggalKeberangkatan, row, 4);
            model.setValueAt(tanggalKedatangan, row, 5);
            model.setValueAt(harga, row, 6);
            model.setValueAt(kursiTersedia, row, 7);
            model.setValueAt(tiketTersedia, row, 8);

        } catch (SQLException ex) {
            
            System.out.println("Error : " + ex.getMessage());
        }
    }
}

    
    private void deleteSelectedRow() {
    int row = tabelPesawatAdmin.getSelectedRow();
    if (row != -1) {
        DefaultTableModel model = (DefaultTableModel) tabelPesawatAdmin.getModel();
        int idPenerbangan = Integer.parseInt(model.getValueAt(row, 0).toString());  // Ensure correct type conversion

        try {
            // Delete from ketersediaan_tiket
            String sqlTiket = "DELETE FROM ketersediaan_tiket WHERE idPenerbangan = ?";
            PreparedStatement pstTiket = conn.prepareStatement(sqlTiket);
            pstTiket.setInt(1, idPenerbangan);
            pstTiket.executeUpdate();

            // Delete from kapasitas_kursi
            String sqlKursi = "DELETE FROM kapasitas_kursi WHERE idPenerbangan = ?";
            PreparedStatement pstKursi = conn.prepareStatement(sqlKursi);
            pstKursi.setInt(1, idPenerbangan);
            pstKursi.executeUpdate();

            // Delete from jadwalpenerbangan
            String sqlJadwal = "DELETE FROM jadwalpenerbangan WHERE idPenerbangan = ?";
            PreparedStatement pstJadwal = conn.prepareStatement(sqlJadwal);
            pstJadwal.setInt(1, idPenerbangan);
            pstJadwal.executeUpdate();

            // Delete from pesawat (using subquery to find idPesawat)
            String sqlPesawat = "DELETE FROM pesawat WHERE idPesawat = (SELECT idPesawat FROM jadwalpenerbangan WHERE idPenerbangan = ?)";
            PreparedStatement pstPesawat = conn.prepareStatement(sqlPesawat);
            pstPesawat.setInt(1, idPenerbangan);
            pstPesawat.executeUpdate();

            System.out.println("Row deleted successfully.");

            // Remove row from table model
            model.removeRow(row);

        } catch (SQLException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }
}




    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        tabelPesawatAdmin = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        buttonTambah = new javax.swing.JButton();
        buttonUbah = new javax.swing.JButton();
        buttonHapus = new javax.swing.JButton();
        iconPesawat = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tfTiket = new javax.swing.JTextField();
        tfMaskapai = new javax.swing.JTextField();
        tfBandaraKeberangkatan = new javax.swing.JTextField();
        tfBandaraKedatangan = new javax.swing.JTextField();
        tfTanggalBerangkat = new javax.swing.JTextField();
        tfKursiTersedia = new javax.swing.JTextField();
        tfHarga = new javax.swing.JTextField();
        tfTanggalKedatangan = new javax.swing.JTextField();

        setBackground(new java.awt.Color(53, 114, 239));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabelPesawatAdmin.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ));
        tabelPesawatAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelPesawatAdminMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabelPesawatAdmin);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 250, 760, 310));

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Data Pesawat");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, 120, 30));

        buttonTambah.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        buttonTambah.setForeground(new java.awt.Color(102, 153, 255));
        buttonTambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Plus.png"))); // NOI18N
        buttonTambah.setText("Tambah");
        buttonTambah.setBorder(null);
        buttonTambah.setPreferredSize(new java.awt.Dimension(78, 25));
        buttonTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTambahActionPerformed(evt);
            }
        });
        add(buttonTambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 70, 130, 50));

        buttonUbah.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        buttonUbah.setForeground(new java.awt.Color(102, 153, 255));
        buttonUbah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Change.png"))); // NOI18N
        buttonUbah.setText("Ubah");
        buttonUbah.setBorder(null);
        buttonUbah.setPreferredSize(new java.awt.Dimension(78, 25));
        buttonUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUbahActionPerformed(evt);
            }
        });
        add(buttonUbah, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 130, 130, 50));

        buttonHapus.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        buttonHapus.setForeground(new java.awt.Color(102, 153, 255));
        buttonHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Minus.png"))); // NOI18N
        buttonHapus.setText("Hapus");
        buttonHapus.setBorder(null);
        buttonHapus.setPreferredSize(new java.awt.Dimension(78, 25));
        buttonHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHapusActionPerformed(evt);
            }
        });
        add(buttonHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 190, 130, 50));

        iconPesawat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Plane.png"))); // NOI18N
        add(iconPesawat, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, -1, -1));

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Master Data > Pesawat");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 0, 260, 30));

        tfTiket.setForeground(new java.awt.Color(153, 153, 153));
        tfTiket.setText("Tiket");
        tfTiket.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfTiketFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfTiketFocusLost(evt);
            }
        });
        tfTiket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTiketActionPerformed(evt);
            }
        });
        add(tfTiket, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 190, 290, 30));

        tfMaskapai.setForeground(new java.awt.Color(153, 153, 153));
        tfMaskapai.setText("Nama Pesawat");
        tfMaskapai.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfMaskapaiFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfMaskapaiFocusLost(evt);
            }
        });
        tfMaskapai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfMaskapaiActionPerformed(evt);
            }
        });
        add(tfMaskapai, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, 300, 30));

        tfBandaraKeberangkatan.setForeground(new java.awt.Color(153, 153, 153));
        tfBandaraKeberangkatan.setText("Bandara Keberangkatan");
        tfBandaraKeberangkatan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfBandaraKeberangkatanFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfBandaraKeberangkatanFocusLost(evt);
            }
        });
        tfBandaraKeberangkatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfBandaraKeberangkatanActionPerformed(evt);
            }
        });
        add(tfBandaraKeberangkatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 300, 30));

        tfBandaraKedatangan.setForeground(new java.awt.Color(153, 153, 153));
        tfBandaraKedatangan.setText("Bandara Kedatangan");
        tfBandaraKedatangan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfBandaraKedatanganFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfBandaraKedatanganFocusLost(evt);
            }
        });
        tfBandaraKedatangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfBandaraKedatanganActionPerformed(evt);
            }
        });
        add(tfBandaraKedatangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 150, 300, 30));

        tfTanggalBerangkat.setForeground(new java.awt.Color(153, 153, 153));
        tfTanggalBerangkat.setText("Tanggal Keberangkatan");
        tfTanggalBerangkat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfTanggalBerangkatFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfTanggalBerangkatFocusLost(evt);
            }
        });
        add(tfTanggalBerangkat, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 190, 300, 30));

        tfKursiTersedia.setForeground(new java.awt.Color(153, 153, 153));
        tfKursiTersedia.setText("Kursi Tersedia");
        tfKursiTersedia.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfKursiTersediaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfKursiTersediaFocusLost(evt);
            }
        });
        tfKursiTersedia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfKursiTersediaActionPerformed(evt);
            }
        });
        add(tfKursiTersedia, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 110, 290, 30));

        tfHarga.setForeground(new java.awt.Color(153, 153, 153));
        tfHarga.setText("Harga");
        tfHarga.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfHargaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfHargaFocusLost(evt);
            }
        });
        tfHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfHargaActionPerformed(evt);
            }
        });
        add(tfHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 150, 290, 30));

        tfTanggalKedatangan.setForeground(new java.awt.Color(153, 153, 153));
        tfTanggalKedatangan.setText("Tanggal Kedatangan");
        tfTanggalKedatangan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfTanggalKedatanganFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfTanggalKedatanganFocusLost(evt);
            }
        });
        add(tfTanggalKedatangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 70, 290, 30));
    }// </editor-fold>//GEN-END:initComponents

    private void tfKursiTersediaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfKursiTersediaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfKursiTersediaActionPerformed

    private void tfMaskapaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfMaskapaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfMaskapaiActionPerformed

    private void tfTiketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfTiketActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfTiketActionPerformed

    private void tfBandaraKedatanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfBandaraKedatanganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfBandaraKedatanganActionPerformed

    private void tabelPesawatAdminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelPesawatAdminMouseClicked
        updateTextFields();
    }//GEN-LAST:event_tabelPesawatAdminMouseClicked

    private void buttonHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHapusActionPerformed
        deleteSelectedRow();
    }//GEN-LAST:event_buttonHapusActionPerformed

    private void buttonUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUbahActionPerformed
        updateSelectedRow();
    }//GEN-LAST:event_buttonUbahActionPerformed

    private void buttonTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTambahActionPerformed
        insertRow();
    }//GEN-LAST:event_buttonTambahActionPerformed

    private void tfMaskapaiFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfMaskapaiFocusGained
        if (tfMaskapai.getText().equals("Nama Pesawat")) {
            tfMaskapai.setText("");
            tfMaskapai.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfMaskapaiFocusGained

    private void tfMaskapaiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfMaskapaiFocusLost
        if (tfMaskapai.getText().equals("")) {
            tfMaskapai.setText("Nama Pesawat");
            tfMaskapai.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfMaskapaiFocusLost

    private void tfBandaraKeberangkatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfBandaraKeberangkatanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfBandaraKeberangkatanActionPerformed

    private void tfBandaraKeberangkatanFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfBandaraKeberangkatanFocusGained
        if (tfBandaraKeberangkatan.getText().equals("Bandara Keberangkatan")) {
            tfBandaraKeberangkatan.setText("");
            tfBandaraKeberangkatan.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfBandaraKeberangkatanFocusGained

    private void tfBandaraKeberangkatanFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfBandaraKeberangkatanFocusLost
        if (tfBandaraKeberangkatan.getText().equals("")) {
            tfBandaraKeberangkatan.setText("Bandara Keberangkatan");
            tfBandaraKeberangkatan.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_tfBandaraKeberangkatanFocusLost

    private void tfBandaraKedatanganFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfBandaraKedatanganFocusGained
        if (tfBandaraKedatangan.getText().equals("Destinasi")) {
            tfBandaraKedatangan.setText("");
            tfBandaraKedatangan.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfBandaraKedatanganFocusGained

    private void tfBandaraKedatanganFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfBandaraKedatanganFocusLost
        if (tfBandaraKedatangan.getText().equals("")) {
            tfBandaraKedatangan.setText("Destinasi");
            tfBandaraKedatangan.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfBandaraKedatanganFocusLost

    private void tfTanggalBerangkatFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfTanggalBerangkatFocusGained
        if (tfTanggalBerangkat.getText().equals("Tanggal Keberangkatan")) {
            tfTanggalBerangkat.setText("");
            tfTanggalBerangkat.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfTanggalBerangkatFocusGained

    private void tfTanggalBerangkatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfTanggalBerangkatFocusLost
        if (tfTanggalBerangkat.getText().equals("")) {
            tfTanggalBerangkat.setText("Tanggal Keberangkatan");
            tfTanggalBerangkat.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfTanggalBerangkatFocusLost

    private void tfKursiTersediaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfKursiTersediaFocusGained
        if (tfKursiTersedia.getText().equals("Kursi Tersedia")) {
            tfKursiTersedia.setText("");
            tfKursiTersedia.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfKursiTersediaFocusGained

    private void tfKursiTersediaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfKursiTersediaFocusLost
        if (tfKursiTersedia.getText().equals("")) {
            tfKursiTersedia.setText("Kursi Tersedia");
            tfKursiTersedia.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfKursiTersediaFocusLost

    private void tfTiketFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfTiketFocusGained
        if (tfTiket.getText().equals("Harga")) {
            tfTiket.setText("");
            tfTiket.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfTiketFocusGained

    private void tfTiketFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfTiketFocusLost
        if (tfTiket.getText().equals("")) {
            tfTiket.setText("Harga");
            tfTiket.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfTiketFocusLost

    private void tfHargaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfHargaFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_tfHargaFocusGained

    private void tfHargaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfHargaFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tfHargaFocusLost

    private void tfHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfHargaActionPerformed

    private void tfTanggalKedatanganFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfTanggalKedatanganFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_tfTanggalKedatanganFocusGained

    private void tfTanggalKedatanganFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfTanggalKedatanganFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tfTanggalKedatanganFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonHapus;
    private javax.swing.JButton buttonTambah;
    private javax.swing.JButton buttonUbah;
    private javax.swing.JLabel iconPesawat;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tabelPesawatAdmin;
    private javax.swing.JTextField tfBandaraKeberangkatan;
    private javax.swing.JTextField tfBandaraKedatangan;
    private javax.swing.JTextField tfHarga;
    private javax.swing.JTextField tfKursiTersedia;
    private javax.swing.JTextField tfMaskapai;
    private javax.swing.JTextField tfTanggalBerangkat;
    private javax.swing.JTextField tfTanggalKedatangan;
    private javax.swing.JTextField tfTiket;
    // End of variables declaration//GEN-END:variables
}
