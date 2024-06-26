//import javafx.application.Application;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//import com.mashape.unirest.http.HttpResponse;
//import com.mashape.unirest.http.Unirest;
//
//public class WeatherApp extends Application {
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("Weather App");
//
//        TextField areaInput = new TextField();
//        areaInput.setPromptText("Enter area name");
//        Label weatherDetails = new Label();
//
//        Button getWeatherButton = new Button("Get Weather");
//        getWeatherButton.setOnAction(e -> {
//            try {
//                String area = areaInput.getText();
//                if (!area.isEmpty()) {
//                    HttpResponse<String> response = Unirest.get("https://weather-by-api-ninjas.p.rapidapi.com/v1/weather?city=" + area)
//                            .header("X-RapidAPI-Key", "2119b2f0bcmsh9266539e1df3257p19382bjsn175ad887fe14")
//                            .header("X-RapidAPI-Host", "weather-by-api-ninjas.p.rapidapi.com")
//                            .asString();
//
//                    weatherDetails.setText(response.getBody());
//                } else {
//                    weatherDetails.setText("Please enter an area name.");
//                }
//            } catch (Exception ex) {
//                weatherDetails.setText("Error fetching weather.");
//                ex.printStackTrace();
//            }
//        });
//
//        VBox layout = new VBox(10);
//        layout.setPadding(new Insets(20));
//        layout.setAlignment(Pos.CENTER);
//        layout.getChildren().addAll(areaInput, getWeatherButton, weatherDetails);
//
//        Scene scene = new Scene(layout, 300, 200);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//}

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WeatherApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather App");

        TextField areaInput = new TextField();
        areaInput.setPromptText("Enter area name");
        Label weatherDetails = new Label();

        Button getWeatherButton = new Button("Get Weather");
        getWeatherButton.setOnAction(e -> {
            try {
                String area = areaInput.getText();
                if (!area.isEmpty()) {
                    HttpResponse<JsonNode> response = Unirest.get("https://weather-by-api-ninjas.p.rapidapi.com/v1/weather?city=" + area)
                            .header("X-RapidAPI-Key", "2119b2f0bcmsh9266539e1df3257p19382bjsn175ad887fe14")
                            .header("X-RapidAPI-Host", "weather-by-api-ninjas.p.rapidapi.com")
                            .asJson();

                    double temperature = response.getBody().getObject().getDouble("temp");

                    String crops = getCropsByTemperature(temperature);

                    weatherDetails.setText("Temperature: " + temperature + "\nCrops in similar temperature range:\n" + crops);
                } else {
                    weatherDetails.setText("Please enter an area name.");
                }
            } catch (Exception ex) {
                weatherDetails.setText("Error fetching weather.");
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(areaInput, getWeatherButton, weatherDetails);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String getCropsByTemperature(double temperature) {
        StringBuilder crops = new StringBuilder();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kashtkari", "root", "admin123");

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT FieldName FROM Fields WHERE temp BETWEEN ? AND ?");
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
}
