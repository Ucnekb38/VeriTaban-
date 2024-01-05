package arayüz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class Panel1 extends JPanel {
    private Connection connection;
    private DefaultTableModel tableModel;
    private JButton ekleButton;
    private JButton gunSonuButton; // Yeni eklenen buton
    private JButton silButton;

    public Panel1(Connection connection) {
        this.connection = connection;

        setLayout(new BorderLayout());
        add(new JLabel("Satış"), BorderLayout.NORTH);

        // Tablo modeli oluştur
        tableModel = new DefaultTableModel();
        tableModel.addColumn("satısID ");
        tableModel.addColumn("tarih");
        tableModel.addColumn("receteID");
        tableModel.addColumn("personel_ID");
        tableModel.addColumn("odeme");
        tableModel.addColumn("hastaID");
        tableModel.addColumn("miktar");
        tableModel.addColumn("FİYAT ");

        JTable table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        ekleButton = new JButton("Ekle");
        gunSonuButton = new JButton("Gün Sonu");
        silButton = new JButton("Sil"); 
       
        buttonPanel.add(silButton); 
        buttonPanel.add(ekleButton);
        buttonPanel.add(gunSonuButton);

        ekleButton.addActionListener(e -> {
            ekleButonuActionPerformed();
            getSatısBilgileri();
        });
        
        // Sil butonu için ActionListener
        silButton.addActionListener(e -> {
            Panel1.this.silButonuActionPerformed();
        });

        gunSonuButton.addActionListener(e -> {
            gunSonuButonuActionPerformed();
        });

        add(buttonPanel, BorderLayout.SOUTH);

        getSatısBilgileri();
    }

    private void ekleButonuActionPerformed() {
        if (connection != null) {
            try {
                String musteriTC = JOptionPane.showInputDialog("Müşteri TC:");
                float miktar = Float.parseFloat(JOptionPane.showInputDialog("Miktar:"));
                float toplamFiyat = Float.parseFloat(JOptionPane.showInputDialog("Toplam Fiyat:"));
                String odeme = JOptionPane.showInputDialog("Ödeme Tipi:");
                String personelAd = JOptionPane.showInputDialog("Personel Ad:");
                String barkod = JOptionPane.showInputDialog("Barkod:");

                String callProcedure = "{call satıs_ekle(?, ?, ?, ?, ?, ?)}";
                try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
                    callableStatement.setBigDecimal(1, new BigDecimal(musteriTC));
                    callableStatement.setFloat(2, miktar);
                    callableStatement.setFloat(3, toplamFiyat);
                    callableStatement.setString(4, odeme);
                    callableStatement.setString(5, personelAd);
                    callableStatement.setString(6, barkod);

                    callableStatement.execute();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("Bağlantı bulunamadı.");
        }
    }

    private void gunSonuButonuActionPerformed() {
        if (connection != null) {
            try {
                String tarih = JOptionPane.showInputDialog("Gün Sonu Tarihi (yyyy-MM-dd):");

                String callProcedure = "{call gun_sonu(?, ?)}";
                try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
                    callableStatement.setString(1, tarih);
                    callableStatement.registerOutParameter(2, Types.FLOAT);

                    callableStatement.execute();

                    float toplamFiyat = callableStatement.getFloat(2);
                    JOptionPane.showMessageDialog(this, "Gün Sonu Toplam para: " + toplamFiyat + "₺");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("Bağlantı bulunamadı.");
        }
    }

    public void getSatısBilgileri() {
        if (connection != null) {
            try {
                tableModel.setRowCount(0);

                String callProcedure = "{call get_Satıs_hepsi()}";
                try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
                    ResultSet resultSet = callableStatement.executeQuery();

                    while (resultSet.next()) {
                        Object[] rowData = {
                                resultSet.getString("satısID"),
                                resultSet.getString("tarih"),
                                resultSet.getString("receteID"),
                                resultSet.getString("personel_ID"),
                                resultSet.getString("odeme"),
                                resultSet.getString("hastaID"),
                                resultSet.getString("miktar"),
                                resultSet.getString("toplamFiyat")
                        };
                        tableModel.addRow(rowData);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("Bağlantı bulunamadı.");
        }
    }
    private void silButonuActionPerformed() {
        if (connection != null) {
            try {
                // Kullanıcıdan silinecek satış ID'sini al
                int satısID = Integer.parseInt(JOptionPane.showInputDialog("Silinecek Satış ID:"));

                // Stored procedure çağrısı
                String callProcedure = "{call satıs_sill(?)}";
                try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
                    callableStatement.setInt(1, satısID);

                    callableStatement.execute();

                    JOptionPane.showMessageDialog(this, "Satış başarıyla silindi.");
                    // Satış bilgilerini yeniden getir
                    getSatısBilgileri();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Satış silinirken bir hata oluştu.");
            }
        } else {
            System.err.println("Bağlantı bulunamadı.");
        }
    }
}

