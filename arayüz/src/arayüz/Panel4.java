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
import java.text.SimpleDateFormat;
import java.util.Date;
public class Panel4 extends JPanel {
	  private Connection connection;
	    private DefaultTableModel tableModel;
	    private JButton ekleButton; // JButton'ı tanımla
	    private JButton duzenleButton;
	    private JButton araButton;
	    public Panel4(Connection connection) {
	        this.connection = connection;

	        setLayout(new BorderLayout());
	        add(new JLabel("Reçeteler"), BorderLayout.NORTH);

	        // Tablo modeli oluştur
	        tableModel = new DefaultTableModel();
	        tableModel.addColumn("Reçete ID");
	        tableModel.addColumn("Tarih");
	        tableModel.addColumn("Hasta ID");
	        tableModel.addColumn("Barkod");
	        tableModel.addColumn("ilaç Adı");
	        tableModel.addColumn("miktar");

	        JTable table = new JTable(tableModel);

	        JScrollPane scrollPane = new JScrollPane(table);
	        add(scrollPane, BorderLayout.CENTER);

	        JPanel buttonPanel = new JPanel(new FlowLayout()); // Butonları yatay olarak yerleştirecek layout

	        ekleButton = new JButton("Ekle");
	        buttonPanel.add(ekleButton);

	        duzenleButton = new JButton("Düzenle");
	        buttonPanel.add(duzenleButton);
	        
	        araButton = new JButton("Ara");
	        buttonPanel.add(araButton);
	        
	        add(buttonPanel, BorderLayout.SOUTH);

	        // Ekle butonu için ActionListener
	        ekleButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                ekleButonuActionPerformed();
	            }
	        });
	        duzenleButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                duzenleButonuActionPerformed();
	            }
	        });
	        
	        araButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                araButonuActionPerformed();
	            }
	        });

	        getReceteBilgileri();
	    }
    private void ekleButonuActionPerformed() {
        if (connection != null) {
            try {
                // Tarih bilgisini al
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String tarih = dateFormat.format(new Date());

                // TC numarasını al
                String tc = JOptionPane.showInputDialog("TC Numarasını Giriniz:");
                if (tc == null) {
                    return;  // İptal edildi
                }

                // Stored procedure çağrısı
                String callProcedure = "{call recete_ekle(?, ?, ?, ?, ?)}";
                try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
                    callableStatement.setBigDecimal(1, new java.math.BigDecimal(tc));
                    callableStatement.setString(2, tarih);
                    callableStatement.setString(3, JOptionPane.showInputDialog("Barkod Numarasını Giriniz:"));
                    callableStatement.setString(4, JOptionPane.showInputDialog("ilaç adı giriniz:"));
                    callableStatement.setFloat(5, Float.parseFloat(JOptionPane.showInputDialog("ilaç miktarını giriniz:")));

                    callableStatement.execute();
                }

                // Yeniden hasta bilgilerini getir
                getReceteBilgileri();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("Bağlantı bulunamadı.");
        }
    }
    private void araButonuActionPerformed() {
        // Hasta TC'sini al
        String tc = JOptionPane.showInputDialog("TC Numarasını Giriniz:");
        if (tc != null && !tc.isEmpty()) {
            // getHastaReceteleri metodunu çağır ve paneli güncelle
            getHastaReceteleri(tc);
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen bir TC numarası girin.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void duzenleButonuActionPerformed() {
        if (connection != null) {
            try {
                // TC numarasını al
                String tc = JOptionPane.showInputDialog("TC Numarasını Giriniz:");
                if (tc == null) {
                    return;  // İptal edildi
                }

                // Barkod numarasını al
                String barkod = JOptionPane.showInputDialog("Yeni Barkod Numarasını Giriniz:");
                if (barkod == null) {
                    return;  // İptal edildi
                }

                // İlaç adını al
                String ilacAdi = JOptionPane.showInputDialog("Yeni İlaç Adını Giriniz:");
                if (ilacAdi == null) {
                    return;  // İptal edildi
                }

                // İlaç miktarını al
                float miktar = Float.parseFloat(JOptionPane.showInputDialog("Yeni İlaç Miktarını Giriniz:"));

                // Reçete ID'sini al
                int receteID = Integer.parseInt(JOptionPane.showInputDialog("Reçete ID'sini Giriniz:"));

                // Stored procedure çağrısı
                String callProcedure = "{call recete_duzenle(?, ?, ?, ?, ?)}";
                try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
                    callableStatement.setInt(1, Integer.parseInt(tc));
                    callableStatement.setString(2, barkod);
                    callableStatement.setString(3, ilacAdi);
                    callableStatement.setFloat(4, miktar);
                    callableStatement.setInt(5, receteID);

                    callableStatement.execute();
                }

                // Yeniden reçete bilgilerini getir
                getReceteBilgileri();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("Bağlantı bulunamadı.");
        }
    }
    private void getHastaReceteleri(String tc) {
        if (connection != null) {
            try {
                // Stored procedure çağrısı
                String callFunction = "{? = call getHastaReceteIlaclar(?)}";
                try (CallableStatement callableStatement = connection.prepareCall(callFunction)) {
                    // OUT parametresi için registerOutParameter kullanılır
                    callableStatement.registerOutParameter(1, Types.VARCHAR);
                    callableStatement.setBigDecimal(2, new BigDecimal(tc));
                    
                    // Stored procedure'ü çağır
                    callableStatement.execute();

                    // OUT parametresini al
                    String result = callableStatement.getString(1);

                    // Sonucu güncelleyerek tabloyu yeniden oluşturun
                    updateTable(result);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("Bağlantı bulunamadı.");
        }
    }
    private void updateTable(String result) {
        // Her bir bilgiyi ayrıştırmak için uygun bir lojik oluşturun
        // Örneğin, satırlar arasındaki ayrımı belirlemek için kullanılan bir desen:
        String recordSeparator = "------------------------------";

        // result içeriğini recordSeparator kullanarak ayırın
        String[] records = result.split(recordSeparator);

        // Her kayıt için işlemleri gerçekleştirin
        for (String record : records) {
            // Kayıttaki boşlukları temizleyin
            record = record.trim();

            // Boş bir kayıtı atlayın
            if (record.isEmpty()) {
                continue;
            }

            // Kaydı ayrıştırın
            String[] fields = record.split("\\n");

            // Bilgileri bir StringBuffer'a topla
            StringBuilder message = new StringBuilder();
            for (String field : fields) {
                message.append(field).append("\n");
            }

            // Bilgileri JOptionPane içinde göster
            JOptionPane.showMessageDialog(this, message.toString(), "Hasta Bilgileri", JOptionPane.INFORMATION_MESSAGE);
        }
    }

	    public void getReceteBilgileri() {
	        if (connection != null) {
	            try {
	                // Tabloyu temizle
	                tableModel.setRowCount(0);

	                // Stored procedure çağrısı
	                String callProcedure = "{call get_recete_hepsi()}";
	                try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
	                    ResultSet resultSet = callableStatement.executeQuery();

	                    while (resultSet.next()) {
	                        // Verileri tabloya ekle
	                        Object[] rowData = {
	                                resultSet.getString("receteID"),
	                                resultSet.getString("tarih"),
	                                resultSet.getString("hastaID"),
	                                resultSet.getString("barkod"),
	                                resultSet.getString("ilaçAdı"),
	                                resultSet.getString("miktar")
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