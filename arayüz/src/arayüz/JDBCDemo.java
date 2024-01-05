package arayüz;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCDemo {

    public static void main(String[] args) {
    	String url = "jdbc:mysql://localhost:3306/CumhuriyetEmre_Eczane";
        String username = "root";
        String password = "";

        try {
            // JDBC sürücüsünü yükle
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Veritabanına bağlan
            Connection connection = DriverManager.getConnection(url, username, password);

            // 1. Saklı işlevi çağırmak için CallableStatement kullan
            String callProcedure1 = "{call get_personel_hepsi()}";
            try (CallableStatement callableStatement1 = connection.prepareCall(callProcedure1)) {
                ResultSet resultSet1 = callableStatement1.executeQuery();
                while (resultSet1.next()) {
                    System.out.println(resultSet1.getString("personel_ID") + " " + resultSet1.getString("ad") + " " + resultSet1.getString("soyad") + " " + resultSet1.getString("pozisyon"));
                }
            }

            // 2. Saklı işlevi çağırmak için CallableStatement kullan
            String callProcedure2 = "{call get_personel(?)}";
            try (CallableStatement callableStatement2 = connection.prepareCall(callProcedure2)) {
                // Kullanıcıdan alınan değeri temsil eder
                String param = "1"; // İstediğiniz parametreyi belirleyin

                // Parametreyi kontrol et ve uygun türde set et
                if (isNumeric(param)) {
                    callableStatement2.setInt(1, Integer.parseInt(param));
                } else {
                    callableStatement2.setString(1, param);
                }

                // Saklı işleve sorguyu gönder
                ResultSet resultSet2 = callableStatement2.executeQuery();
                while (resultSet2.next()) {
                    System.out.println(resultSet2.getString("personel_ID") + " " + resultSet2.getString("ad") + " " + resultSet2.getString("soyad") + " " + resultSet2.getString("pozisyon"));
                }
            }

            // Bağlantıyı kapat
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Verinin sayısal olup olmadığını kontrol etmek için yardımcı metod
    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}