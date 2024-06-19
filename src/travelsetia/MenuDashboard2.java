/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package travelsetia;

import java.util.HashSet;
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
public class MenuDashboard2 extends javax.swing.JPanel {

    private DefaultTableModel model = new DefaultTableModel();
    private Connection conn;

    public MenuDashboard2() {
        initComponents();
        loadDataToTable();
    }

    private void loadDataToTable() {

        conn = Koneksi.bukaKoneksi();
        System.out.println(conn);
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
            tabelDash.setModel(model);
            tabelDash.setDefaultEditor(Object.class, null); // Nonaktifkan pengeditan sel
            tabelDash.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Hanya pilih satu baris
            labelUpdatePesawat.setText("Total Pesawat: " + hitungPesawat());
            labelUpdateCustomer.setText("Total Customer: " + hitungCustomer());
            labelUpdateBandara.setText("Total Bandara: " + hitungBandara());

        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    
     private int hitungPesawat() {
        int totalPesawat = 0;
        String sql = "SELECT COUNT(*) AS total FROM pesawat";

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                totalPesawat = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving total aircraft: " + e.getMessage());
        }

        return totalPesawat;
    }
     
     private int hitungBandara() {
        int totalBandara= 0;
        String sql = "SELECT COUNT(*) AS totalBandara FROM bandara";

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                totalBandara = rs.getInt("totalBandara");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving total aircraft: " + e.getMessage());
        }

        return totalBandara;
    }
     
     
     private int hitungCustomer() {
        int totalCustomer= 0;
        String sql = "SELECT COUNT(*) AS totalCustomer FROM detail_transaksi";

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                totalCustomer = rs.getInt("totalCustomer");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving total aircraft: " + e.getMessage());
        }

        return totalCustomer;
    }
     
     

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        PanelPesawat = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        labelUpdatePesawat = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        PanelKursi = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        labelUpdateCustomer = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        PanelBandara = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        labelUpdateBandara = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelDash = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        iconDash = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(928, 599));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PanelPesawat.setBackground(new java.awt.Color(53, 114, 239));
        PanelPesawat.setPreferredSize(new java.awt.Dimension(133, 130));

        jPanel9.setBackground(new java.awt.Color(51, 153, 255));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Pesawat");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        labelUpdatePesawat.setBackground(new java.awt.Color(255, 255, 255));
        labelUpdatePesawat.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        labelUpdatePesawat.setForeground(new java.awt.Color(255, 255, 255));
        labelUpdatePesawat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelUpdatePesawat.setText("9999");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Plane.png"))); // NOI18N

        javax.swing.GroupLayout PanelPesawatLayout = new javax.swing.GroupLayout(PanelPesawat);
        PanelPesawat.setLayout(PanelPesawatLayout);
        PanelPesawatLayout.setHorizontalGroup(
            PanelPesawatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelUpdatePesawat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(PanelPesawatLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel7)
                .addContainerGap(60, Short.MAX_VALUE))
        );
        PanelPesawatLayout.setVerticalGroup(
            PanelPesawatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPesawatLayout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelUpdatePesawat, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.add(PanelPesawat, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 50, 150, 130));

        PanelKursi.setBackground(new java.awt.Color(53, 114, 239));
        PanelKursi.setPreferredSize(new java.awt.Dimension(133, 130));

        jPanel8.setBackground(new java.awt.Color(51, 153, 255));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Data Penumpang");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        labelUpdateCustomer.setBackground(new java.awt.Color(255, 255, 255));
        labelUpdateCustomer.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        labelUpdateCustomer.setForeground(new java.awt.Color(255, 255, 255));
        labelUpdateCustomer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelUpdateCustomer.setText("9999");

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Sitting on Chair.png"))); // NOI18N

        javax.swing.GroupLayout PanelKursiLayout = new javax.swing.GroupLayout(PanelKursi);
        PanelKursi.setLayout(PanelKursiLayout);
        PanelKursiLayout.setHorizontalGroup(
            PanelKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelUpdateCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(PanelKursiLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jLabel9)
                .addContainerGap(61, Short.MAX_VALUE))
        );
        PanelKursiLayout.setVerticalGroup(
            PanelKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelKursiLayout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelUpdateCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.add(PanelKursi, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 50, 150, 130));

        PanelBandara.setBackground(new java.awt.Color(53, 114, 239));
        PanelBandara.setPreferredSize(new java.awt.Dimension(133, 130));

        jPanel6.setBackground(new java.awt.Color(51, 153, 255));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Bandara");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        labelUpdateBandara.setBackground(new java.awt.Color(255, 255, 255));
        labelUpdateBandara.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        labelUpdateBandara.setForeground(new java.awt.Color(255, 255, 255));
        labelUpdateBandara.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelUpdateBandara.setText("9999");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Airport.png"))); // NOI18N

        javax.swing.GroupLayout PanelBandaraLayout = new javax.swing.GroupLayout(PanelBandara);
        PanelBandara.setLayout(PanelBandaraLayout);
        PanelBandaraLayout.setHorizontalGroup(
            PanelBandaraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelUpdateBandara, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelBandaraLayout.createSequentialGroup()
                .addContainerGap(62, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(58, 58, 58))
        );
        PanelBandaraLayout.setVerticalGroup(
            PanelBandaraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBandaraLayout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addComponent(labelUpdateBandara, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(PanelBandara, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 50, 150, 130));

        tabelDash.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabelDash);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 240, 760, 320));

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(53, 114, 239));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("LAPORAN");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 210, 180, 30));

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(53, 114, 239));
        jLabel2.setText("Master Data > Dashboard");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 0, 200, 30));

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(53, 114, 239));
        jLabel3.setText("JUMLAH YANG BEROPRASI");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, -1, -1));

        iconDash.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconDash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Laptop Metrics.png"))); // NOI18N
        jPanel1.add(iconDash, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, 40, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 930, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 625, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelBandara;
    private javax.swing.JPanel PanelKursi;
    private javax.swing.JPanel PanelPesawat;
    private javax.swing.JLabel iconDash;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelUpdateBandara;
    private javax.swing.JLabel labelUpdateCustomer;
    private javax.swing.JLabel labelUpdatePesawat;
    private javax.swing.JTable tabelDash;
    // End of variables declaration//GEN-END:variables
}
