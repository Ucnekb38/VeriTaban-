package arayüz;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Panel3 extends JPanel {
	  private Connection connection;
	    private DefaultTableModel tableModel;

	    public Panel3(Connection connection) {
	        this.connection = connection;

	        setLayout(new BorderLayout());
	        add(new JLabel("Hasta Bilgisi"), BorderLayout.NORTH);

	        // Tablo modeli oluştur
	        tableModel = new DefaultTableModel();
	        tableModel.addColumn("Hasta ID");
	        tableModel.addColumn("Ad");
	        tableModel.addColumn("Soyad");
	        tableModel.addColumn("TC");
	        tableModel.addColumn("Adres");
	        tableModel.addColumn("Telefon");

	        JTable table = new JTable(tableModel);

	        JScrollPane scrollPane = new JScrollPane(table);
	        add(scrollPane, BorderLayout.CENTER);

	        // Hasta bilgilerini getirme işlemi...
	        getHastaBilgileri();

	        // Yeni bir alt panel oluştur
	        JPanel bottomPanel = new JPanel(new FlowLayout());

	        JButton ekleButton = new JButton("Ekle");
	        bottomPanel.add(ekleButton);

	        // Ekle butonuna ActionListener ekleyerek işlevsellik kazandır
	        ekleButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                // Ekle butonuna tıklandığında yapılacak işlemler
	                String ad = JOptionPane.showInputDialog(null, "Ad:");
	                String soyad = JOptionPane.showInputDialog(null, "Soyad:");
	                String tc = JOptionPane.showInputDialog(null, "TC:");
	                String adres = JOptionPane.showInputDialog(null, "Adres:");
	                String telefon = JOptionPane.showInputDialog(null, "Telefon:");

	                // Ekleme işlemi
	                try {
	                    String callProcedure = "{call hasta_ekle(?, ?, ?, ?, ?)}";
	                    try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
	                        callableStatement.setString(1, ad);
	                        callableStatement.setString(2, soyad);
	                        callableStatement.setString(3, tc);
	                        callableStatement.setString(4, adres);
	                        callableStatement.setString(5, telefon);
	                        callableStatement.executeUpdate();

	                        // Ekleme işlemi başarılı olursa tabloyu güncelle
	                        getHastaBilgileri();
	                    }
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(null, "Hasta eklenirken bir hata oluştu!");
	                }
	            }
	        });

	        // Sil butonunu alt panele ekle
	        JButton silButton = new JButton("Sil");
	        bottomPanel.add(silButton);

	        // Sil butonuna ActionListener ekleyerek işlevsellik kazandır
	        silButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                // Sil butonuna tıklandığında yapılacak işlemler
	                String tc = JOptionPane.showInputDialog(null, "Silmek istediğiniz hastanın TC'sini girin:");
	                // Silme işlemi
	                try {
	                    String callProcedure = "{call hasta_sil(?)}";
	                    try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
	                        callableStatement.setString(1, tc);
	                        callableStatement.executeUpdate();

	                        // Silme işlemi başarılı olursa tabloyu güncelle
	                        getHastaBilgileri();
	                    }
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(null, "Hasta silinirken bir hata oluştu!");
	                }
	            }
	        });

	        // Güncelle butonunu alt panele ekle
	        JButton guncelleButton = new JButton("Bilgileri Güncelle");
	        bottomPanel.add(guncelleButton);

	        // Güncelle butonuna ActionListener ekleyerek tabloyu güncelle
	        guncelleButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                // Güncelle butonuna tıklandığında yapılacak işlemler
	                String tc = JOptionPane.showInputDialog(null, "Güncellenecek hastanın TC'sini girin:");

	                // Verileri al
	                String ad = JOptionPane.showInputDialog(null, "Ad:");
	                String soyad = JOptionPane.showInputDialog(null, "Soyad:");
	                String adres = JOptionPane.showInputDialog(null, "Adres:");
	                String telefon = JOptionPane.showInputDialog(null, "Telefon:");

	                // Güncelleme işlemi
	                try {
	                    String callProcedure = "{call hasta_guncelle(?, ?, ?, ?, ?)}";
	                    try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
	                        callableStatement.setString(1, tc);
	                        callableStatement.setString(2, ad);
	                        callableStatement.setString(3, soyad);
	                        callableStatement.setString(4, adres);
	                        callableStatement.setString(5, telefon);
	                        callableStatement.executeUpdate();

	                        // Güncelleme işlemi başarılı olursa tabloyu güncelle
	                        getHastaBilgileri();
	                    }
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(null, "Hasta güncellenirken bir hata oluştu!");
	                }
	            }
	        });

	        // Alt paneli ana panele ekle
	        add(bottomPanel, BorderLayout.SOUTH);
	    }

	    public void getHastaBilgileri() {
	        if (connection != null) {
	            try {
	                // Tabloyu temizle
	                tableModel.setRowCount(0);

	                // Stored procedure çağrısı
	                String callProcedure = "{call get_hasta_hepsi()}";
	                try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
	                    ResultSet resultSet = callableStatement.executeQuery();

	                    while (resultSet.next()) {
	                        // Verileri tabloya ekle
	                        Object[] rowData = {
	                                resultSet.getString("hastaID"),
	                                resultSet.getString("ad"),
	                                resultSet.getString("soyad"),
	                                resultSet.getString("tc"),
	                                resultSet.getString("adres"),
	                                resultSet.getString("telefon")
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