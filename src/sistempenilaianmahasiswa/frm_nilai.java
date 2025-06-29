/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistempenilaianmahasiswa;

import java.awt.BorderLayout;
import javax.swing.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import sun.net.www.MimeTable;

/**
 *
 * @author Malik
 */
public class frm_nilai extends javax.swing.JFrame {

    koneksi dbsetting;
    String driver, database, user, pass;
    Object tabel;

    /**
     * Creates new form frm_mhs
     */
    public frm_nilai() {
        initComponents();

        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");
        tabel_mata_kuliah.setModel(tableModel);

        txt_preview_indeks.setEditable(false);
        txt_preview_ket.setEditable(false);

        this.setDataToCombobox();
        setTableLoad();
    }

    private javax.swing.table.DefaultTableModel tableModel = getDefaultTabel();

    private javax.swing.table.DefaultTableModel getDefaultTabel() {
        return new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"KODE NILAI","NAMA", "NIM", "NAMA MATKUL", "KODE MATKUL", "NILAI", "INDEX", "KETERANGAN"}
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int ColumnIndex) {
                return canEdit[ColumnIndex];
            }
        };

    }
    ;
    
    String data[] = new String[10];

    private void setTableLoad() {
        String stat = "";
        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String sql = "select * from t_nilai INNER JOIN t_mata_kuliah ON t_nilai.kd_mk = t_mata_kuliah.kd_mk inner join t_mahasiswa on t_nilai.nim = t_mahasiswa.nim";
            ResultSet res = stt.executeQuery(sql);

            while (res.next()) {
                data[0] = res.getString(1);
                data[1] = res.getString("nama");
                data[2] = res.getString(2);
                data[3] = res.getString("nama_mk");
                data[4] = res.getString(3);
                data[5] = res.getString(4);
                data[6] = res.getString(5);
                data[7] = res.getString(6);
                tableModel.addRow(data);
            }

            res.close();
            stt.close();
            kon.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    public void membersihkan_teks() {
        txt_nim.setText("");
        txt_nilai.setText("");

    }

    public void nonaktif_teks() {
        txt_nim.setEnabled(false);
        txt_nilai.setEnabled(false);

    }

    public void aktif_teks() {
        txt_nim.setEnabled(true);
        txt_nilai.setEnabled(true);

    }

    int row = 0;

    public void tampil_field() {
        row = tabel_mata_kuliah.getSelectedRow();
        txt_nim.setText(tableModel.getValueAt(row, 2).toString());
        txt_nilai.setText(tableModel.getValueAt(row, 5).toString());
        String nama_matkul = tableModel.getValueAt(row, 3).toString();
        String kd_matkul = tableModel.getValueAt(row, 4).toString();

        txt_preview_indeks.setText(tableModel.getValueAt(row, 6).toString());
        txt_preview_ket.setText(tableModel.getValueAt(row, 7).toString());

        String matkulSelect = kd_matkul + " - " + nama_matkul;
        combo_box_matkul.setSelectedItem(matkulSelect);

        btn_simpan.setEnabled(false);
        btn_ubah.setEnabled(true);
        btn_hapus.setEnabled(true);
        btn_batal.setEnabled(false);
        aktif_teks();

    }

    private String[] hitungIndeksNilai(Double nilai) {
        String indeksNilai;

        if (nilai < 101 && nilai > -1) {
            if (nilai >= 90) {
                indeksNilai = "A";
            } else if (nilai >= 80) {
                indeksNilai = "B";
            } else if (nilai >= 70) {
                indeksNilai = "C";
            } else if (nilai >= 60) {
                indeksNilai = "D";
            } else {
                indeksNilai = "E";
            }
        } else {
            return null;
        }

        String keteranganLulus;
        if ("E".equals(indeksNilai)) {
            keteranganLulus = "Tidak Lulus";

        } else {
            keteranganLulus = "Lulus";
        }

        return new String[]{indeksNilai, keteranganLulus};
    }

    private void checkNilai() {

        try {
            Double nilai = Double.parseDouble(txt_nilai.getText());
            String[] hasilNilai = this.hitungIndeksNilai(nilai);

            if (hasilNilai == null) {
                txt_preview_ket.setText("");
                txt_preview_indeks.setText("");
                JOptionPane.showMessageDialog(null, "Nilai Harus dalam range 0 - 100");
                return;
            }

            txt_preview_indeks.setText(hasilNilai[0]);
            txt_preview_ket.setText(hasilNilai[1]);

        } catch (Exception e) {
            txt_nilai.setText("");
            txt_preview_indeks.setText("");
            txt_preview_ket.setText("");

            if (txt_nilai.getText().isEmpty()) {
                return;
            } else {
                JOptionPane.showMessageDialog(null, "Nilai harus berupa angka");
            }
        }

    }

    private void setDataToCombobox() {
        DefaultComboBoxModel<String> model_matkul = new DefaultComboBoxModel<>();

        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String nim = txt_nim.getText();
            String sql = "SELECT nama_mk, kd_mk FROM t_mata_kuliah";
            ResultSet res = stt.executeQuery(sql);
            boolean data_ada = false;

            while (res.next()) {
                String nama_matkul = res.getString("kd_mk") + " - " + res.getString("nama_mk");
                model_matkul.addElement(nama_matkul);
                data_ada = true;
            }

            combo_box_matkul.setModel(model_matkul);
            res.close();
            stt.close();
            kon.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
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

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_mata_kuliah = new javax.swing.JTable();
        btn_tambah = new javax.swing.JButton();
        btn_ubah = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_simpan = new javax.swing.JButton();
        btn_batal = new javax.swing.JButton();
        btn_keluar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txt_cari_nim = new javax.swing.JTextField();
        btn_cari = new javax.swing.JButton();
        btn_tampil = new javax.swing.JButton();
        combo_box_matkul = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        txt_nilai = new javax.swing.JTextField();
        txt_nim = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txt_preview_indeks = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txt_preview_ket = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("NIM Mahasiswa");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Mata Kuliah");

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Form Nilai");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(515, 515, 515)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(27, 27, 27))
        );

        tabel_mata_kuliah.setModel(new javax.swing.table.DefaultTableModel(
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
        tabel_mata_kuliah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_mata_kuliahMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabel_mata_kuliah);

        btn_tambah.setText("Tambah");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        btn_ubah.setText("Ubah");
        btn_ubah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ubahActionPerformed(evt);
            }
        });

        btn_hapus.setText("Hapus");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        btn_simpan.setText("Simpan");
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
            }
        });

        btn_batal.setText("Batal");
        btn_batal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batalActionPerformed(evt);
            }
        });

        btn_keluar.setText("Keluar");
        btn_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_keluarActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Pencarian Data Mahasiswa", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION), "Pencarian Data Mahasiswa", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setText("Masukan NIM");

        btn_cari.setText("Cari");
        btn_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cariActionPerformed(evt);
            }
        });

        btn_tampil.setText("Tampilkan Seluruh Data");
        btn_tampil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tampilActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(txt_cari_nim, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_tampil, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tampil)
                    .addComponent(btn_cari)
                    .addComponent(jLabel8)
                    .addComponent(txt_cari_nim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        combo_box_matkul.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                combo_box_matkulFocusGained(evt);
            }
        });
        combo_box_matkul.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                combo_box_matkulPopupMenuWillBecomeVisible(evt);
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        combo_box_matkul.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                combo_box_matkulMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                combo_box_matkulMouseEntered(evt);
            }
        });
        combo_box_matkul.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_box_matkulActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setText("Nilai");

        txt_nilai.setToolTipText("");
        txt_nilai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_nilaiKeyReleased(evt);
            }
        });

        txt_nim.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_nimKeyReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("Preview Indeks");

        txt_preview_indeks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_preview_indeksActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("Preview Keterangan Lulus");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_tambah)
                        .addGap(18, 18, 18)
                        .addComponent(btn_ubah, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_nim, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combo_box_matkul, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(46, 46, 46)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txt_nilai, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jLabel5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txt_preview_indeks, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(txt_preview_ket, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(501, 501, 501)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txt_nilai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txt_nim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(combo_box_matkul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txt_preview_indeks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txt_preview_ket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(71, 71, 71)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah)
                    .addComponent(btn_ubah)
                    .addComponent(btn_hapus)
                    .addComponent(btn_simpan)
                    .addComponent(btn_batal)
                    .addComponent(btn_keluar))
                .addGap(65, 65, 65))
        );

        jPanel2.getAccessibleContext().setAccessibleName("Pencarian Data Nilai Mahasiswa");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        // TODO add your handling code here:
        membersihkan_teks();
        txt_nim.requestFocus();
        btn_simpan.setEnabled(true);
        btn_ubah.setEnabled(true);
        btn_hapus.setEnabled(true);
        btn_keluar.setEnabled(true);
        aktif_teks();
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        // TODO add your handling code here:
        String data[] = new String[5];

        String kd_mk = combo_box_matkul.getSelectedItem().toString();
        String[] kd_mkString = kd_mk.split(" - ");

        if ((txt_nim.getText().isEmpty()) || (txt_nilai.getText().isEmpty())) {
            JOptionPane.showMessageDialog(null, "Data tidak boleh kosong, silahkan dilengkapi");
            txt_nim.requestFocus();
        } else {
            try {
                Class.forName(driver);
                Connection kon = DriverManager.getConnection(database, user, pass);
                Statement stt = kon.createStatement();
                Double parsedIntNilai = Double.parseDouble(txt_nilai.getText());
                String sql = "INSERT INTO t_nilai ("
                        + "nim,"
                        + "kd_mk,"
                        + "nilai,"
                        + "`index`,"
                        + "ket"
                        + ")"
                        + " VALUES"
                        + " ( '" + txt_nim.getText() + "',"
                        + " '" + kd_mkString[0] + "' ,"
                        + " " + parsedIntNilai + ","
                        + " '" + txt_preview_indeks.getText() + "' ,"
                        + " '" + txt_preview_ket.getText() + "')";
                stt.executeUpdate(sql);
                tableModel.setRowCount(0);
                setTableLoad();
                stt.close();
                kon.close();
                membersihkan_teks();
                btn_simpan.setEnabled(false);
                nonaktif_teks();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void tabel_mata_kuliahMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_mata_kuliahMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 1) {
            tampil_field();
        }
    }//GEN-LAST:event_tabel_mata_kuliahMouseClicked

    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        // TODO add your handling code here:
        String nim = txt_nim.getText();
        String nilai = txt_nilai.getText();
        Object comboBoxValue = combo_box_matkul.getSelectedItem();

        String kd_mk = combo_box_matkul.getSelectedItem().toString();
        String[] kd_mkString = kd_mk.split(" - ");
        

        if ((nim.isEmpty()) | (nilai.isEmpty())) {
            JOptionPane.showMessageDialog(null, "Data tidak boleh kosong, silahkan dilengkapi");
            txt_nim.requestFocus();
        } else {
            try {
                Class.forName(driver);
                Connection kon = DriverManager.getConnection(database, user, pass);
                Statement stt = kon.createStatement();
                String sql = "UPDATE t_nilai "
                        + "SET nim='" + nim + "',"
                        + "kd_mk='" + kd_mkString[0] + "' ,"
                        + "nilai=" + Double.parseDouble(nilai) + ", "
                        + "`index`='" + txt_preview_indeks.getText() + "', "
                        + "ket='" + txt_preview_ket.getText() + "' "
                        + "WHERE "
                        + "nim='" + tableModel.getValueAt(row, 2).toString() + "'";
                stt.executeUpdate(sql);
                tableModel.removeRow(row);
                tableModel.setRowCount(0);
                setTableLoad();
                stt.close();
                kon.close();
                membersihkan_teks();
                btn_simpan.setEnabled(false);
                nonaktif_teks();

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }


    }//GEN-LAST:event_btn_ubahActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String sql = "DELETE FROM t_nilai "
                    + "WHERE "
                    + "nim='" + tableModel.getValueAt(row, 2).toString() + "'";
            stt.executeUpdate(sql);
            tableModel.removeRow(row);
            stt.close();
            kon.close();
            membersihkan_teks();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cariActionPerformed
        // TODO add your handling code here:
        tableModel.setRowCount(0);

        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String sql = "SELECT * FROM t_nilai INNER JOIN t_mata_kuliah ON t_nilai.kd_mk = t_mata_kuliah.kd_mk INNER JOIN t_mahasiswa ON t_nilai.nim = t_mahasiswa.nim WHERE t_nilai.nim='" + txt_cari_nim.getText() + "'";
            ResultSet res = stt.executeQuery(sql);
            while (res.next()) {
                data[0] = res.getString(1);
                data[1] = res.getString("nama");
                data[2] = res.getString(2);
                data[3] = res.getString("nama_mk");
                data[4] = res.getString(3);
                data[5] = res.getString(4);
                data[6] = res.getString(5);
                data[7] = res.getString(6);
                tableModel.addRow(data);
            }

            res.close();
            stt.close();
            kon.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }//GEN-LAST:event_btn_cariActionPerformed

    private void btn_tampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampilActionPerformed
        // TODO add your handling code here:
        tableModel.setRowCount(0);
        setTableLoad();
    }//GEN-LAST:event_btn_tampilActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        frm_utama utama = new frm_utama();
        utama.setVisible(true);
    }//GEN-LAST:event_formWindowClosed

    private void btn_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_batalActionPerformed

    private void btn_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_keluarActionPerformed
        // TODO add your handling code here:
        frm_utama utama = new frm_utama();
        utama.setVisible(true);

        this.setVisible(false);

    }//GEN-LAST:event_btn_keluarActionPerformed

    private void combo_box_matkulMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_combo_box_matkulMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_box_matkulMouseClicked

    private void combo_box_matkulActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_box_matkulActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_combo_box_matkulActionPerformed

    private void combo_box_matkulFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_combo_box_matkulFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_combo_box_matkulFocusGained

    private void txt_nimKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nimKeyReleased
        // TODO add your handling code here:  

    }//GEN-LAST:event_txt_nimKeyReleased

    private void combo_box_matkulMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_combo_box_matkulMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_box_matkulMouseEntered

    private void combo_box_matkulPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_box_matkulPopupMenuWillBecomeVisible
        // TODO add your handling code here:

    }//GEN-LAST:event_combo_box_matkulPopupMenuWillBecomeVisible

    private void txt_nilaiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nilaiKeyReleased
        // TODO add your handling code here:
        this.checkNilai();

    }//GEN-LAST:event_txt_nilaiKeyReleased

    private void txt_preview_indeksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_preview_indeksActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_preview_indeksActionPerformed

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
            java.util.logging.Logger.getLogger(frm_nilai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frm_nilai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frm_nilai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frm_nilai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frm_nilai().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal;
    private javax.swing.JButton btn_cari;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_keluar;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_tampil;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JComboBox combo_box_matkul;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tabel_mata_kuliah;
    private javax.swing.JTextField txt_cari_nim;
    private javax.swing.JTextField txt_nilai;
    private javax.swing.JTextField txt_nim;
    private javax.swing.JTextField txt_preview_indeks;
    private javax.swing.JTextField txt_preview_ket;
    // End of variables declaration//GEN-END:variables
}
