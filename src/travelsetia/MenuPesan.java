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
public class MenuPesan extends javax.swing.JFrame {

    
    
    private Connection conn;
     private String bandaraKeberangkatan;
    private String bandaraTujuan;
    private String namaPesawat;
    private String tanggalKeberangkatan;
    private double totalHarga;
    private int idPenerbangan; // Ensure this is set correctly in your actual code
    private int kursiTersedia;
    private int jumlahTiket;
     private int jumlahPenumpang;

    public MenuPesan(String bandaraKeberangkatan, String bandaraTujuan, String namaPesawat,
                     String tanggalKeberangkatan, double totalHarga, int jumlahTiket, int kursiTersedia, int idPenerbangan, int jumlahPenumpang)  {
        initComponents();
        this.bandaraKeberangkatan = bandaraKeberangkatan;
        this.bandaraTujuan = bandaraTujuan;
        this.namaPesawat = namaPesawat;
        this.tanggalKeberangkatan = tanggalKeberangkatan;
        this.totalHarga = totalHarga;
        this.kursiTersedia = kursiTersedia;
        this.jumlahTiket = jumlahTiket;
        this.idPenerbangan = idPenerbangan;
        this.jumlahPenumpang = jumlahPenumpang;
        
        // Set idPenerbangan accordingly if needed
        
        
        conn = Koneksi.bukaKoneksi();
        // Example usage to display data in labels
        
        labelTiket.setText("Total Tiket: " + getTiketTersedia(idPenerbangan));
        setupTextFieldBasedOnPassengerCount();
    }
    
    
        

    private void prosesBooking() {
        
        if (kursiTersedia >= jumlahTiket) {
            int kursiBaru = kursiTersedia - jumlahTiket;
            updateKursiTersedia(idPenerbangan, kursiBaru);
            JOptionPane.showMessageDialog(this, "Booking berhasil!");
        } else {
            JOptionPane.showMessageDialog(this, "Maaf, kursi untuk penerbangan ini sudah habis.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
        insertDetailBooking();
        saveNamesToDatabase();
        Koneksi.tutupKoneksi();
    }
    
    private void insertDetailBooking() {
        String sql = "INSERT INTO detail_booking (idDetail, idBooking, idPenumpang, tanggalBooking) VALUES (?, ?, ?, ?)";
        try  {
            Connection conn = Koneksi.bukaKoneksi(); 
            PreparedStatement pstmt = conn.prepareStatement(sql);
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

            int idDetail = generateIdDetail(); // Implement this method to generate a unique idDetail
            int idBooking = generateIdBooking(); // Implement this method to generate a unique idBooking
            int idPenumpang = generateIdPenumpang(); // Implement this method to generate a unique idPenumpang

            // Assuming you have a method to get passenger names based on the number of tickets
            if (jumlahPenumpang >= 1) {
                pstmt.setInt(1, idDetail);
                pstmt.setInt(2, idBooking);
                pstmt.setInt(3, idPenumpang);
                pstmt.setDate(4, currentDate);
                pstmt.addBatch();
                idDetail++;
                idBooking++;
                idPenumpang++;
            }
            if (jumlahPenumpang >= 2) {
                pstmt.setInt(1, idDetail);
                pstmt.setInt(2, idBooking);
                pstmt.setInt(3, idPenumpang);
                pstmt.setDate(4, currentDate);
                pstmt.addBatch();
                idDetail++;
                idBooking++;
                idPenumpang++;
            }
            if (jumlahPenumpang >= 3) {
                pstmt.setInt(1, idDetail);
                pstmt.setInt(2, idBooking);
                pstmt.setInt(3, idPenumpang);
                pstmt.setDate(4, currentDate);
                pstmt.addBatch();
                idDetail++;
                idBooking++;
                idPenumpang++;
            }

            pstmt.executeBatch();
        } catch (SQLException e){
            
        }
    } 

    private int generateIdDetail() {
        return 1; 
    }

    private int generateIdBooking() {
        return 1; 
    }

    private int generateIdPenumpang() {
        return 1; 
    }


    private void updateKursiTersedia(int idPenerbangan, int kursiTersedia) {
        if (conn != null){
             String sqlUpdate = "UPDATE kapasitas_kursi SET kursiTersedia = ? WHERE idPenerbangan = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(sqlUpdate);
            pst.setInt(1, kursiTersedia);
            pst.setInt(2, idPenerbangan);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating available seats: " + e.getMessage());
        }
        }

    }
    
    private int getTiketTersedia(int idPenerbangan) {
    int tiketTersedia = 0;
    String sql = "SELECT tiketTersedia FROM ketersediaan_tiket WHERE idPenerbangan = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, idPenerbangan);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            tiketTersedia = rs.getInt("tiketTersedia");
        }
    } catch (SQLException e) {
        System.out.println("Error retrieving available tickets: " + e.getMessage());
    }

    return tiketTersedia;
    
    
}
    
    
    
    private void setupTextFieldBasedOnPassengerCount() {
        // Sembunyikan semua text field terlebih dahulu
        panelPenumpang.setVisible(false);
        panelPenumpang2.setVisible(false);
        panelPenumpang3.setVisible(false);
        panelPenumpang4.setVisible(false);
        
        // Tampilkan text field sesuai dengan jumlah penumpang yang dipilih
        if (jumlahPenumpang >= 1) {
            panelPenumpang.setVisible(true);
        }
        if (jumlahPenumpang >= 2) {
            panelPenumpang2.setVisible(true);
        }
        if (jumlahPenumpang >= 3) {
            panelPenumpang3.setVisible(true);
        }
        if (jumlahPenumpang >= 4) {
            panelPenumpang4.setVisible(true);
        }
    }
    
 private void saveNamesToDatabase() {
        int idBooking = generateIdBookingTransaksi();
        try (Connection conn = Koneksi.bukaKoneksi()) {
            // Insert names into database for each passenger
            if (jumlahPenumpang >= 1) {
                saveNameToDatabase(tfNamaPenumpang1.getText(), conn, idBooking);
            }
            if (jumlahPenumpang >= 2) {
                saveNameToDatabase(tfNamaPenumpang2.getText(), conn, idBooking);
            }
            if (jumlahPenumpang >= 3) {
                saveNameToDatabase(tfNamaPenumpang3.getText(), conn, idBooking) ;
            }
            if (jumlahPenumpang >= 4) {
                saveNameToDatabase(tfNamaPenumpang4.getText(), conn, idBooking);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: Gagal menyimpan nama penumpang ke database.");
            e.printStackTrace();
        }
    }
 
 private int generateIdBookingTransaksi() {
        int newIdBooking = 0;
        String sql = "SELECT MAX(idBooking) AS maxId FROM detail_transaksi";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                newIdBooking = rs.getInt("maxId") + 1;
            }
        } catch (SQLException e) {
            System.out.println("Error generating new idBooking: " + e.getMessage());
        }
        return newIdBooking;
    }

    
    private void saveNameToDatabase(String namaPenumpang, Connection conn, int idBooking) throws SQLException {
        String sql = "INSERT INTO detail_transaksi (tanggalTransaksi, jumlahPembayaran, namaPenumpang, idPenerbangan, idBooking ) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(System.currentTimeMillis())); // Assuming you want to set the current date
        pstmt.setDouble(2, totalHarga); // Assuming this is the total payment amount
        pstmt.setString(3, namaPenumpang);
        pstmt.setInt(4, idPenerbangan);
        pstmt.setInt(5, idBooking);
        pstmt.executeUpdate();  
        }
    }
    
    private void cetakTiket(){
        if(jumlahTiket > 0){
         String message = String.format(
                        "Jumlah Tiket: %s\nTotal Harga: %s\nMaskapai: %s\nKota Keberangkatan: %s\nDestinasi: %s\nTanggal Berangkat: %s\n",
                        jumlahTiket, totalHarga, namaPesawat, bandaraKeberangkatan, bandaraTujuan, tanggalKeberangkatan
                );
         JOptionPane.showMessageDialog(this, message, "Rincian Pembayaran", JOptionPane.INFORMATION_MESSAGE);
    } else{
            JOptionPane.showMessageDialog(this, "Maaf, TIket untuk penerbangan ini sudah habis.", "Peringatan", JOptionPane.WARNING_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnKembaliRiwayat = new javax.swing.JToggleButton();
        minimize = new javax.swing.JLabel();
        exit = new javax.swing.JLabel();
        PanelKursi = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        labelTiket = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        panelPenumpang = new javax.swing.JPanel();
        tfNamaPenumpang1 = new javax.swing.JTextField();
        iconMember1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        panelPenumpang2 = new javax.swing.JPanel();
        tfNamaPenumpang2 = new javax.swing.JTextField();
        iconMember2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        panelPenumpang3 = new javax.swing.JPanel();
        tfNamaPenumpang3 = new javax.swing.JTextField();
        iconMember3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        panelPenumpang4 = new javax.swing.JPanel();
        tfNamaPenumpang4 = new javax.swing.JTextField();
        iconMember = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnCetakTiket = new javax.swing.JButton();
        buttonBayar = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(53, 114, 239));
        jPanel1.setPreferredSize(new java.awt.Dimension(928, 599));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Pesan Sekarang");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, 140, 30));

        btnKembaliRiwayat.setText("Kembali");
        btnKembaliRiwayat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliRiwayatActionPerformed(evt);
            }
        });
        jPanel1.add(btnKembaliRiwayat, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 460, 120, 40));

        minimize.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        minimize.setForeground(new java.awt.Color(255, 255, 255));
        minimize.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minimize.setText("-");
        minimize.setToolTipText("");
        minimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                minimizeMouseClicked(evt);
            }
        });
        jPanel1.add(minimize, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 0, 40, 40));

        exit.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        exit.setForeground(new java.awt.Color(255, 255, 255));
        exit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        exit.setText("x");
        exit.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitMouseClicked(evt);
            }
        });
        jPanel1.add(exit, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 0, 40, 40));

        PanelKursi.setBackground(new java.awt.Color(255, 255, 255));
        PanelKursi.setPreferredSize(new java.awt.Dimension(133, 130));

        jPanel8.setBackground(new java.awt.Color(51, 153, 255));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Tiket yang tersedia");

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

        labelTiket.setBackground(new java.awt.Color(255, 255, 255));
        labelTiket.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        labelTiket.setForeground(new java.awt.Color(51, 153, 255));
        labelTiket.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTiket.setText("9999");

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Ticket.png"))); // NOI18N

        javax.swing.GroupLayout PanelKursiLayout = new javax.swing.GroupLayout(PanelKursi);
        PanelKursi.setLayout(PanelKursiLayout);
        PanelKursiLayout.setHorizontalGroup(
            PanelKursiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelTiket, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(labelTiket, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.add(PanelKursi, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 100, 150, 130));

        tfNamaPenumpang1.setForeground(new java.awt.Color(153, 153, 153));
        tfNamaPenumpang1.setText("Nama Penumpang");
        tfNamaPenumpang1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfNamaPenumpang1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfNamaPenumpang1FocusLost(evt);
            }
        });
        tfNamaPenumpang1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfNamaPenumpang1ActionPerformed(evt);
            }
        });

        iconMember1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconMember1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/People.png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 153, 255));
        jLabel3.setText("Data Penumpang 1");

        javax.swing.GroupLayout panelPenumpangLayout = new javax.swing.GroupLayout(panelPenumpang);
        panelPenumpang.setLayout(panelPenumpangLayout);
        panelPenumpangLayout.setHorizontalGroup(
            panelPenumpangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPenumpangLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(panelPenumpangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelPenumpangLayout.createSequentialGroup()
                        .addComponent(iconMember1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfNamaPenumpang1, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelPenumpangLayout.setVerticalGroup(
            panelPenumpangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPenumpangLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPenumpangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(iconMember1)
                    .addComponent(tfNamaPenumpang1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel1.add(panelPenumpang, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 100, 510, 60));

        tfNamaPenumpang2.setForeground(new java.awt.Color(153, 153, 153));
        tfNamaPenumpang2.setText("Nama Penumpang");
        tfNamaPenumpang2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfNamaPenumpang2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfNamaPenumpang2FocusLost(evt);
            }
        });

        iconMember2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconMember2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/People.png"))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 153, 255));
        jLabel4.setText("Data Penumpang 2");

        javax.swing.GroupLayout panelPenumpang2Layout = new javax.swing.GroupLayout(panelPenumpang2);
        panelPenumpang2.setLayout(panelPenumpang2Layout);
        panelPenumpang2Layout.setHorizontalGroup(
            panelPenumpang2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPenumpang2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(panelPenumpang2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelPenumpang2Layout.createSequentialGroup()
                        .addComponent(iconMember2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfNamaPenumpang2, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelPenumpang2Layout.setVerticalGroup(
            panelPenumpang2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPenumpang2Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPenumpang2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(iconMember2)
                    .addComponent(tfNamaPenumpang2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel1.add(panelPenumpang2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 190, 510, 60));

        tfNamaPenumpang3.setForeground(new java.awt.Color(153, 153, 153));
        tfNamaPenumpang3.setText("Nama Penumpang");
        tfNamaPenumpang3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfNamaPenumpang3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfNamaPenumpang3FocusLost(evt);
            }
        });
        tfNamaPenumpang3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfNamaPenumpang3ActionPerformed(evt);
            }
        });

        iconMember3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconMember3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/People.png"))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 153, 255));
        jLabel5.setText("Data Penumpang 3");

        javax.swing.GroupLayout panelPenumpang3Layout = new javax.swing.GroupLayout(panelPenumpang3);
        panelPenumpang3.setLayout(panelPenumpang3Layout);
        panelPenumpang3Layout.setHorizontalGroup(
            panelPenumpang3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPenumpang3Layout.createSequentialGroup()
                .addGroup(panelPenumpang3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPenumpang3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(iconMember3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfNamaPenumpang3, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE))
                    .addGroup(panelPenumpang3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelPenumpang3Layout.setVerticalGroup(
            panelPenumpang3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPenumpang3Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPenumpang3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(iconMember3)
                    .addComponent(tfNamaPenumpang3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel1.add(panelPenumpang3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 280, 510, 60));

        tfNamaPenumpang4.setForeground(new java.awt.Color(153, 153, 153));
        tfNamaPenumpang4.setText("Nama Penumpang");
        tfNamaPenumpang4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfNamaPenumpang4FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfNamaPenumpang4FocusLost(evt);
            }
        });

        iconMember.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconMember.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/People.png"))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 153, 255));
        jLabel1.setText("Data Penumpang 4");

        javax.swing.GroupLayout panelPenumpang4Layout = new javax.swing.GroupLayout(panelPenumpang4);
        panelPenumpang4.setLayout(panelPenumpang4Layout);
        panelPenumpang4Layout.setHorizontalGroup(
            panelPenumpang4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPenumpang4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(panelPenumpang4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelPenumpang4Layout.createSequentialGroup()
                        .addComponent(iconMember, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfNamaPenumpang4, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelPenumpang4Layout.setVerticalGroup(
            panelPenumpang4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPenumpang4Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPenumpang4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(iconMember)
                    .addComponent(tfNamaPenumpang4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel1.add(panelPenumpang4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 370, 510, 60));

        btnCetakTiket.setText("Cetak TIket");
        btnCetakTiket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakTiketActionPerformed(evt);
            }
        });
        jPanel1.add(btnCetakTiket, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 400, 150, 30));

        buttonBayar.setText("Bayar");
        buttonBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBayarActionPerformed(evt);
            }
        });
        jPanel1.add(buttonBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 360, 150, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 936, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliRiwayatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliRiwayatActionPerformed
        // TODO add your handling code here:
        MenuCustomer customerFrame = new MenuCustomer();
        customerFrame.setVisible(true);
        customerFrame.pack();
        customerFrame.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_btnKembaliRiwayatActionPerformed

    private void minimizeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizeMouseClicked
        // minimize
        this.setExtendedState(MenuPesan.ICONIFIED);
    }//GEN-LAST:event_minimizeMouseClicked

    private void exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseClicked
        // exit
        System.exit(0);
    }//GEN-LAST:event_exitMouseClicked

    private void buttonBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBayarActionPerformed
       prosesBooking();
    }//GEN-LAST:event_buttonBayarActionPerformed

    private void tfNamaPenumpang1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfNamaPenumpang1ActionPerformed
        
    }//GEN-LAST:event_tfNamaPenumpang1ActionPerformed

    private void tfNamaPenumpang1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfNamaPenumpang1FocusGained
        if (tfNamaPenumpang1.getText().equals("Nama Penumpang")) {
            tfNamaPenumpang1.setText("");
            tfNamaPenumpang1.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_tfNamaPenumpang1FocusGained

    private void tfNamaPenumpang1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfNamaPenumpang1FocusLost
        if (tfNamaPenumpang1.getText().equals("")) {
            tfNamaPenumpang1.setText("Nama Penumpang");
            tfNamaPenumpang1.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_tfNamaPenumpang1FocusLost

    private void tfNamaPenumpang2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfNamaPenumpang2FocusGained
        if (tfNamaPenumpang2.getText().equals("Nama Penumpang")) {
            tfNamaPenumpang2.setText("");
            tfNamaPenumpang2.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_tfNamaPenumpang2FocusGained

    private void tfNamaPenumpang2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfNamaPenumpang2FocusLost
        // TODO add your handling code here:
        if (tfNamaPenumpang2.getText().equals("")) {
            tfNamaPenumpang2.setText("Nama Penumpang");
            tfNamaPenumpang2.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_tfNamaPenumpang2FocusLost

    private void tfNamaPenumpang3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfNamaPenumpang3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfNamaPenumpang3ActionPerformed

    private void tfNamaPenumpang3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfNamaPenumpang3FocusGained
        if (tfNamaPenumpang3.getText().equals("Nama Penumpang")) {
            tfNamaPenumpang3.setText("");
            tfNamaPenumpang3.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_tfNamaPenumpang3FocusGained

    private void tfNamaPenumpang3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfNamaPenumpang3FocusLost
       if (tfNamaPenumpang3.getText().equals("")) {
            tfNamaPenumpang3.setText("Nama Penumpang");
            tfNamaPenumpang3.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_tfNamaPenumpang3FocusLost

    private void tfNamaPenumpang4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfNamaPenumpang4FocusGained
        if (tfNamaPenumpang4.getText().equals("Nama Penumpang")) {
            tfNamaPenumpang4.setText("");
            tfNamaPenumpang4.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_tfNamaPenumpang4FocusGained

    private void tfNamaPenumpang4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfNamaPenumpang4FocusLost
        if (tfNamaPenumpang4.getText().equals("")) {
            tfNamaPenumpang4.setText("Nama Penumpang");
            tfNamaPenumpang4.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_tfNamaPenumpang4FocusLost

    private void btnCetakTiketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakTiketActionPerformed
        // TODO add your handling code here:
        cetakTiket();
    }//GEN-LAST:event_btnCetakTiketActionPerformed

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
            java.util.logging.Logger.getLogger(MenuPesan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuPesan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuPesan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuPesan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuPesann().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelKursi;
    private javax.swing.JButton btnCetakTiket;
    private javax.swing.JToggleButton btnKembaliRiwayat;
    private javax.swing.JToggleButton buttonBayar;
    private javax.swing.JLabel exit;
    private javax.swing.JLabel iconMember;
    private javax.swing.JLabel iconMember1;
    private javax.swing.JLabel iconMember2;
    private javax.swing.JLabel iconMember3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel labelTiket;
    private javax.swing.JLabel minimize;
    private javax.swing.JPanel panelPenumpang;
    private javax.swing.JPanel panelPenumpang2;
    private javax.swing.JPanel panelPenumpang3;
    private javax.swing.JPanel panelPenumpang4;
    private javax.swing.JTextField tfNamaPenumpang1;
    private javax.swing.JTextField tfNamaPenumpang2;
    private javax.swing.JTextField tfNamaPenumpang3;
    private javax.swing.JTextField tfNamaPenumpang4;
    // End of variables declaration//GEN-END:variables

    private static class MenuPesann {

        public MenuPesann() {
        }

        private void setVisible(boolean b) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}
