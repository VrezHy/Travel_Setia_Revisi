/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package travelsetia;

import java.awt.Color;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author dimas
 */
public class MenuCustomer extends javax.swing.JFrame {

    /**
     * Creates new form MenuCustomer
     */
    private DefaultTableModel model = new DefaultTableModel();
    private Connection conn;

    public MenuCustomer() {
        initComponents();
        txtCariPenerbangan.setBackground(new java.awt.Color(0, 0, 0, 1));
        txtTotalBayar.setBackground(new java.awt.Color(0, 0, 0, 1));

        conn = Koneksi.bukaKoneksi();
        String sql = "SELECT j.idPenerbangan, j.bandaraKeberangkatan, j.bandaraTujuan, j.tanggalKeberangkatan, j.tanggalKedatangan, p.namaPesawat, p.harga, k.kursiTersedia "
                + "FROM jadwalpenerbangan j "
                + "LEFT JOIN pesawat p ON j.idPesawat = p.idPesawat "
                + "LEFT JOIN kapasitas_kursi k ON j.idPenerbangan = k.idPenerbangan";

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
                "Status Kursi"
            });

            // Memproses ResultSet dan menambahkan data ke model
            while (rs.next()) {
                String statusKursi;
                int idPenerbangan = rs.getInt("idPenerbangan");

                if (rs.getInt("kursiTersedia") > 0) {
                    statusKursi = "ada";
                } else {
                    statusKursi = "habis";
                }

                // Perbarui status kursi (pastikan metode ini benar)
                updateStatusKursi(idPenerbangan, statusKursi);

                model.addRow(new Object[]{
                    rs.getString("idPenerbangan"),
                    rs.getString("bandaraKeberangkatan"),
                    rs.getString("bandaraTujuan"),
                    rs.getString("namaPesawat"),
                    rs.getString("tanggalKeberangkatan"),
                    rs.getString("tanggalKedatangan"),
                    rs.getDouble("harga"),
                    rs.getInt("kursiTersedia"),
                    statusKursi
                });
            }

            // Set the model to your JTable
            jTablePesawat.setModel(model);
            jTablePesawat.setDefaultEditor(Object.class, null); // Nonaktifkan pengeditan sel
            jTablePesawat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Hanya pilih satu baris

        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

    }

    private void updateTextFields() {
        int row = jTablePesawat.getSelectedRow();
        row = jTablePesawat.convertRowIndexToModel(row);
        DefaultTableModel model = (DefaultTableModel) jTablePesawat.getModel();

        // Ambil nilai berdasarkan urutan kolom yang sesuai
        String bandaraKeberangkatan = model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "";
        String bandaraTujuan = model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "";
        String namaPesawat = model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "";
        String tanggalKeberangkatan = model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "";
        String tanggalKedatangan = model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "";
        String totalHarga = model.getValueAt(row, 6) != null ? model.getValueAt(row, 6).toString() : "";
        String kursiTersedia = model.getValueAt(row, 7) != null ? model.getValueAt(row, 7).toString() : "";
        String statusKursi = model.getValueAt(row, 8) != null ? model.getValueAt(row, 8).toString() : "";

        tfBandaraAsal.setText(bandaraKeberangkatan);
        tfBandaraTujuan.setText(bandaraTujuan);
        tfTanggalKeberangkatan.setText(tanggalKeberangkatan);
        tfTanggalKedatangan.setText(tanggalKedatangan); //
        tfMaskapai.setText(namaPesawat); //
        tfKursiTersedia.setText(kursiTersedia);
        tfStatusKursi.setText(statusKursi);
        txtTotalBayar.setText(totalHarga);

    }

    private void updateTotalPrice() {

        if (conn != null) {
            PreparedStatement pstmt;
            ResultSet rs;
            try {
                int jumlahTiket = Integer.parseInt(CBtiketPenumpang.getSelectedItem().toString());
                int selectedRow = jTablePesawat.getSelectedRow();
                if (selectedRow != -1) {
                    double harga = Double.parseDouble(jTablePesawat.getValueAt(selectedRow, 6).toString());
                    double totalHarga = harga * jumlahTiket;
                    txtTotalBayar.setText(String.valueOf(totalHarga));

                    // Get the idPenerbangan and kursiTersedia values
                    int idPenerbangan = Integer.parseInt(jTablePesawat.getValueAt(selectedRow, 0).toString());
                    int kursiTersedia = Integer.parseInt(jTablePesawat.getValueAt(selectedRow, 7).toString());

                    if (kursiTersedia >= jumlahTiket) {
                        conn = Koneksi.bukaKoneksi();
                        // Update kursiTersedia
                        kursiTersedia -= jumlahTiket;

                        // Update the database
                        // Update the JTable
                        jTablePesawat.setValueAt(kursiTersedia, selectedRow, 7);
                        jTablePesawat.setValueAt(kursiTersedia > 0 ? "ada" : "habis", selectedRow, 8);

                        String updateJumlahTiketQuery = "UPDATE ketersediaan_tiket SET jumlahTiket = jumlahTiket + ? WHERE idPenerbangan = ?";
                        pstmt = conn.prepareStatement(updateJumlahTiketQuery);
                        pstmt.setInt(1, jumlahTiket);
                        pstmt.setInt(2, idPenerbangan);
                        pstmt.executeUpdate();

                        String updatetiketTersedia = "UPDATE ketersediaan_tiket SET tiketTersedia = tiketTersedia - ? WHERE idPenerbangan = ?";
                        pstmt = conn.prepareStatement(updatetiketTersedia);
                        pstmt.setInt(1, jumlahTiket);
                        pstmt.setInt(2, idPenerbangan);
                        pstmt.executeUpdate();
                    } else {
                        JOptionPane.showMessageDialog(null, "Jumlah tiket melebihi kursi tersedia", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    txtTotalBayar.setText("0");
                }
            } catch (NumberFormatException e) {
                txtTotalBayar.setText("0");
                System.out.println("Error parsing number: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("SQL Error: " + e.getMessage());
            }
        }
    }

    private void updateStatusKursi(int idPenerbangan, String statusKursi) {
        String sql = "UPDATE kapasitas_kursi SET statusKursi = ? WHERE idKapasitas = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, statusKursi);
            pst.setInt(2, idPenerbangan); // Assuming idPenerbangan corresponds to idKapasitas in kapasitas_kursi
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating status kursi: " + e.getMessage());
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

        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton3 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLogout = new javax.swing.JButton();
        tfff9 = new javax.swing.JPanel();
        txtTotalBayar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnPesan = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        tfStatusKursi = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        CBtiketPenumpang = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        tfBandaraAsal = new javax.swing.JTextField();
        tfBandaraTujuan = new javax.swing.JTextField();
        tfTanggalKeberangkatan = new javax.swing.JTextField();
        tfTanggalKedatangan = new javax.swing.JTextField();
        tfMaskapai = new javax.swing.JTextField();
        tfKursiTersedia = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTablePesawat = new javax.swing.JTable();
        txtCariPenerbangan = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        BackroundCustomer = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        exit = new javax.swing.JLabel();
        minimize = new javax.swing.JLabel();

        jPanel2.setBackground(new java.awt.Color(0, 204, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("ID :");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Asal :");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Tujuan :");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Maskapai :");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Harga :");

        jLabel9.setText("Nama Pemesan :");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Tanggal Pemesanan :");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Jumlah Tiket :");

        jButton1.setText("Pesan");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Reset");

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jButton3.setText("Cetak Pembayaran");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel11))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel9)))
                                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1)
                            .addComponent(jTextField2)
                            .addComponent(jTextField3)
                            .addComponent(jTextField4)
                            .addComponent(jTextField5)
                            .addComponent(jTextField6)
                            .addComponent(jComboBox2, 0, 158, Short.MAX_VALUE)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton2)))))
                .addGap(23, 23, 23))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addGap(53, 53, 53))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(53, 114, 239));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(0, 204, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLogout.setText("Logout");
        jLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLogoutActionPerformed(evt);
            }
        });
        jPanel5.add(jLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 590, 90, 40));

        tfff9.setBackground(new java.awt.Color(53, 114, 239));
        tfff9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtTotalBayar.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        txtTotalBayar.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalBayar.setBorder(null);
        txtTotalBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalBayarActionPerformed(evt);
            }
        });
        tfff9.add(txtTotalBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 230, 280, 40));

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Status Kursi");
        tfff9.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 260, 140, 30));

        btnPesan.setText("Pesan Sekarang");
        btnPesan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesanActionPerformed(evt);
            }
        });
        tfff9.add(btnPesan, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 280, 280, -1));

        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("_______________________________________________________");
        tfff9.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 260, 280, -1));

        tfStatusKursi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfStatusKursiActionPerformed(evt);
            }
        });
        tfff9.add(tfStatusKursi, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 260, 370, 30));

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Jumlah Tiket Penumpang");
        tfff9.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 150, 210, 40));

        jLabel17.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Bandara Asal");
        tfff9.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 26, 140, 20));

        jLabel19.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Tanggal Keberangkatan");
        tfff9.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 170, 30));

        CBtiketPenumpang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4" }));
        CBtiketPenumpang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CBtiketPenumpangActionPerformed(evt);
            }
        });
        tfff9.add(CBtiketPenumpang, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 150, 70, 40));

        jLabel20.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Total Bayar");
        tfff9.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 200, 100, 30));

        jLabel21.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Maskapai");
        tfff9.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 180, 120, 30));

        jLabel16.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Bandara Tujuan");
        tfff9.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 140, 30));

        jLabel18.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Kursi Tersedia");
        tfff9.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 220, 150, 30));

        jLabel22.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Tanggal Kedatangan");
        tfff9.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 180, 30));

        tfBandaraAsal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfBandaraAsalActionPerformed(evt);
            }
        });
        tfff9.add(tfBandaraAsal, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, 370, 30));

        tfBandaraTujuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfBandaraTujuanActionPerformed(evt);
            }
        });
        tfff9.add(tfBandaraTujuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 60, 370, 30));

        tfTanggalKeberangkatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTanggalKeberangkatanActionPerformed(evt);
            }
        });
        tfff9.add(tfTanggalKeberangkatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, 370, 30));

        tfTanggalKedatangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTanggalKedatanganActionPerformed(evt);
            }
        });
        tfff9.add(tfTanggalKedatangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 180, 370, 30));

        tfMaskapai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfMaskapaiActionPerformed(evt);
            }
        });
        tfff9.add(tfMaskapai, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 140, 370, 30));

        tfKursiTersedia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfKursiTersediaActionPerformed(evt);
            }
        });
        tfff9.add(tfKursiTersedia, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 220, 370, 30));

        jPanel5.add(tfff9, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 220, 990, 360));

        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("_________________________________________________________");
        jPanel5.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 40, 280, -1));

        jTablePesawat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Maskapai", "Lokasi Keberangkatan", "Destinasi", "Tanggal Keberangkatan", "Status Kursi"
            }
        ));
        jTablePesawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTablePesawatMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTablePesawat);
        if (jTablePesawat.getColumnModel().getColumnCount() > 0) {
            jTablePesawat.getColumnModel().getColumn(1).setHeaderValue("Lokasi Keberangkatan");
            jTablePesawat.getColumnModel().getColumn(2).setHeaderValue("Destinasi");
            jTablePesawat.getColumnModel().getColumn(3).setHeaderValue("Tanggal Keberangkatan");
            jTablePesawat.getColumnModel().getColumn(4).setHeaderValue("Status Kursi");
        }

        jPanel5.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 990, 140));

        txtCariPenerbangan.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        txtCariPenerbangan.setForeground(new java.awt.Color(255, 255, 255));
        txtCariPenerbangan.setBorder(null);
        txtCariPenerbangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariPenerbanganActionPerformed(evt);
            }
        });
        jPanel5.add(txtCariPenerbangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 20, 280, 30));

        jButton5.setForeground(new java.awt.Color(53, 114, 239));
        jButton5.setText("Cari...");
        jButton5.setBorder(null);
        jButton5.setBorderPainted(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 20, 70, 30));

        jLabel13.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Cari penerbangan pesawatmu sekarang!");
        jPanel5.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 380, 40));

        BackroundCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/pexels-jerry-wang-2135752-3768652.jpg"))); // NOI18N
        BackroundCustomer.setText("jLabel1");
        jPanel5.add(BackroundCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -7, 1320, 680));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 101, 1320, 670));

        jLabel12.setFont(new java.awt.Font("Verdana", 1, 48)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("TRAVEL SETIA");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 40, -1, 50));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/icons8-palm-tree-50.png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, 50, 60));

        exit.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        exit.setForeground(new java.awt.Color(255, 255, 255));
        exit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        exit.setText("x");
        exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitMouseClicked(evt);
            }
        });
        jPanel1.add(exit, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 0, 40, 40));

        minimize.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        minimize.setForeground(new java.awt.Color(255, 255, 255));
        minimize.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minimize.setText("-");
        minimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                minimizeMouseClicked(evt);
            }
        });
        jPanel1.add(minimize, new org.netbeans.lib.awtextra.AbsoluteConstraints(1230, 0, 40, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_exitMouseClicked

    private void minimizeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizeMouseClicked
        // TODO add your handling code here:
        this.setExtendedState(MenuCustomer.ICONIFIED);
    }//GEN-LAST:event_minimizeMouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        DefaultTableModel BTCari = (DefaultTableModel) jTablePesawat.getModel();
        TableRowSorter<DefaultTableModel> Cari = new TableRowSorter<>(BTCari);
        jTablePesawat.setRowSorter(Cari);
        String searchText = txtCariPenerbangan.getText();

        if (searchText.trim().length() == 0) {
            Cari.setRowFilter(null);
        } else {
            Cari.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void txtCariPenerbanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariPenerbanganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariPenerbanganActionPerformed

    private void btnPesanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesanActionPerformed
        int selectedRow = jTablePesawat.getSelectedRow();
        if (selectedRow != -1) {
            try {
                int modelRowIndex = jTablePesawat.convertRowIndexToModel(selectedRow);
                DefaultTableModel model = (DefaultTableModel) jTablePesawat.getModel();

                String idBandaraStr = model.getValueAt(modelRowIndex, 0).toString();
                int idPenerbangan = Integer.parseInt(idBandaraStr);
                String bandaraKeberangkatan = model.getValueAt(modelRowIndex, 1).toString();
                String bandaraTujuan = model.getValueAt(modelRowIndex, 2).toString();
                String namaPesawat = model.getValueAt(modelRowIndex, 3).toString();
                String tanggalKeberangkatan = model.getValueAt(modelRowIndex, 4).toString();
                String kursiTersediaStr = model.getValueAt(modelRowIndex, 7).toString(); // Assuming kursiTersedia index is 7
                int kursiTersedia = Integer.parseInt(kursiTersediaStr);
                String statusKursi = model.getValueAt(modelRowIndex, 8).toString();
                int jumlahPenumpang = Integer.parseInt(CBtiketPenumpang.getSelectedItem().toString());// Assuming statusKursi index is 8

                // Assuming you get jumlahTiket from somewhere (e.g., a JComboBox)
                int jumlahTiket = Integer.parseInt(CBtiketPenumpang.getSelectedItem().toString());

                double harga = Double.parseDouble(model.getValueAt(modelRowIndex, 6).toString()); // Assuming harga index is 6
                double totalHarga = harga * jumlahTiket;

                // Pass parameters to MenuPesan constructor
                MenuPesan pesan = new MenuPesan(bandaraKeberangkatan, bandaraTujuan, namaPesawat,
                        tanggalKeberangkatan, totalHarga, jumlahTiket, kursiTersedia, idPenerbangan, jumlahPenumpang);
                pesan.setVisible(true);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Format harga atau kursi tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println(e);
                e.printStackTrace(); // For debugging
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println(e);
                e.printStackTrace(); // For debugging
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih penerbangan terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_btnPesanActionPerformed

    private void tfStatusKursiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfStatusKursiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfStatusKursiActionPerformed

    private void jTablePesawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTablePesawatMouseClicked
        updateTextFields();
    }//GEN-LAST:event_jTablePesawatMouseClicked

    private void CBtiketPenumpangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CBtiketPenumpangActionPerformed

        updateTotalPrice();


    }//GEN-LAST:event_CBtiketPenumpangActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        tfStatusKursi.setEditable(false);
        tfMaskapai.setEditable(false);
        tfBandaraAsal.setEditable(false);
        tfBandaraTujuan.setEditable(false);
        tfTanggalKedatangan.setEditable(false);
        tfTanggalKeberangkatan.setEditable(false);
        tfKursiTersedia.setEditable(false);
        txtTotalBayar.setEditable(false);
    }//GEN-LAST:event_formComponentShown

    private void jLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLogoutActionPerformed
        Login1 Login1Frame = new Login1();
        Login1Frame.setVisible(true);
        Login1Frame.pack();
        Login1Frame.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_jLogoutActionPerformed

    private void tfBandaraAsalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfBandaraAsalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfBandaraAsalActionPerformed

    private void tfBandaraTujuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfBandaraTujuanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfBandaraTujuanActionPerformed

    private void tfTanggalKeberangkatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfTanggalKeberangkatanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfTanggalKeberangkatanActionPerformed

    private void tfTanggalKedatanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfTanggalKedatanganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfTanggalKedatanganActionPerformed

    private void tfMaskapaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfMaskapaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfMaskapaiActionPerformed

    private void tfKursiTersediaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfKursiTersediaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfKursiTersediaActionPerformed

    private void txtTotalBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalBayarActionPerformed

    }//GEN-LAST:event_txtTotalBayarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MenuCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuCustomer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BackroundCustomer;
    private javax.swing.JComboBox<String> CBtiketPenumpang;
    private javax.swing.JButton btnPesan;
    private javax.swing.JLabel exit;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jLogout;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTablePesawat;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JLabel minimize;
    private javax.swing.JTextField tfBandaraAsal;
    private javax.swing.JTextField tfBandaraTujuan;
    private javax.swing.JTextField tfKursiTersedia;
    private javax.swing.JTextField tfMaskapai;
    private javax.swing.JTextField tfStatusKursi;
    private javax.swing.JTextField tfTanggalKeberangkatan;
    private javax.swing.JTextField tfTanggalKedatangan;
    private javax.swing.JPanel tfff9;
    private javax.swing.JTextField txtCariPenerbangan;
    private javax.swing.JTextField txtTotalBayar;
    // End of variables declaration//GEN-END:variables
}
