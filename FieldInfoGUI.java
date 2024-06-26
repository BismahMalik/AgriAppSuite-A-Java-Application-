import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class FieldInfoGUI extends JFrame {

    private Connection connection;
    private JTextArea infoTextArea;
    private JTextField cropTypeField;
    private JButton getInfoButton;

    public FieldInfoGUI() {
        super("Field Information");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cropTypeField = new JTextField(15);
        getInfoButton = new JButton("Get Information");

        topPanel.add(new JLabel("Enter Crop Type:"));
        topPanel.add(cropTypeField);
        topPanel.add(getInfoButton);

        panel.add(topPanel, BorderLayout.NORTH);

        infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(infoTextArea);

        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);

        getInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cropType = cropTypeField.getText();
                displayFieldInfo(cropType);
            }
        });

        // Establish database connection upon object creation
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            // Load the JDBC driver and establish a connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kashtkari", "root", "admin123");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayFieldInfo(String cropType) {
        StringBuilder fieldInfo = new StringBuilder();

        try {
            // Create a PreparedStatement for querying the database with the specified crop type
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM FieldInformation WHERE CropType = ?");
            preparedStatement.setString(1, cropType);

            // Execute the query to select fields based on the crop type
            ResultSet resultSet = preparedStatement.executeQuery();

            // Iterate through the result set and append information to the StringBuilder
            while (resultSet.next()) {
                int fieldID = resultSet.getInt("FieldID");
                String idealConditions = resultSet.getString("IdealConditions");
                String harvestingPeriod = resultSet.getString("HarvestingPeriod");

                fieldInfo.append("\nCrop Type: ").append(cropType)
                        .append("\nIdeal Conditions: ").append(idealConditions)
                        .append("\nHarvesting Period: ").append(harvestingPeriod)
                        .append("\n");
            }

            // Close the resources
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set retrieved field information to the text area
        infoTextArea.setText(fieldInfo.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FieldInfoGUI().setVisible(true);
        });
    }
}
