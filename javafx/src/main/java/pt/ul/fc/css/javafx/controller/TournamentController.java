package pt.ul.fc.css.javafx.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ul.fc.css.javafx.api.TournamentApiClient;
import pt.ul.fc.css.javafx.dto.TournamentDTO;
import pt.ul.fc.css.javafx.models.TournamentModel;

public class TournamentController {
    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextArea teamsTextArea;

    @FXML private TextField tournamentIdField;

    @FXML private TableView<TournamentModel> tournamentsTable;
    @FXML private TableColumn<TournamentModel, Long> idColumn;
    @FXML private TableColumn<TournamentModel, String> nameColumn;
    @FXML private TableColumn<TournamentModel, String> typeColumn;

    @FXML private TextField scheduleTournamentIdField;
    @FXML private TextField scheduleMatchIdField;
    @FXML private TextField markPlayedTournamentIdField;
    @FXML private TextField markPlayedMatchIdField;

    private ObservableList<TournamentModel> tournamentsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (typeComboBox != null) {
            typeComboBox.setItems(FXCollections.observableArrayList(
                "KNOCKOUT","POINTS"
            ));
            typeComboBox.setPromptText("Select Type");
        }

        if (tournamentsTable != null) {
            setupTournamentTable();
            loadTournaments();
        }
    }

    private void setupTournamentTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        tournamentsTable.setItems(tournamentsData);
    }

    private void loadTournaments() {
        new Thread(() -> {
            try {
                List<TournamentDTO> tournamentDTOs = TournamentApiClient.getAllTournaments();
                List<TournamentModel> tournaments = tournamentDTOs.stream()
                        .map(TournamentModel::new)
                        .collect(Collectors.toList());

                javafx.application.Platform.runLater(() -> {
                    tournamentsData.setAll(tournaments);
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    System.err.println("Error loading tournaments: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void createTournament(ActionEvent event) {
        String name = nameField.getText().trim();
        String type = typeComboBox.getValue();
        String teamsText = teamsTextArea.getText().trim();

        if (name.isEmpty()) {
            showAlert("Error", "Tournament name is required!");
            return;
        }

        if (type == null) {
            showAlert("Error", "Please select a tournament type!");
            return;
        }

        List<String> teams = new ArrayList<>();
        if (!teamsText.isEmpty()) {
            String[] teamNames = teamsText.split("\\r?\\n");
            for (String teamName : teamNames) {
                if (!teamName.trim().isEmpty()) {
                    teams.add(teamName.trim());
                }
            }
        }

        TournamentDTO newTournament = new TournamentDTO();
        newTournament.setName(name);
        newTournament.setType(type);
        newTournament.setTeamsNames(teams);

        new Thread(() -> {
            try {
                TournamentDTO createdTournament = TournamentApiClient.createTournament(newTournament);

                Platform.runLater(() -> {
                    showAlert("Success", "Tournament created successfully!");
                    clearCreateForm();
                    loadTournaments();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert("Error", "Failed to create tournament: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void scheduleMatchToTournament(ActionEvent event) {
        String tournamentIdText = scheduleTournamentIdField.getText().trim();
        String matchIdText = scheduleMatchIdField.getText().trim();

        if (tournamentIdText.isEmpty() || matchIdText.isEmpty()) {
            showAlert("Error", "Both Tournament ID and Match ID are required!");
            return;
        }

        try {
            Long tournamentId = Long.parseLong(tournamentIdText);
            Long matchId = Long.parseLong(matchIdText);

            new Thread(() -> {
                try {
                    TournamentDTO updatedTournament = TournamentApiClient.scheduleMatchToTournament(tournamentId, matchId);

                    Platform.runLater(() -> {
                        showAlert("Success", "Match scheduled to tournament successfully!");
                        loadTournaments();
                        try {
                            switchToTournamentView(event);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        showAlert("Error", "Failed to schedule match: " + e.getMessage());
                    });
                }
            }).start();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numeric IDs!");
        }
    }

    @FXML
    public void markMatchAsPlayed(ActionEvent event) {
        String tournamentIdText = markPlayedTournamentIdField.getText().trim();
        String matchIdText = markPlayedMatchIdField.getText().trim();

        if (tournamentIdText.isEmpty() || matchIdText.isEmpty()) {
            showAlert("Error", "Both Tournament ID and Match ID are required!");
            return;
        }

        try {
            Long tournamentId = Long.parseLong(tournamentIdText);
            Long matchId = Long.parseLong(matchIdText);

            new Thread(() -> {
                try {
                    TournamentDTO updatedTournament = TournamentApiClient.markMatchAsPlayed(tournamentId, matchId);

                    Platform.runLater(() -> {
                        showAlert("Success", "Match marked as played successfully!");
                        loadTournaments();
                        try {
                            switchToTournamentView(event);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        showAlert("Error", "Failed to mark match as played: " + e.getMessage());
                    });
                }
            }).start();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numeric IDs!");
        }
    }

    @FXML
    public void updateTournament(ActionEvent event) {
        String idText = tournamentIdField.getText().trim();
        String name = nameField.getText().trim();
        String type = typeComboBox.getValue();
        String teamsText = teamsTextArea.getText().trim();

        if (idText.isEmpty()) {
            showAlert("Error", "Tournament ID is required!");
            return;
        }

        if (name.isEmpty()) {
            showAlert("Error", "Tournament name is required!");
            return;
        }

        if (type == null) {
            showAlert("Error", "Please select a tournament type!");
            return;
        }

        Long tournamentId;
        try {
            tournamentId = Long.parseLong(idText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid tournament ID!");
            return;
        }

        List<String> teams = new ArrayList<>();
        if (!teamsText.isEmpty()) {
            String[] teamNames = teamsText.split("\\r?\\n");
            for (String teamName : teamNames) {
                if (!teamName.trim().isEmpty()) {
                    teams.add(teamName.trim());
                }
            }
        }

        TournamentDTO updatedTournament = new TournamentDTO();
        updatedTournament.setId(tournamentId);
        updatedTournament.setName(name);
        updatedTournament.setType(type);
        updatedTournament.setTeamsNames(teams);

        new Thread(() -> {
            try {
                TournamentDTO result = TournamentApiClient.updateTournament(tournamentId, updatedTournament);

                Platform.runLater(() -> {
                    showAlert("Success", "Tournament updated successfully!");
                    clearUpdateForm();
                    loadTournaments();
                    try {
                        switchToTournamentView(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert("Error", "Failed to update tournament: " + e.getMessage());
                });
            }
        }).start();
    }

    private void clearUpdateForm() {
        if (tournamentIdField != null) tournamentIdField.clear();
        if (nameField != null) nameField.clear();
        if (typeComboBox != null) typeComboBox.getSelectionModel().clearSelection();
        if (teamsTextArea != null) teamsTextArea.clear();
    }

    @FXML
    public void removeTournament(ActionEvent event) {
        String idText = tournamentIdField.getText().trim();

        if (idText.isEmpty()) {
            showAlert("Error", "Tournament ID is required!");
            return;
        }

        Long tournamentId;
        try {
            tournamentId = Long.parseLong(idText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid tournament ID!");
            return;
        }

        new Thread(() -> {
            try {
                TournamentDTO tournament = TournamentApiClient.getTournamentById(tournamentId);
                if (tournament == null) {
                    Platform.runLater(() -> {
                        showAlert("Error", "Tournament with ID " + tournamentId + " not found!");
                    });
                    return;
                }

                TournamentApiClient.deleteTournament(tournamentId);

                Platform.runLater(() -> {
                    showAlert("Success", "Tournament removed successfully!");
                    clearRemoveForm();
                    loadTournaments();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert("Error", "Failed to remove tournament: " + e.getMessage());
                });
            }
        }).start();
    }

    private void clearCreateForm() {
        if (nameField != null) nameField.clear();
        if (typeComboBox != null) typeComboBox.getSelectionModel().clearSelection();
        if (teamsTextArea != null) teamsTextArea.clear();
    }

    private void clearRemoveForm() {
        if (tournamentIdField != null) tournamentIdField.clear();
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
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/create_tournament_scene.fxml");
    }

    public void switchToUpdateView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/update_tournament_scene.fxml");
    }

    public void switchToDeletionView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/remove_tournament_scene.fxml");
    }

    public void switchToScheduleView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/schedule_match_scene.fxml");
    }

    public void switchToMarkedView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/mark_played_scene.fxml");
    }

    public void switchToLoginView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/login_scene.fxml");
    }
}
