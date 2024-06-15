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
        String sql = "SELECT p.idPesawat, p.namaPesawat, b.namaBandara AS kotaKeberangkatan, p.destinasi, jp.tanggalKeberangkatan, p.kursiTersedia, p.harga, p.statusKursi\n"
                + "FROM pesawat p \n"
                + "LEFT JOIN bandara b ON p.destinasi = b.kota\n"
                + "LEFT JOIN jadwalpenerbangan jp ON p.idPesawat = jp.idPesawat\n"
                + "ORDER BY jp.tanggalKeberangkatan ASC;";
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new Object[]{"ID Pesawat", "Nama Pesawat", "Kota Keberangkatan", "Destinasi", "Tanggal Keberangkatan", "Kursi Tersedia", "Harga", "Status Kursi"});

            while (rs.next()) {
                
                String statusKursi;
                if (rs.getInt("kursiTersedia") > 0){
                    statusKursi = "ada";
                } else {
                    statusKursi = " habis ";
                }
                model.addRow(new Object[]{
                    rs.getInt("idPesawat"),
                    rs.getString("namaPesawat"),
                    rs.getString("kotaKeberangkatan"),
                    rs.getString("destinasi"),
                    rs.getString("tanggalKeberangkatan"),
                    rs.getInt("kursiTersedia"),
                    rs.getInt("harga"),
                    statusKursi
                    
                });
            }

            tabelPesawatAdmin.setModel(model);
            tabelPesawatAdmin.setDefaultEditor(Object.class, null);

        } catch (Exception ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    private void insertRow() {
        String namaPesawat = tfMaskapai.getText();
        String kotaKeberangkatan = tfKotaKeberangkatan.getText();
        String destinasi = tfDestinasi.getText();
        String tanggalBerangkat = tfTanggalBerangkat.getText();
        int kursiTersedia = Integer.parseInt(tfkursiTersedia.getText());
        int harga = Integer.parseInt(txtTotalBayar.getText());

        // Query INSERT yang benar
        String sql = "INSERT INTO pesawat (namaPesawat, destinasi, kursiTersedia, harga) VALUES (?, ?, ?, ?)";
        try {
            // Menggunakan RETURN_GENERATED_KEYS untuk mendapatkan kunci yang dihasilkan
            PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, namaPesawat);
            pst.setString(2, destinasi);
            pst.setInt(3, kursiTersedia);
            pst.setInt(4, harga);
            pst.executeUpdate();
            System.out.println("Row inserted successfully.");

            // Dapatkan ID yang dihasilkan
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                int idPesawat = rs.getInt(1);

                // Tambahkan baris baru ke model tabel
                DefaultTableModel model = (DefaultTableModel) tabelPesawatAdmin.getModel();
                model.addRow(new Object[]{idPesawat, namaPesawat, kotaKeberangkatan, destinasi, tanggalBerangkat, kursiTersedia, harga});
            }
        } catch (Exception ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    private void updateTextFields() {
        int row = tabelPesawatAdmin.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tabelPesawatAdmin.getModel();
        String namaPesawat = model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "";
        String kotaKeberangkatan = model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "";
        String destinasi = model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "";
        String waktuKeberangkatan = model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "";
        String kursiTersedia = model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "";
        String harga = model.getValueAt(row, 6) != null ? model.getValueAt(row, 6).toString() : "";

        tfMaskapai.setText(namaPesawat);
        tfKotaKeberangkatan.setText(kotaKeberangkatan);
        tfDestinasi.setText(destinasi);
        tfTanggalBerangkat.setText(waktuKeberangkatan);
        tfkursiTersedia.setText(kursiTersedia);
        txtTotalBayar.setText(harga);
    }

    private void updateSelectedRow() {
        int row = tabelPesawatAdmin.getSelectedRow();
        if (row != -1) {
            DefaultTableModel model = (DefaultTableModel) tabelPesawatAdmin.getModel();
            int idPesawat = (int) model.getValueAt(row, 0);

            String namaPesawat = tfMaskapai.getText();
            String kotaKeberangkatan = tfKotaKeberangkatan.getText();
            String destinasi = tfDestinasi.getText();
            String tanggalBerangkat = tfTanggalBerangkat.getText();
            int kursiTersedia = Integer.parseInt(tfkursiTersedia.getText());
            int harga = Integer.parseInt(txtTotalBayar.getText());

            // Update di database
            String sql = "UPDATE pesawat SET namaPesawat = ?, destinasi = ?, kursiTersedia = ?, harga = ? WHERE idPesawat = ?";
            try {
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, namaPesawat);
                pst.setString(2, destinasi);
                pst.setInt(3, kursiTersedia);
                pst.setInt(4, harga);
                pst.setInt(5, idPesawat);
                pst.executeUpdate();
                System.out.println("Row updated successfully.");
            } catch (Exception ex) {
                System.out.println("Error : " + ex.getMessage());
            }

            // Update di tabel
            model.setValueAt(namaPesawat, row, 1);
            model.setValueAt(kotaKeberangkatan, row, 2);
            model.setValueAt(destinasi, row, 3);
            model.setValueAt(tanggalBerangkat, row, 4);
            model.setValueAt(kursiTersedia, row, 5);
            model.setValueAt(harga, row, 6);
        }
    }

    private void deleteSelectedRow() {
        int row = tabelPesawatAdmin.getSelectedRow();
        if (row != -1) {
            DefaultTableModel model = (DefaultTableModel) tabelPesawatAdmin.getModel();
            int idPesawat = (int) model.getValueAt(row, 0);

            // Hapus dari database
            String sql = "DELETE FROM pesawat WHERE idPesawat = ?";
            try {
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, idPesawat);
                pst.executeUpdate();
                System.out.println("Row deleted successfully.");
            } catch (Exception ex) {
                System.out.println("Error : " + ex.getMessage());
            }

            // Hapus dari tabel
            model.removeRow(row);
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
        txtTotalBayar = new javax.swing.JTextField();
        tfMaskapai = new javax.swing.JTextField();
        tfKotaKeberangkatan = new javax.swing.JTextField();
        tfDestinasi = new javax.swing.JTextField();
        tfTanggalBerangkat = new javax.swing.JTextField();
        tfkursiTersedia = new javax.swing.JTextField();
        txtTotalBayar1 = new javax.swing.JTextField();

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

        txtTotalBayar.setForeground(new java.awt.Color(153, 153, 153));
        txtTotalBayar.setText("Harga");
        txtTotalBayar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTotalBayarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTotalBayarFocusLost(evt);
            }
        });
        txtTotalBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalBayarActionPerformed(evt);
            }
        });
        add(txtTotalBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 190, 290, 30));

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
        add(tfMaskapai, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, 300, 70));

        tfKotaKeberangkatan.setForeground(new java.awt.Color(153, 153, 153));
        tfKotaKeberangkatan.setText("Kota Keberangkatan");
        tfKotaKeberangkatan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfKotaKeberangkatanFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfKotaKeberangkatanFocusLost(evt);
            }
        });
        tfKotaKeberangkatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfKotaKeberangkatanActionPerformed(evt);
            }
        });
        add(tfKotaKeberangkatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 150, 300, 30));

        tfDestinasi.setForeground(new java.awt.Color(153, 153, 153));
        tfDestinasi.setText("Destinasi");
        tfDestinasi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfDestinasiFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfDestinasiFocusLost(evt);
            }
        });
        tfDestinasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDestinasiActionPerformed(evt);
            }
        });
        add(tfDestinasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 190, 300, 30));

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
        add(tfTanggalBerangkat, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 70, 290, 30));

        tfkursiTersedia.setForeground(new java.awt.Color(153, 153, 153));
        tfkursiTersedia.setText("Kursi Tersedia");
        tfkursiTersedia.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfkursiTersediaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfkursiTersediaFocusLost(evt);
            }
        });
        tfkursiTersedia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfkursiTersediaActionPerformed(evt);
            }
        });
        add(tfkursiTersedia, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 110, 290, 30));

        txtTotalBayar1.setForeground(new java.awt.Color(153, 153, 153));
        txtTotalBayar1.setText("Harga");
        txtTotalBayar1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTotalBayar1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTotalBayar1FocusLost(evt);
            }
        });
        txtTotalBayar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalBayar1ActionPerformed(evt);
            }
        });
        add(txtTotalBayar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 150, 290, 30));
    }// </editor-fold>//GEN-END:initComponents

    private void tfkursiTersediaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfkursiTersediaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfkursiTersediaActionPerformed

    private void tfMaskapaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfMaskapaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfMaskapaiActionPerformed

    private void txtTotalBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalBayarActionPerformed

    private void tfDestinasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDestinasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDestinasiActionPerformed

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

    private void tfKotaKeberangkatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfKotaKeberangkatanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfKotaKeberangkatanActionPerformed

    private void tfKotaKeberangkatanFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfKotaKeberangkatanFocusGained
        if (tfKotaKeberangkatan.getText().equals("Kota Keberangkatan")) {
            tfKotaKeberangkatan.setText("");
            tfKotaKeberangkatan.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfKotaKeberangkatanFocusGained

    private void tfKotaKeberangkatanFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfKotaKeberangkatanFocusLost
        if (tfKotaKeberangkatan.getText().equals("")) {
            tfKotaKeberangkatan.setText("Kota Keberangkata");
            tfKotaKeberangkatan.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_tfKotaKeberangkatanFocusLost

    private void tfDestinasiFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfDestinasiFocusGained
        if (tfDestinasi.getText().equals("Destinasi")) {
            tfDestinasi.setText("");
            tfDestinasi.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfDestinasiFocusGained

    private void tfDestinasiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfDestinasiFocusLost
        if (tfDestinasi.getText().equals("")) {
            tfDestinasi.setText("Destinasi");
            tfDestinasi.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfDestinasiFocusLost

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

    private void tfkursiTersediaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfkursiTersediaFocusGained
        if (tfkursiTersedia.getText().equals("Kursi Tersedia")) {
            tfkursiTersedia.setText("");
            tfkursiTersedia.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfkursiTersediaFocusGained

    private void tfkursiTersediaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfkursiTersediaFocusLost
        if (tfkursiTersedia.getText().equals("")) {
            tfkursiTersedia.setText("Kursi Tersedia");
            tfkursiTersedia.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_tfkursiTersediaFocusLost

    private void txtTotalBayarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTotalBayarFocusGained
        if (txtTotalBayar.getText().equals("Harga")) {
            txtTotalBayar.setText("");
            txtTotalBayar.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_txtTotalBayarFocusGained

    private void txtTotalBayarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTotalBayarFocusLost
        if (txtTotalBayar.getText().equals("")) {
            txtTotalBayar.setText("Harga");
            txtTotalBayar.setForeground(new Color(153, 153, 153));
        }

    }//GEN-LAST:event_txtTotalBayarFocusLost

    private void txtTotalBayar1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTotalBayar1FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalBayar1FocusGained

    private void txtTotalBayar1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTotalBayar1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalBayar1FocusLost

    private void txtTotalBayar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalBayar1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalBayar1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonHapus;
    private javax.swing.JButton buttonTambah;
    private javax.swing.JButton buttonUbah;
    private javax.swing.JLabel iconPesawat;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tabelPesawatAdmin;
    private javax.swing.JTextField tfDestinasi;
    private javax.swing.JTextField tfKotaKeberangkatan;
    private javax.swing.JTextField tfMaskapai;
    private javax.swing.JTextField tfTanggalBerangkat;
    private javax.swing.JTextField tfkursiTersedia;
    private javax.swing.JTextField txtTotalBayar;
    private javax.swing.JTextField txtTotalBayar1;
    // End of variables declaration//GEN-END:variables
}
