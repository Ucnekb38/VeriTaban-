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

public class Panel5 extends JPanel {
    private Connection connection;
    private DefaultTableModel tableModel;

    public Panel5(Connection connection) {
        this.connection = connection;

        setLayout(new BorderLayout());
        add(new JLabel("personel "), BorderLayout.NORTH);

        // Tablo modeli oluştur
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Ad");
        tableModel.addColumn("Soyad");
        tableModel.addColumn("Pozisyon");

        JTable table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton ekleButton = new JButton("Ekle");
        JButton silButton = new JButton("Sil");
        JButton ara = new JButton("ara");

        ekleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ad = JOptionPane.showInputDialog(null, "Ad:");
                String soyad = JOptionPane.showInputDialog(null, "Soyad:");
                String pozisyon = JOptionPane.showInputDialog(null, "Pozisyon:");

                // Ekleme işlemini gerçekleştir
                try {
                    String callProcedure = "{call personel_ekle(?, ?, ?)}";
                    try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
                        callableStatement.setString(1, ad);
                        callableStatement.setString(2, soyad);
                        callableStatement.setString(3, pozisyon);
                        callableStatement.executeUpdate();
                    }

                    // Ekleme işlemi başarılıysa tabloyu güncelle
                    getPersonelBilgileri();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Ekleme işlemi sırasında bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        silButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String personelIDString = JOptionPane.showInputDialog(null, "Silinecek personel ID'sini girin:");
                
                try {
                    int personelID = Integer.parseInt(personelIDString);
                    
                    // Silme işlemini gerçekleştir
                    String callProcedure = "{call personel_sil(?)}";
                    try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
                        callableStatement.setInt(1, personelID);
                        callableStatement.executeUpdate();
                    }

                    // Silme işlemi başarılıysa tabloyu güncelle
                    getPersonelBilgileri();
                } catch (NumberFormatException | SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Geçersiz personel ID veya silme işlemi sırasında bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel buttonPanel5 = new JPanel();
        buttonPanel5.add(ekleButton);
        buttonPanel5.add(silButton);
        buttonPanel5.add(ara);

        add(buttonPanel5, BorderLayout.SOUTH);
    }

    public void getPersonelBilgileri() {
        if (connection != null) {
            try {
                // Tabloyu temizle
                tableModel.setRowCount(0);

                String callProcedure = "{call get_personel_hepsi()}";
                try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
                    ResultSet resultSet = callableStatement.executeQuery();

                    while (resultSet.next()) {
                        // Verileri tabloya ekle
                        Object[] rowData = {
                                resultSet.getString("personel_ID"),
                                resultSet.getString("ad"),
                                resultSet.getString("soyad"),
                                resultSet.getString("pozisyon")
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
