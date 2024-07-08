package com.example.testfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    @FXML
    private TableView<Item> itemTable;

    @FXML
    private TableColumn<Item, String> colNomorInventaris;

    @FXML
    private TableColumn<Item, String> colNamaAlat;

    @FXML
    private TableColumn<Item, String> colNomorSeri;

    @FXML
    private TableColumn<Item, Integer> colTahunBeli;

    @FXML
    private TableColumn<Item, String> colKondisi;

    @FXML
    private TableColumn<Item, String> colSpesifikasi;

    @FXML
    private TableColumn<Item, String> colStatus;

    @FXML
    private TableColumn<Item, String> colDeskripsi;

    @FXML
    private TableColumn<Item, String> colCategory;

    @FXML
    private TableColumn<Item, Void> colManage;

    private ObservableList<Item> itemList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemList = FXCollections.observableArrayList();

        colNomorInventaris.setCellValueFactory(new PropertyValueFactory<>("NomorInventaris"));
        colNamaAlat.setCellValueFactory(new PropertyValueFactory<>("NamaAlat"));
        colNomorSeri.setCellValueFactory(new PropertyValueFactory<>("NomorSeri"));
        colTahunBeli.setCellValueFactory(new PropertyValueFactory<>("TahunBeli"));
        colKondisi.setCellValueFactory(new PropertyValueFactory<>("Kondisi"));
        colSpesifikasi.setCellValueFactory(new PropertyValueFactory<>("Spesifikasi"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("Status"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("Deskripsi"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("Category"));

        addManageButtonToTable();

        itemTable.setItems(itemList);

        loadData();
    }

    private void addManageButtonToTable() {
        Callback<TableColumn<Item, Void>, TableCell<Item, Void>> cellFactory = new Callback<TableColumn<Item, Void>, TableCell<Item, Void>>() {
            @Override
            public TableCell<Item, Void> call(final TableColumn<Item, Void> param) {
                final TableCell<Item, Void> cell = new TableCell<Item, Void>() {

                    private final Button btn = new Button("Manage");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Item data = getTableView().getItems().get(getIndex());
                            showManageDialog(data);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                            setContentDisplay(ContentDisplay.CENTER); // Center-align content
                        }
                    }
                };
                return cell;
            }
        };

        colManage.setCellFactory(cellFactory);
    }

    private void showManageDialog(Item item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("manage-item.fxml"));
            Parent root = loader.load();
            ManageItemController controller = loader.getController();
            controller.setItem(item);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Manage Item");
            stage.showAndWait();

            itemList.clear();
            loadData();
        } catch (IOException e) {
            showAlert("Error", "Unable to load the Manage Item page.");
        }
    }

    private void loadData() {
        try {

            String endpoint = "/items";
            String urlString = ApiUtil.getEndpoint(endpoint);

            URL url = new URL(urlString);
            //URL url = new URL("http://localhost:3000/items");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString().trim());
                JSONArray jsonArray = jsonResponse.getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Item item = new Item(
                            jsonObject.getInt("item_Id"),
                            jsonObject.getString("NomorInventaris"),
                            jsonObject.getString("NamaAlat"),
                            jsonObject.getString("NomorSeri"),
                            jsonObject.getInt("TahunBeli"),
                            jsonObject.getString("Kondisi"),
                            jsonObject.getString("Spesifikasi"),
                            jsonObject.getString("Status"),
                            jsonObject.getString("Deskripsi"),
                            jsonObject.getString("Category")
                    );
                    itemList.add(item);
                }
            } else {
                showAlert("Error", "Failed to fetch data from server. Response code: " + responseCode);
            }
        } catch (IOException e) {
            showAlert("Error", "An error occurred while fetching data: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAddItemAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-view.fxml"));
            Parent root = loader.load();
            AddController addController = loader.getController(); // Accessing AddController instance
            Stage stage = new Stage();
            stage.setTitle("Add Item");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Show and wait for it to close

            // After AddController window closes, refresh the table
            itemList.clear(); // Clear current items
            loadData(); // Reload data from server
        } catch (IOException e) {
            showAlert("Error", "Unable to load the Add Item page.");
        }
    }

    @FXML
    public void handleManageItemsAction() {
        // Implementation for managing items (optional)
    }

    @FXML
    public void handleManageBorrowingsAction() {
        try {
            // Load ManageBorrowing.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("manage-borrowing.fxml"));
            Parent root = loader.load();

            // Create a new stage for Manage Borrowings
            Stage manageBorrowingsStage = new Stage();
            manageBorrowingsStage.setTitle("Manage Borrowings");
            manageBorrowingsStage.setScene(new Scene(root));
            manageBorrowingsStage.show();

            // Close the main stage (current stage)
            Stage currentStage = (Stage) itemTable.getScene().getWindow(); // Assuming itemTable is a node in your main page
            currentStage.close();

        } catch (IOException e) {
            showAlert("Error", "Unable to load the Manage Borrowings page: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging purposes
        }
    }

    public void handleRefreshAction(ActionEvent actionEvent) {
        itemList.clear();
        loadData();
    }
}