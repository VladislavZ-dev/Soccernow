package pt.ul.fc.css.javafx.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ul.fc.css.javafx.api.TeamApiClient;
import pt.ul.fc.css.javafx.dto.TeamDTO;
import pt.ul.fc.css.javafx.models.TeamModel;

public class TeamController {
    @FXML private TextField nameField;
    @FXML private TextArea playersField;
    @FXML private TextField idField;

    @FXML private TableView<TeamModel> teamsTable;
    @FXML private TableColumn<TeamModel, Long> idColumn;
    @FXML private TableColumn<TeamModel, String> nameColumn;
    @FXML private TableColumn<TeamModel, Integer> playerCountColumn;
    @FXML private TableColumn<TeamModel, String> playersColumn;

    private ObservableList<TeamModel> teamsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (teamsTable != null) {
            setupTeamTable();
            loadTeams();
        }
    }

    private void setupTeamTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        playerCountColumn.setCellValueFactory(new PropertyValueFactory<>("playerCount"));
        playersColumn.setCellValueFactory(new PropertyValueFactory<>("playersString"));
        teamsTable.setItems(teamsData);
    }

    private void loadTeams() {
        new Thread(() -> {
            try {
                List<TeamDTO> teamDTOs = TeamApiClient.getAllTeams();
                List<TeamModel> teams = teamDTOs.stream()
                        .map(TeamModel::new)
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    teamsData.setAll(teams);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    System.err.println("Error loading teams: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void createTeam(ActionEvent event) {
        String name = nameField.getText().trim();
        String playersText = playersField.getText().trim();

        if (name.isEmpty()) {
            showAlert("Error", "Team name is required!");
            return;
        }

        TeamDTO newTeam = new TeamDTO();
        newTeam.setName(name);

        if (!playersText.isEmpty()) {
            List<String> playersList = Arrays.stream(playersText.split("[,\n]"))
                    .map(String::trim)
                    .filter(player -> !player.isEmpty())
                    .collect(Collectors.toList());
            newTeam.setPlayers(playersList);
        }

        new Thread(() -> {
            try {
                TeamDTO createdTeam = TeamApiClient.createTeam(newTeam);

                Platform.runLater(() -> {
                    showAlert("Success", "Team created successfully!");
                    clearCreateForm();
                    loadTeams();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert("Error", "Failed to create team: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void removeTeam(ActionEvent event) {
        String idText = idField.getText().trim();

        if (idText.isEmpty()) {
            showAlert("Error", "Team ID is required!");
            return;
        }

        Long teamId;
        try {
            teamId = Long.parseLong(idText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid team ID!");
            return;
        }

        new Thread(() -> {
            try {

                Platform.runLater(() -> {
                    showAlert("Error", "Team with ID " + teamId + " not found!");
                });

                TeamApiClient.deleteTeam(teamId);

                Platform.runLater(() -> {
                    showAlert("Success", "Team removed successfully!");
                    clearRemoveForm();
                    loadTeams();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert("Error", "Failed to remove team: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void updateTeam(ActionEvent event) {
        String idText = idField.getText().trim();
        String name = nameField.getText().trim();
        String playersText = playersField.getText().trim();

        if (idText.isEmpty()) {
            showAlert("Error", "Team ID is required!");
            return;
        }

        if (name.isEmpty()) {
            showAlert("Error", "Team name is required!");
            return;
        }

        Long teamId;
        try {
            teamId = Long.parseLong(idText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid team ID!");
            return;
        }

        TeamDTO updatedTeam = new TeamDTO();
        updatedTeam.setName(name);

        if (!playersText.isEmpty()) {
            List<String> playersList = Arrays.stream(playersText.split("[,\n]"))
                    .map(String::trim)
                    .filter(player -> !player.isEmpty())
                    .collect(Collectors.toList());
            updatedTeam.setPlayers(playersList);
        }

        new Thread(() -> {
            try {

                Platform.runLater(() -> {
                    showAlert("Error", "Team with ID " + teamId + " not found!");
                });


                TeamDTO result = TeamApiClient.updateTeam(teamId, updatedTeam);

                Platform.runLater(() -> {
                    showAlert("Success", "Team updated successfully!");
                    clearUpdateForm();
                    loadTeams();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert("Error", "Failed to update team: " + e.getMessage());
                });
            }
        }).start();
    }

    private void clearCreateForm() {
        if (nameField != null) nameField.clear();
        if (playersField != null) playersField.clear();
    }

    private void clearRemoveForm() {
        if (idField != null) idField.clear();
    }

    private void clearUpdateForm() {
        if (idField != null) idField.clear();
        if (nameField != null) nameField.clear();
        if (playersField != null) playersField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/create_team_scene.fxml");
    }

    public void switchToUpdateView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/update_team_scene.fxml");
    }

    public void switchToAdditionView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/add_players_scene.fxml");
    }

    public void switchToDeletionView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/remove_team_scene.fxml");
    }

    public void switchToLoginView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/login_scene.fxml");
    }
}
