/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Bismah Malik
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login() {
        super("Login Application");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Signup");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(signupButton);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signup();
            }
        });
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Validate login credentials in the database
        if (validateLogin(username, password)) {
            showOptions(); // Open the new frame after successful login
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password");
        }
    }

   private void showOptions() {
        // Update UI components to show options after successful login
        JPanel panel = (JPanel) getContentPane();
        panel.removeAll(); // Remove existing components

        JButton searchFieldButton = new JButton("Search about the Field");
        JButton fieldInfoButton = new JButton("Give Information about the Fields");
        JButton suggestFieldsButton = new JButton("Suggest Suitable Fields Using Weather and Area");
        JButton tutorialsButton = new JButton("Tutorials from Experts about Different Fields");

        // Add action listeners for the new buttons

        panel.setLayout(new GridLayout(4, 1));
        panel.add(searchFieldButton);
        panel.add(fieldInfoButton);
        panel.add(suggestFieldsButton);
        panel.add(tutorialsButton);

        // Repaint and validate to reflect the changes
        panel.revalidate();
        panel.repaint();

         searchFieldButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new CropSearch().setVisible(true);
        }
    });

    fieldInfoButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new FieldInfoGUI().setVisible(true);
        }
    });

    suggestFieldsButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new SuggestFieldsGUI().setVisible(true);
        }
    });

    tutorialsButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new TutorialsGUI().setVisible(true);
        }
    });
    }

    private boolean validateLogin(String username, String password) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kashtkari", "root", "admin123");

            String query = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void signup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Save user data in the database
        if (saveUserData(username, password)) {
            JOptionPane.showMessageDialog(this, "Signup successful!");
        } else {
            JOptionPane.showMessageDialog(this, "Error signing up");
        }
    }

    private boolean saveUserData(String username, String password) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kashtkari", "root", "admin123");

            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Login().setVisible(true);
            }
        });
    }
}


