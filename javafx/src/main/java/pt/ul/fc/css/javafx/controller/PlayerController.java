package pt.ul.fc.css.javafx.controller;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pt.ul.fc.css.javafx.models.PlayerModel;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ul.fc.css.javafx.api.PlayerApiClient;
import pt.ul.fc.css.javafx.dto.PlayerDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerController {
    @FXML
    private TableView<PlayerModel> playersTable;

    @FXML
    private TableColumn<PlayerModel, Integer> idColumn;

    @FXML
    private TableColumn<PlayerModel, String> nameColumn;

    @FXML
    private TableColumn<PlayerModel, String> positionColumn;

    @FXML
    private TableColumn<PlayerModel, String> teamsColumn;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> positionComboBox;

    @FXML
    private TextArea teamsTextArea;

    @FXML
    private Button createPlayerButton;

    @FXML
    private TextField idField;

    @FXML
    private Button updatePlayerButton;

    @FXML
    private ComboBox<String> removeByComboBox;

    @FXML
    private TextField removeField;

    @FXML
    private Button removePlayerButton;

    @FXML
    private Button backButton;

    private final ObservableList<PlayerModel> playersData = FXCollections.observableArrayList();

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        teamsColumn.setCellValueFactory(new PropertyValueFactory<>("teamsString"));
        playersTable.setItems(playersData);
    }

    private void loadPlayers() {
        new Thread(() -> {
            try {
                List<PlayerDTO> playerDTOs = PlayerApiClient.getAllPlayers();
                List<PlayerModel> players = playerDTOs.stream()
                        .map(PlayerModel::new)
                        .collect(Collectors.toList());

                javafx.application.Platform.runLater(() -> {
                    playersData.setAll(players);
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    System.err.println("Error loading players: " + e.getMessage());
                });
            }
        }).start();
    }

    private void setupComboBoxes() {
        if (positionComboBox != null) {
            positionComboBox.setItems(FXCollections.observableArrayList(
                "GOALKEEPER", "DEFENDER", "RIGHT_WINGER", "LEFT_WINGER", "PIVOT"
            ));
            positionComboBox.setPromptText("Select Position");
        }
    }

    @FXML
    public void initialize() {
        if (playersTable != null && idColumn != null) {
            setupTableColumns();
            loadPlayers();
        }

        setupComboBoxes();
        setupRemoveOptions();
    }

    @FXML
    public void createPlayer(ActionEvent event) {
        String name = nameField.getText().trim();
        String position = positionComboBox.getValue();
        String teamsText = teamsTextArea.getText().trim();

        if (name.isEmpty()) {
            showAlert("Error", "Player name is required!");
            return;
        }

        if (position == null) {
            showAlert("Error", "Please select a position!");
            return;
        }

        PlayerDTO newPlayer = new PlayerDTO();
        newPlayer.setId((long) 0);
        newPlayer.setName(name);
        newPlayer.setPosition(position);

        List<String> teams = new ArrayList<>();
        if (!teamsText.isEmpty()) {
            String[] teamNames = teamsText.split("\\r?\\n");
            for (String teamName : teamNames) {
                if (!teamName.trim().isEmpty()) {
                    teams.add(teamName.trim());
                }
            }
        }
        newPlayer.setTeams(teams);

        new Thread(() -> {
            try {
                PlayerDTO createdPlayer = PlayerApiClient.createPlayer(newPlayer);

                javafx.application.Platform.runLater(() -> {
                    showAlert("Success", "Player created successfully!");
                    clearForm();
                    if (playersTable != null) {
                        loadPlayers();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Failed to create player: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void updatePlayer(ActionEvent event) {
        String idText = idField.getText().trim();
        String name = nameField.getText().trim();
        String position = positionComboBox.getValue();
        String teamsText = teamsTextArea.getText().trim();

        if (idText.isEmpty()) {
            showAlert("Error", "No player selected!");
            return;
        }

        if (name.isEmpty()) {
            showAlert("Error", "Player name is required!");
            return;
        }

        if (position == null) {
            showAlert("Error", "Please select a position!");
            return;
        }

        try {
            long id = Long.parseLong(idText);
            PlayerDTO updatedPlayer = new PlayerDTO();
            updatedPlayer.setId(id);
            updatedPlayer.setName(name);
            updatedPlayer.setPosition(position);

            List<String> teams = new ArrayList<>();
            if (!teamsText.isEmpty()) {
                String[] teamNames = teamsText.split("\\r?\\n");
                for (String teamName : teamNames) {
                    if (!teamName.trim().isEmpty()) {
                        teams.add(teamName.trim());
                    }
                }
            }
            updatedPlayer.setTeams(teams);

            new Thread(() -> {
                try {
                    PlayerDTO result = PlayerApiClient.updatePlayer(updatedPlayer);

                    javafx.application.Platform.runLater(() -> {
                        showAlert("Success", "Player updated successfully!");
                        clearForm();
                        loadPlayers();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        showAlert("Error", "Failed to update player: " + e.getMessage());
                    });
                }
            }).start();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid player ID!");
        }
    }

    private void setupRemoveOptions() {
        if (removeByComboBox != null) {
            removeByComboBox.setItems(FXCollections.observableArrayList(
                "ID", "Name"
            ));
            removeByComboBox.setPromptText("Select Option");
        }
    }

    @FXML
    public void removePlayer(ActionEvent event) {
        String idText = idField.getText().trim();

        if (idText.isEmpty()) {
            showAlert("Error", "Please enter a Player ID to remove");
            return;
        }

        new Thread(() -> {
            try {
                long id = Long.parseLong(idText);
                boolean success = PlayerApiClient.deletePlayerById(id);

                javafx.application.Platform.runLater(() -> {
                    if (success) {
                        showAlert("Success", "Player with ID " + id + " removed successfully!");
                        idField.clear();
                        loadPlayers();
                    } else {
                        showAlert("Not Found", "No player found with ID: " + id);
                    }
                });
            } catch (NumberFormatException e) {
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Invalid ID format. Please enter a numeric value.");
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Failed to remove player: " + e.getMessage());
                });
            }
        }).start();
    }

    private void clearForm() {
        if (nameField != null) nameField.clear();
        if (positionComboBox != null) positionComboBox.setValue(null);
        if (teamsTextArea != null) teamsTextArea.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert;
        if (title.equals("Error")) {
            alert = new Alert(Alert.AlertType.ERROR);
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }
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
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/create_player_scene.fxml");
    }

    public void switchToUpdateView(ActionEvent event) throws IOException {
       SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/update_player_scene.fxml");
    }

    public void switchToDeletionView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/remove_player_scene.fxml");
    }

    public void switchToLoginView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/login_scene.fxml");
    }
}
