import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SuggestFieldsGUI extends JFrame {
    private JTextField areaInput;
    private JLabel weatherDetails;

    public SuggestFieldsGUI() {
        super("Suggest Fields");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        areaInput = new JTextField();
        areaInput.setToolTipText("Enter area name");

        weatherDetails = new JLabel("Waiting for area input...");
        JButton getWeatherButton = new JButton("Get Weather");
        getWeatherButton.setMargin(new Insets(10, 10, 10, 10));
//         getWeatherButton.setPreferredSize(new Dimension(0, 0));
//        getWeatherButton.setMaximumSize(new Dimension(100, 40));
//        getWeatherButton.setMinimumSize(new Dimension(100, 40));
        

        panel.add(areaInput, BorderLayout.NORTH);
        panel.add(getWeatherButton, BorderLayout.CENTER);
        panel.add(weatherDetails, BorderLayout.SOUTH);

        add(panel);

        getWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String area = areaInput.getText();
                    if (!area.isEmpty()) {
                        double temperature = getTemperatureForArea(area);

                        String crops = getCropsByTemperature(temperature);

                        weatherDetails.setText("\nTemperature: " + temperature + "\n\nCrops in similar temperature range:\n\n" + crops);
                    } else {
                        weatherDetails.setText("Please enter an area name.");
                    }
                } catch (Exception ex) {
                    weatherDetails.setText("Error fetching weather.");
                    ex.printStackTrace();
                }
            }
        });
    }

    private double getTemperatureForArea(String area) {
        double temperature = 0.0;
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://weather-by-api-ninjas.p.rapidapi.com/v1/weather?city=" + area)
                    .header("X-RapidAPI-Key", "2119b2f0bcmsh9266539e1df3257p19382bjsn175ad887fe14")
                    .header("X-RapidAPI-Host", "weather-by-api-ninjas.p.rapidapi.com")
                    .asJson();

            temperature = response.getBody().getObject().getDouble("temp");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return temperature;
    }

    private String getCropsByTemperature(double temperature) {
        StringBuilder crops = new StringBuilder();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kashtkari", "root", "admin123");

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT FieldName FROM fieldsweather WHERE temp BETWEEN ? AND ?");
            preparedStatement.setDouble(1, temperature - 10); // Adjust range as needed
            preparedStatement.setDouble(2, temperature + 20);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                crops.append(resultSet.getString("FieldName")).append("\n");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return crops.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SuggestFieldsGUI().setVisible(true);
        });
    }
}

