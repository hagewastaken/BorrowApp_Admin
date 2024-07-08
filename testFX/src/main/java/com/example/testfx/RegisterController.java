package com.example.testfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterController {

    @FXML
    private TextField userEmail;

    @FXML
    private PasswordField userPassword;

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private void handleRegisterButtonAction() {
        String email = userEmail.getText();
        String password = userPassword.getText();
        String confirmPasswordText = confirmPassword.getText();

        if (email.isEmpty() || password.isEmpty() || confirmPasswordText.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
        } else if (!password.equals(confirmPasswordText)) {
            showAlert("Error", "Passwords do not match.");
        } else {
            boolean registrationSuccessful = registerUser(email, password);
            if (registrationSuccessful) {
                showAlert("Registration Successful", "Welcome " + email + "!");
                // Navigate to the login page or next page
            } else {
                showAlert("Registration Failed", "An error occurred. Please try again.");
            }
        }
    }

    private boolean registerUser(String email, String password) {
        try {
            String endpoint = "/users/register";
            String urlString = ApiUtil.getEndpoint(endpoint);

            URL url = new URL(urlString);
            //URL url = new URL("http://localhost:3000/users/register"); // Updated URL to match the server route
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Chrome/51.0.2704.103");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonUserRegister = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
            System.out.println("Sending JSON: " + jsonUserRegister);  // Debug log

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonUserRegister.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Response: " + response.toString());
                    return true;
                }
            } else {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Error Response: " + response.toString());
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred: " + e.getMessage());
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLoginButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
            // Close the register stage
            Stage registerStage = (Stage) userEmail.getScene().getWindow();
            registerStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the login page.");
        }
    }
}