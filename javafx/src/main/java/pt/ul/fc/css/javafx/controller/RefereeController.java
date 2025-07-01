package pt.ul.fc.css.javafx.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ul.fc.css.javafx.api.RefereeApiClient;
import pt.ul.fc.css.javafx.dto.RefereeDTO;
import pt.ul.fc.css.javafx.models.RefereeModel;

public class RefereeController {

    @FXML private TableView<RefereeModel> refereesTable;
    @FXML private TableColumn<RefereeModel, Integer> idColumn;
    @FXML private TableColumn<RefereeModel, String> nameColumn;
    @FXML private TableColumn<RefereeModel, String> certificateColumn;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> certificateComboBox;
    @FXML private TextField idField;
    @FXML private Button updateRefereeButton;
    @FXML private Button createRefereeButton;
    @FXML private Button removeRefereeButton;
    @FXML private Button backButton;

    private final ObservableList<RefereeModel> refereesData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadReferees();
        setupCertificateComboBox();
    }

    private void setupTableColumns() {
        if (refereesTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            certificateColumn.setCellValueFactory(new PropertyValueFactory<>("certificate"));
            refereesTable.setItems(refereesData);
        }
    }

    private void loadReferees() {
        new Thread(() -> {
            try {
                List<RefereeDTO> refereeDTOs = RefereeApiClient.getAllReferees();
                List<RefereeModel> referees = refereeDTOs.stream()
                        .map(RefereeModel::new)
                        .collect(Collectors.toList());

                javafx.application.Platform.runLater(() -> {
                    refereesData.setAll(referees);
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Failed to load referees: " + e.getMessage());
                });
            }
        }).start();
    }

    private void setupCertificateComboBox() {
        if (certificateComboBox != null) {
            certificateComboBox.setItems(FXCollections.observableArrayList(
                "CERTIFIED","UNCERTIFIED"
            ));
            certificateComboBox.setPromptText("Select Certificate");
        }
    }

    @FXML
    public void createReferee(ActionEvent event) {
        String name = nameField.getText().trim();
        String certificate = certificateComboBox.getValue();

        if (name.isEmpty()) {
            showAlert("Error", "Referee name is required!");
            return;
        }

        if (certificate == null) {
            showAlert("Error", "Please select a certificate level!");
            return;
        }

        RefereeDTO newReferee = new RefereeDTO();
        newReferee.setName(name);
        newReferee.setCertificate(certificate);
        newReferee.setId((long) 0);

        new Thread(() -> {
            try {
                RefereeDTO createdReferee = RefereeApiClient.createReferee(newReferee);

                javafx.application.Platform.runLater(() -> {
                    showAlert("Success", "Referee created successfully!");
                    clearForm();
                    loadReferees();
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Failed to create referee: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void updateReferee(ActionEvent event) {
        String idText = idField.getText().trim();
        String name = nameField.getText().trim();
        String certificate = certificateComboBox.getValue();

        if (idText.isEmpty()) {
            showAlert("Error", "Referee ID is required!");
            return;
        }

        if (name.isEmpty()) {
            showAlert("Error", "Referee name is required!");
            return;
        }

        if (certificate == null) {
            showAlert("Error", "Please select a certificate level!");
            return;
        }

        try {
            Long id = Long.parseLong(idText);
            RefereeDTO updatedReferee = new RefereeDTO();
            updatedReferee.setId(id);
            updatedReferee.setName(name);
            updatedReferee.setCertificate(certificate);

            new Thread(() -> {
                try {
                    RefereeDTO result = RefereeApiClient.updateReferee(updatedReferee);

                    javafx.application.Platform.runLater(() -> {
                        showAlert("Success", "Referee updated successfully!");
                        loadReferees();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        showAlert("Error", "Failed to update referee: " + e.getMessage());
                    });
                }
            }).start();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid referee ID format!");
        }
    }

    @FXML
    public void removeReferee(ActionEvent event) {
        String idText = idField.getText().trim();

        if (idText.isEmpty()) {
            showAlert("Error", "Please enter a Referee ID to remove");
            return;
        }

        new Thread(() -> {
            try {
                long id = Long.parseLong(idText);
                boolean success = RefereeApiClient.deleteRefereeById(id);

                javafx.application.Platform.runLater(() -> {
                    if (success) {
                        showAlert("Success", "Referee with ID " + id + " removed successfully!");
                        idField.clear();
                        loadReferees();
                    } else {
                        showAlert("Not Found", "No referee found with ID: " + id);
                    }
                });
            } catch (NumberFormatException e) {
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Invalid ID format. Please enter a numeric value.");
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Failed to remove referee: " + e.getMessage());
                });
            }
        }).start();
    }


    private void clearForm() {
        nameField.clear();
        certificateComboBox.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void switchToPlayerView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/player_scene.fxml");
    }

    public void switchToRefereeView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/referee_scene.fxml");
    }

    public void switchToTeamView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/team_scene.fxml");
    }

    public void switchToMatchView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/matches_scene.fxml");
    }

    public void switchToPlaceView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/place_scene.fxml");
    }

    public void switchToTournamentView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/tournament_scene.fxml");
    }

    public void switchToCreationView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/create_referee_scene.fxml");
    }

    public void switchToUpdateView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/update_referee_scene.fxml");
    }

    public void switchToDeletionView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/remove_referee_scene.fxml");
    }

    public void switchToLoginView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/login_scene.fxml");
    }
}
