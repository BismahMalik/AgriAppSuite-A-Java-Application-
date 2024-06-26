/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Bismah Malik
 */
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CropSearch extends JFrame {
    private JTextField queryField;
    private JTextArea resultArea;
    

// Inside your CropSearch class
private Connection connection;

    public CropSearch() {
        
        super("Crop Search");
        connectToDatabase();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel queryLabel = new JLabel("Enter Query:");
        queryField = new JTextField();
        JButton searchButton = new JButton("Search");
        resultArea = new JTextArea();

        panel.add(queryLabel);
        panel.add(queryField);
        panel.add(searchButton);
        panel.add(resultArea);

        add(panel);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
    }
    // Method to establish a database connection
private void connectToDatabase() {
    try {
        // Load the JDBC driver and establish a connection
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kashtkari", "root", "admin123");
    } catch (Exception e) {
        e.printStackTrace();
    }
}


// Method to perform the search and retrieve data from the database
private void performSearch() {
    String query = queryField.getText();
    String result = "";

    try {
        // Prepare the SQL statement
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT Information FROM CropInfo WHERE CropName = ?");
        preparedStatement.setString(1, query);

        // Execute the query
        ResultSet resultSet = preparedStatement.executeQuery();

        // Process the result
        if (resultSet.next()) {
            // If a record is found, retrieve the information
            result = "Results for query: " + query + "\nCrop Information: " + resultSet.getString("Information");
        } else {
            result = "No information found for query: " + query;
        }

        // Display the result
        resultArea.setText(result);

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CropSearch().setVisible(true);
            }
        });
    }
}
