package com.example.testfx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AddController {

    @FXML
    private TextField txtNomorInventaris;

    @FXML
    private TextField txtNamaAlat;

    @FXML
    private TextField txtNomorSeri;

    @FXML
    private TextField txtTahunBeli;

    @FXML
    private TextField txtKondisi;

    @FXML
    private TextField txtSpesifikasi;

    @FXML
    private ComboBox<String> comboStatus;

    @FXML
    private TextArea txtDeskripsi;

    @FXML
    private ComboBox<String> comboCategory;

    @FXML
    private void handleAddItemAction() {
        String nomorInventaris = txtNomorInventaris.getText();
        String namaAlat = txtNamaAlat.getText();
        String nomorSeri = txtNomorSeri.getText();
        String tahunBeli = txtTahunBeli.getText();
        String kondisi = txtKondisi.getText();
        String spesifikasi = txtSpesifikasi.getText();
        String status = comboStatus.getValue();
        String deskripsi = txtDeskripsi.getText();
        String category = comboCategory.getValue();

        if (nomorInventaris.isEmpty() || namaAlat.isEmpty() || nomorSeri.isEmpty() || tahunBeli.isEmpty()
                || kondisi.isEmpty() || spesifikasi.isEmpty() || status == null || deskripsi.isEmpty()
                || category == null) {
            showAlert("Error", "Please fill in all fields.");
        } else {
            boolean addItemSuccessful = addItem(nomorInventaris, namaAlat, nomorSeri, tahunBeli, kondisi, spesifikasi, status, deskripsi, category);
            if (addItemSuccessful) {
                showAlert("Add Item", "Item added successfully.");
                clearFields();
            } else {
                showAlert("Add Item Failed", "An error occurred while adding the item. Please try again.");
            }
        }
    }

    private boolean addItem(String nomorInventaris, String namaAlat, String nomorSeri, String tahunBeli, String kondisi,
                            String spesifikasi, String status, String deskripsi, String category) {
        try {
            String endpoint = "/items/add";
            String urlString = ApiUtil.getEndpoint(endpoint);

            URL url = new URL(urlString);
            //URL url = new URL("http://localhost:3000/items/add"); // Adjusted URL for your item add endpoint
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Chrome/51.0.2704.103");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonItem = String.format("{\"NomorInventaris\": \"%s\", \"NamaAlat\": \"%s\", \"NomorSeri\": \"%s\", \"TahunBeli\": \"%s\", \"Kondisi\": \"%s\", \"Spesifikasi\": \"%s\", \"Status\": \"%s\", \"Deskripsi\": \"%s\", \"Category\": \"%s\"}",
                    nomorInventaris, namaAlat, nomorSeri, tahunBeli, kondisi, spesifikasi, status, deskripsi, category);
            System.out.println("Sending JSON: " + jsonItem);  // Debug log

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonItem.getBytes(StandardCharsets.UTF_8);
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

    private void clearFields() {
        txtNomorInventaris.clear();
        txtNamaAlat.clear();
        txtNomorSeri.clear();
        txtTahunBeli.clear();
        txtKondisi.clear();
        txtSpesifikasi.clear();
        comboStatus.getSelectionModel().clearSelection();
        txtDeskripsi.clear();
        comboCategory.getSelectionModel().clearSelection();
    }
}