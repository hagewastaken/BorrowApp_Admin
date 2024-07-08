package com.example.testfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReturnItemController {

    @FXML
    private TextField nomorInventarisField;

    private Borrowing borrowing;
    private ManageBorrowingController manageBorrowingController;
    public void initData(Borrowing borrowing, ManageBorrowingController manageBorrowingController) {
        this.borrowing = borrowing;
        this.manageBorrowingController = manageBorrowingController;
    }

    @FXML
    private void handleReturnAction(ActionEvent event) {
        String nomorInventaris = nomorInventarisField.getText().trim();

        try {
            String endpoint = "/borrow/verify/";
            String urlString = ApiUtil.getEndpoint(endpoint);

            URL url = new URL(urlString + borrowing.getBorrow_Id());
            //URL url = new URL("http://localhost:3000/borrow/verify/" + borrowing.getBorrow_Id());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", Auth.getToken());
            conn.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("NomorInventaris", nomorInventaris);

            conn.getOutputStream().write(requestBody.toString().getBytes());

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Now perform the return operation
                performReturnOperation(borrowing.getBorrow_Id(), event);
            } else {
                showAlert("Verification Failed", "Item verification failed. Please check the information.");
            }

        } catch (IOException e) {
            showAlert("Error", "An error occurred while connecting to server: " + e.getMessage());
        }
    }

    private void performReturnOperation(int borrowId, ActionEvent event) {
        try {
            String endpoint = "/borrow/return/";
            String urlString = ApiUtil.getEndpoint(endpoint);

            URL url = new URL(urlString + borrowId);
            //URL url = new URL("http://localhost:3000/borrow/return/" + borrowId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", Auth.getToken());
            conn.setDoOutput(true);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                showAlert("Success", "Item returned successfully.");

                // Notify ManageBorrowingController to refresh the table
                if (manageBorrowingController != null) {
                    manageBorrowingController.refreshTable();
                }
            } else {
                showAlert("Error", "Failed to return item. Response code: " + responseCode);
            }

        } catch (IOException e) {
            showAlert("Error", "An error occurred while connecting to server: " + e.getMessage());
        }

        // Close the dialog
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCloseAction(ActionEvent event) {
        // Close the dialog without performing any action
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}