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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageBorrowingController implements Initializable {

    @FXML
    private TableView<Borrowing> borrowingTable;

    @FXML
    private TableColumn<Borrowing, Integer> colBorrowId;

    @FXML
    private TableColumn<Borrowing, Integer> colUserId;

    @FXML
    private TableColumn<Borrowing, Integer> colItemId;

    @FXML
    private TableColumn<Borrowing, String> colUsername;

    @FXML
    private TableColumn<Borrowing, String> colNamaAlat;

    @FXML
    private TableColumn<Borrowing, Void> colReturnButton;

    private ObservableList<Borrowing> borrowingList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        borrowingList = FXCollections.observableArrayList();

        colBorrowId.setCellValueFactory(new PropertyValueFactory<>("borrow_Id"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("user_Id"));
        colItemId.setCellValueFactory(new PropertyValueFactory<>("item_Id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colNamaAlat.setCellValueFactory(new PropertyValueFactory<>("namaAlat"));

        initializeReturnButtonColumn();

        borrowingTable.setItems(borrowingList);

        loadData();
    }

    private void initializeReturnButtonColumn() {
        colReturnButton.setCellFactory(param -> new TableCell<>() {
            private final Button returnButton = new Button("Return");
            private final HBox hBox = new HBox(returnButton);

            {
                hBox.setStyle("-fx-alignment: CENTER;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Borrowing borrowing = getTableView().getItems().get(getIndex());
                    if (borrowing.getReturn_Date().isEmpty()) {
                        setGraphic(hBox);
                        returnButton.setOnAction(event -> openReturnItemDialog(borrowing));
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void loadData() {
        try {
            String token = Auth.getToken();
            if (token == null || token.isEmpty()) {
                System.out.println("Token is null or empty. Cannot proceed.");
                return;
            }
            String endpoint = "/borrow/all";
            String urlString = ApiUtil.getEndpoint(endpoint);

            URL url = new URL(urlString);
            //URL url = new URL("http://localhost:3000/borrow/all");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", Auth.getToken());

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
                    int borrowId = jsonObject.getInt("borrow_Id");
                    int userId = jsonObject.getInt("user_Id");
                    int itemId = jsonObject.getInt("item_Id");
                    String borrowDate = jsonObject.getString("borrow_Date");
                    String returnDate = jsonObject.isNull("return_Date") ? "" : jsonObject.getString("return_Date");
                    String username = jsonObject.optString("email", "Unknown");
                    String namaAlat = jsonObject.optString("NamaAlat", "Unknown");

                    Borrowing borrowing = new Borrowing(borrowId, userId, itemId, borrowDate, returnDate, username, namaAlat);
                    borrowingList.add(borrowing);
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

    private void openReturnItemDialog(Borrowing borrowing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("return-item.fxml"));
            Parent root = loader.load();

            ReturnItemController controller = loader.getController();
            controller.initData(borrowing, this);

            Stage returnItemStage = new Stage();
            returnItemStage.setTitle("Return Item");
            returnItemStage.setScene(new Scene(root));
            returnItemStage.show();

        } catch (IOException e) {
            showAlert("Error", "Unable to open Return Item dialog: " + e.getMessage());
        }
    }

    public void handleManageItemsAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-page.fxml"));
            Parent root = loader.load();

            Stage manageBorrowingsStage = new Stage();
            manageBorrowingsStage.setTitle("Manage Items");
            manageBorrowingsStage.setScene(new Scene(root));
            manageBorrowingsStage.show();

            Stage currentStage = (Stage) borrowingTable.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showAlert("Error", "Unable to load the Manage items page: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleManageBorrowingsAction() {
    }

    public void handleRefreshAction() {
        refreshTable();
    }

    public void refreshTable() {
        borrowingList.clear();
        loadData();
    }

    @FXML
    private void handleClearReturnedAction() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Clear Returned Items");
        confirmationAlert.setContentText("Are you sure you want to clear all returned items? This action cannot be undone.");

        // Show the confirmation dialog and wait for the user response
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User confirmed, proceed with clearing returned items
            clearReturnedItems();
        } else {
            // User cancelled, do nothing
            confirmationAlert.close();
        }
    }

    private void clearReturnedItems() {
        try {
            String token = Auth.getToken();
            if (token == null || token.isEmpty()) {
                showAlert("Error", "Token is null or empty. Cannot proceed.");
                return;
            }
            String endpoint = "/borrow/clear-returned";
            String urlString = ApiUtil.getEndpoint(endpoint);

            URL url = new URL(urlString);
           // URL url = new URL("http://localhost:3000/borrow/clear-returned");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", token);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                showAlert("Success", "Returned borrowings cleared successfully.");
                refreshTable(); // Refresh the table to update the data
            } else {
                showAlert("Error", "Failed to clear returned borrowings. Response code: " + responseCode);
            }
        } catch (IOException e) {
            showAlert("Error", "An error occurred while clearing returned borrowings: " + e.getMessage());
        }
    }

}