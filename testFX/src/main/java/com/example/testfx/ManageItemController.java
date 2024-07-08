package com.example.testfx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ManageItemController {

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

    private Item item;

    public void setItem(Item item) {
        this.item = item;
        txtNomorInventaris.setText(item.getNomorInventaris());
        txtNamaAlat.setText(item.getNamaAlat());
        txtNomorSeri.setText(item.getNomorSeri());
        txtTahunBeli.setText(String.valueOf(item.getTahunBeli()));
        txtKondisi.setText(item.getKondisi());
        txtSpesifikasi.setText(item.getSpesifikasi());
        comboStatus.setValue(item.getStatus());
        txtDeskripsi.setText(item.getDeskripsi());
        comboCategory.setValue(item.getCategory());
    }

    @FXML
    private void handleSaveItemAction() {
        String nomorInventaris = txtNomorInventaris.getText();
        String namaAlat = txtNamaAlat.getText();
        String nomorSeri = txtNomorSeri.getText();
        int tahunBeli = Integer.parseInt(txtTahunBeli.getText());
        String kondisi = txtKondisi.getText();
        String spesifikasi = txtSpesifikasi.getText();
        String status = comboStatus.getValue();
        String deskripsi = txtDeskripsi.getText();
        String category = comboCategory.getValue();

        Item updatedItem = new Item(item.getId(), nomorInventaris, namaAlat, nomorSeri, tahunBeli, kondisi, spesifikasi, status, deskripsi, category);

        try {
            String endpoint = "/items/edit/";
            String urlString = ApiUtil.getEndpoint(endpoint);

            URL url = new URL(urlString + item.getId());
            //URL url = new URL("http://localhost:3000/items/edit/" + item.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format("{\"NomorInventaris\":\"%s\", \"NamaAlat\":\"%s\", \"NomorSeri\":\"%s\", \"TahunBeli\":%d, \"Kondisi\":\"%s\", \"Spesifikasi\":\"%s\", \"Status\":\"%s\", \"Deskripsi\":\"%s\", \"Category\":\"%s\"}",
                    nomorInventaris, namaAlat, nomorSeri, tahunBeli, kondisi, spesifikasi, status, deskripsi, category);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                showAlert("Success", "Item updated successfully.");
                closeWindow();
            } else {
                showAlert("Error", "Failed to update the item. Response Code: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            showAlert("Error", "Failed to update the item: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteAction() {
        try {
            String endpoint = "/items/delete/";
            String urlString = ApiUtil.getEndpoint(endpoint);

            URL url = new URL(urlString + item.getId());
            //URL url = new URL("http://localhost:3000/items/delete/" + item.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                showAlert("Success", "Item deleted successfully.");
                closeWindow();
            } else {
                showAlert("Error", "Failed to delete the item. Response Code: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            showAlert("Error", "Failed to delete the item: " + e.getMessage());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtNomorInventaris.getScene().getWindow();
        stage.close();
    }
}