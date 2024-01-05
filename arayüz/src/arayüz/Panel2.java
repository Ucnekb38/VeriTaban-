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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Panel2 extends JPanel {
    private Connection connection;
    private DefaultTableModel tableModel;
    private JButton ekleButton; // JButton'ı tanımla
    private JButton sil;
    private JButton ara;

    public Panel2(Connection connection) {
        this.connection = connection;

        setLayout(new BorderLayout());
        add(new JLabel("İlaçlar"), BorderLayout.NORTH);

        // Tablo modeli oluştur
        tableModel = new DefaultTableModel();
        tableModel.addColumn("İlaç ID");
        tableModel.addColumn("İlaç Adı");
        tableModel.addColumn("Fiyat");
        tableModel.addColumn("Miktar");
        tableModel.addColumn("Tarih");
        tableModel.addColumn("Sürüm");
        tableModel.addColumn("Reçete Durumu");
        tableModel.addColumn("Etnik ID");

        JTable table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout()); // Butonları yatay olarak yerleştirecek layout

        sil = new JButton("sil");
        buttonPanel.add(sil);
        ara = new JButton("ara");
        buttonPanel.add(ara);
        
        ekleButton = new JButton("Ekle");
        buttonPanel.add(ekleButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Ekle butonu için ActionListener
        ekleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
					ekleButonuActionPerformed();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        ara.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
					ekleButonuActionPerformed();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        sil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
					silButonuActionPerformed();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });

        getIlacBilgileri();
    }

    private void ekleButonuActionPerformed() throws ParseException {
        if (connection != null) {
            try {
                // Kullanıcıdan ilaç bilgilerini al
                String ilacAdi = JOptionPane.showInputDialog("İlaç Adı:");
                float fiyat = Float.parseFloat(JOptionPane.showInputDialog("Fiyat:"));
                float miktar = Float.parseFloat(JOptionPane.showInputDialog("Miktar:"));
                String surum = JOptionPane.showInputDialog("Sürüm:");
                String receteDurumu = JOptionPane.showInputDialog("Reçete Durumu:");
                int etnikID = Integer.parseInt(JOptionPane.showInputDialog("Etnik ID:"));
                String tarihStr = JOptionPane.showInputDialog("Tarih (yyyy-MM-dd):");

             // String tarih bilgisini Date objesine çevir
             Date tarih = java.sql.Date.valueOf(tarihStr);
                // Stored procedure çağrısı
                String callProcedure = "{call ilac_ekle(?, ?, ?, ?, ?, ?, ?)}";
                try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
                    callableStatement.setString(1, ilacAdi);
                    callableStatement.setFloat(2, fiyat);
                    callableStatement.setFloat(3, miktar);
                    callableStatement.setTimestamp(4, new java.sql.Timestamp(tarih.getTime()));
                    callableStatement.setString(5, surum);
                    callableStatement.setString(6, receteDurumu);
                    callableStatement.setInt(7, etnikID);

                    callableStatement.execute();
                }

                // Yeniden ilaç bilgilerini getir
                getIlacBilgileri();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("Bağlantı bulunamadı.");
        }
    }
    private void silButonuActionPerformed() throws ParseException {
        // Silinecek ilaç ID'sini kullanıcıdan alabilirsiniz.
        String ilacIDStr = JOptionPane.showInputDialog("Silinecek ilaç ID'sini girin:");
        
        // Girilen değeri integer'a çevirme işlemi
        try {
            int ilacID = Integer.parseInt(ilacIDStr);

            // Veritabanında ilac_sil stored procedure'ünü çağırma
            try (CallableStatement callableStatement = connection.prepareCall("{call ilac_sil(?)}")) {
                callableStatement.setInt(1, ilacID);
                callableStatement.executeUpdate();
                
                // Başarı mesajını kullanıcıya göster
                JOptionPane.showMessageDialog(this, "İlaç başarıyla silindi.");
            } catch (SQLException ex) {
                // Hata durumunda hatayı kullanıcıya bildir
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "İlaç silinirken bir hata oluştu.");
            }
        } catch (NumberFormatException e) {
            // Sayıya çevirme hatası durumunda kullanıcıya hata bildir
            JOptionPane.showMessageDialog(this, "Geçersiz ilaç ID'si. Lütfen sayısal bir değer girin.");
        }
    }

    public void getIlacBilgileri() {
        if (connection != null) {
            try {
                // Tabloyu temizle
                tableModel.setRowCount(0);

                // Stored procedure çağrısı
                String callProcedure = "{call get_ilaç_hepsi()}";
                try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
                    ResultSet resultSet = callableStatement.executeQuery();

                    while (resultSet.next()) {
                        // Verileri tabloya ekle
                        Object[] rowData = {
                                resultSet.getString("ilaçID"),
                                resultSet.getString("ilaçAdı"),
                                resultSet.getString("fiyat"),
                                resultSet.getString("miktar"),
                                resultSet.getString("tarih"),
                                resultSet.getString("surum"),
                                resultSet.getString("receteD"),
                                resultSet.getString("etnikID")
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
}
