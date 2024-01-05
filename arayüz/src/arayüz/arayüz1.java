package arayüz;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class arayüz1 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame();
            mainFrame.setTitle("Eczane");
            mainFrame.setSize(500, 600);
            mainFrame.setLocation(100, 200);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainPanel = new JPanel();
            mainFrame.add(mainPanel);

            Connection connection = null;
            try {
                // Veritabanına bağlan
                String url = "jdbc:mysql://localhost:3306/CumhuriyetEmre_Eczane";
                String username = "root";
                String password = "";
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, username, password);
            } catch (Exception e) {
                e.printStackTrace();
            }

            CardLayout cardLayout = new CardLayout();
            mainPanel.setLayout(cardLayout);

            JButton button1 = new JButton("Satış");
            JButton button2 = new JButton("ilaçlar");
            JButton button3 = new JButton("hastalar");
            JButton button4 = new JButton("reçeteler");
            JButton button5 = new JButton("personel");



    ;

    		Panel1 panel1=new Panel1(connection);
            Panel2 panel2 = new Panel2(connection);
            Panel3 panel3 = new Panel3(connection);
            Panel4 panel4 = new Panel4(connection);
            Panel5 panel5 = new Panel5(connection);

            mainPanel.add(panel1, "Panel1");
            mainPanel.add(panel2, "Panel2");
            mainPanel.add(panel3, "Panel3");
            mainPanel.add(panel4, "Panel4");
            mainPanel.add(panel5, "Panel5");

            button1.addActionListener(e ->{
            	cardLayout.show(mainPanel, "Panel1");
            	panel1.getSatısBilgileri();
            });
            button2.addActionListener(e -> {
                cardLayout.show(mainPanel, "Panel2");
                panel2.getIlacBilgileri(); // İlaç bilgilerini getir
            });
            button3.addActionListener(e -> {
                cardLayout.show(mainPanel, "Panel3");
                panel3.getHastaBilgileri();
            });
            button4.addActionListener(e -> {
                cardLayout.show(mainPanel, "Panel4");
                panel4.getReceteBilgileri();
            });
            button5.addActionListener(e -> {
                cardLayout.show(mainPanel, "Panel5");
                panel5.getPersonelBilgileri();
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(button1);
            buttonPanel.add(button2);
            buttonPanel.add(button3);
            buttonPanel.add(button4);
            buttonPanel.add(button5);
            mainFrame.add(buttonPanel, BorderLayout.SOUTH);

            mainFrame.setVisible(true);
        });
    }
}