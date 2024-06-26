import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TutorialsGUI extends JFrame {
    private JTextField userInputField;

    public TutorialsGUI() {
        super("Tutorials from Experts about Different Fields");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        JLabel instructionLabel = new JLabel("Enter Crop or Field Name:");
        userInputField = new JTextField(20);
        JButton tutorialsButton = new JButton("Show Tutorials");

        panel.add(instructionLabel);
        panel.add(userInputField);
        panel.add(tutorialsButton);

        add(panel);

        tutorialsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = userInputField.getText();
                String tutorials = retrieveTutorialsFromDatabase(userInput);
                JOptionPane.showMessageDialog(TutorialsGUI.this, tutorials);
            }
        });
    }

    private String retrieveTutorialsFromDatabase(String userInput) {
        StringBuilder tutorials = new StringBuilder();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kashtkari", "root", "admin123");
            
            // Query the Crops table for video links based on user input
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT VideoLink FROM cropstutorial WHERE CropName = ?");
            preparedStatement.setString(1, userInput);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
    tutorials.append("Crop Tutorials for ").append(userInput).append(": ").append(resultSet.getString("VideoLink")).append("\n");
}


            

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (tutorials.length() == 0) {
            return "No tutorials found for " + userInput;
        }

        return tutorials.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TutorialsGUI().setVisible(true);
            }
        });
    }
}
