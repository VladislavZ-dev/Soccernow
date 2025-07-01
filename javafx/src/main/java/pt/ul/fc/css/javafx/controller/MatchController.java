package pt.ul.fc.css.javafx.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import pt.ul.fc.css.javafx.api.MatchApiClient;
import pt.ul.fc.css.javafx.dto.LineUpDTO;
import pt.ul.fc.css.javafx.dto.MatchDTO;
import pt.ul.fc.css.javafx.dto.MatchGoalsDTO;
import pt.ul.fc.css.javafx.dto.PlayerCardDTO;
import pt.ul.fc.css.javafx.dto.PlayerGoalDTO;
import pt.ul.fc.css.javafx.models.MatchModel;

public class MatchController {
    @FXML private TextField lineup1CaptainField;
    @FXML private TextField lineup1GoalkeeperField;
    @FXML private TextField lineup1DefenderField;
    @FXML private TextField lineup1RightWingerField;
    @FXML private TextField lineup1LeftWingerField;
    @FXML private TextField lineup1PivotField;

    @FXML private TextField lineup2CaptainField;
    @FXML private TextField lineup2GoalkeeperField;
    @FXML private TextField lineup2DefenderField;
    @FXML private TextField lineup2RightWingerField;
    @FXML private TextField lineup2LeftWingerField;
    @FXML private TextField lineup2PivotField;

    @FXML private TextField lineup1TeamIdField;
    @FXML private TextField lineup2TeamIdField;

    @FXML private TextField placeField;
    @FXML private TextField refereeIdField;
    @FXML private TextField idField;

    @FXML private TableView<MatchModel> matchesTable;
    @FXML private TableColumn<MatchModel, Integer> idColumn;
    @FXML private TableColumn<MatchModel, String> placeColumn;
    @FXML private TableColumn<MatchModel, String> lineup1Column;
    @FXML private TableColumn<MatchModel, String> lineup2Column;
    @FXML private TableColumn<MatchModel, Integer> refereeIdColumn;

    private ObservableList<MatchModel> matchesData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (matchesTable != null) {
            setupMatchTable();
            loadMatches();
        }
    }

    private void setupMatchTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        lineup1Column.setCellValueFactory(new PropertyValueFactory<>("lineup1Display"));
        lineup2Column.setCellValueFactory(new PropertyValueFactory<>("lineup2Display"));
        refereeIdColumn.setCellValueFactory(new PropertyValueFactory<>("refereeId"));
        matchesTable.setItems(matchesData);
    }

    private void loadMatches() {
        new Thread(() -> {
            try {
                List<MatchDTO> matchDTOs = MatchApiClient.getAllMatches();
                List<MatchModel> matches = matchDTOs.stream()
                        .map(MatchModel::new)
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    matchesData.setAll(matches);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    System.err.println("Error loading matches: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void createMatch(ActionEvent event) {
        LineUpDTO lineup1 = createLineup1FromFields();
        if (lineup1 == null) {
            showAlert("Error", "Please fill all Lineup 1 positions!");
            return;
        }

        LineUpDTO lineup2 = createLineup2FromFields();
        if (lineup2 == null) {
            showAlert("Error", "Please fill all Lineup 2 positions!");
            return;
        }

        String place = placeField.getText().trim();
        String refereeIdText = refereeIdField.getText().trim();

        if (place.isEmpty()) {
            showAlert("Error", "Place is required!");
            return;
        }

        if (refereeIdText.isEmpty()) {
            showAlert("Error", "Referee ID is required!");
            return;
        }

        int refereeId;
        int placeId;
        try {
            refereeId = Integer.parseInt(refereeIdText);
            placeId = Integer.parseInt(place);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for Referee ID and Place ID!");
            return;
        }

        MatchDTO newMatch = new MatchDTO();
        newMatch.setLineUp1(lineup1);
        newMatch.setLineUp2(lineup2);
        newMatch.setRefId(refereeId);
        newMatch.setPlace(placeId);

        new Thread(() -> {
            try {
                MatchDTO createdMatch = MatchApiClient.createMatch(newMatch);

                Platform.runLater(() -> {
                    showAlert("Success", "Match created successfully with ID: " + createdMatch.getId());
                    clearCreateForm();
                    loadMatches();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert("Error", "Failed to create match: " + e.getMessage());
                });
            }
        }).start();
    }

    private LineUpDTO createLineup1FromFields() {
        String captain = getFieldText(lineup1CaptainField);
        String goalkeeper = getFieldText(lineup1GoalkeeperField);
        String defender = getFieldText(lineup1DefenderField);
        String rightWinger = getFieldText(lineup1RightWingerField);
        String leftWinger = getFieldText(lineup1LeftWingerField);
        String pivot = getFieldText(lineup1PivotField);
        String team = getFieldText(lineup1TeamIdField);

        if (captain.isEmpty() || goalkeeper.isEmpty() || defender.isEmpty() ||
            rightWinger.isEmpty() || leftWinger.isEmpty() || pivot.isEmpty() || team.isEmpty()) {
            return null;
        }

        LineUpDTO lineup = new LineUpDTO();
        lineup.setCaptain(captain);
        lineup.setGoalkeeper(goalkeeper);
        lineup.setDefender(defender);
        lineup.setRightWinger(rightWinger);
        lineup.setLeftWinger(leftWinger);
        lineup.setPivot(pivot);
        lineup.setTeam(team);

        return lineup;
    }

    private LineUpDTO createLineup2FromFields() {
        String captain = getFieldText(lineup2CaptainField);
        String goalkeeper = getFieldText(lineup2GoalkeeperField);
        String defender = getFieldText(lineup2DefenderField);
        String rightWinger = getFieldText(lineup2RightWingerField);
        String leftWinger = getFieldText(lineup2LeftWingerField);
        String pivot = getFieldText(lineup2PivotField);
        String team = getFieldText(lineup2TeamIdField);

        if (captain.isEmpty() || goalkeeper.isEmpty() || defender.isEmpty() ||
            rightWinger.isEmpty() || leftWinger.isEmpty() || pivot.isEmpty() || team.isEmpty()) {
            return null;
        }

        LineUpDTO lineup = new LineUpDTO();
        lineup.setCaptain(captain);
        lineup.setGoalkeeper(goalkeeper);
        lineup.setDefender(defender);
        lineup.setRightWinger(rightWinger);
        lineup.setLeftWinger(leftWinger);
        lineup.setPivot(pivot);
        lineup.setTeam(team);

        return lineup;
    }

    private String getFieldText(TextField field) {
        return field != null ? field.getText().trim() : "";
    }

    @FXML
    private void registerGoals(ActionEvent event) {
        String matchIdText = idField.getText().trim();

        if (matchIdText.isEmpty()) {
            showAlert("Error", "Match ID is required!");
            return;
        }

        try {
            long matchId = Long.parseLong(matchIdText);

            Dialog<List<PlayerGoalDTO>> dialog = createGoalsDialog();
            Optional<List<PlayerGoalDTO>> result = dialog.showAndWait();

            if (result.isPresent() && !result.get().isEmpty()) {
                MatchGoalsDTO goalsDTO = new MatchGoalsDTO(matchId, result.get());

                new Thread(() -> {
                    try {
                        boolean success = MatchApiClient.registerGoals(goalsDTO);

                        Platform.runLater(() -> {
                            if (success) {
                                showAlert("Success", "Goals registered successfully!");
                            } else {
                                showAlert("Error", "Failed to register goals");
                            }
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            showAlert("Error", "Failed to register goals: " + e.getMessage());
                        });
                    }
                }).start();
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid Match ID!");
        }
    }

    @FXML
    private void registerCard(ActionEvent event) {
        String matchIdText = idField.getText().trim();

        if (matchIdText.isEmpty()) {
            showAlert("Error", "Match ID is required!");
            return;
        }

        try {
            long matchId = Long.parseLong(matchIdText);

            Dialog<PlayerCardDTO> dialog = createCardDialog();
            Optional<PlayerCardDTO> result = dialog.showAndWait();

            if (result.isPresent()) {
                PlayerCardDTO cardDTO = result.get();
                cardDTO.setMatchId(matchId);

                new Thread(() -> {
                    try {
                        boolean success = MatchApiClient.registerCard(cardDTO);

                        Platform.runLater(() -> {
                            if (success) {
                                showAlert("Success", "Card registered successfully!");
                            } else {
                                showAlert("Error", "Failed to register card");
                            }
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            showAlert("Error", "Failed to register card: " + e.getMessage());
                        });
                    }
                }).start();
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid Match ID!");
        }
    }

    private Dialog<List<PlayerGoalDTO>> createGoalsDialog() {
        Dialog<List<PlayerGoalDTO>> dialog = new Dialog<>();
        dialog.setTitle("Register Goals");
        dialog.setHeaderText("Enter player goals for this match");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField playerIdField = new TextField();
        playerIdField.setPromptText("Player ID");
        TextField goalsField = new TextField();
        goalsField.setPromptText("Goals");

        grid.add(new Label("Player ID:"), 0, 0);
        grid.add(playerIdField, 1, 0);
        grid.add(new Label("Goals:"), 0, 1);
        grid.add(goalsField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        List<PlayerGoalDTO> goalsList = new ArrayList<>();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    long playerId = Long.parseLong(playerIdField.getText());
                    int goals = Integer.parseInt(goalsField.getText());
                    goalsList.add(new PlayerGoalDTO(playerId, goals));
                    return goalsList;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter valid numbers for Player ID and Goals");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private Dialog<PlayerCardDTO> createCardDialog() {
        Dialog<PlayerCardDTO> dialog = new Dialog<>();
        dialog.setTitle("Register Card");
        dialog.setHeaderText("Enter card details for this match");

        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField playerNameField = new TextField();
        playerNameField.setPromptText("Player Name");
        CheckBox redCardCheckBox = new CheckBox("Red Card");

        grid.add(new Label("Player Name:"), 0, 0);
        grid.add(playerNameField, 1, 0);
        grid.add(redCardCheckBox, 0, 1, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                String playerName = playerNameField.getText().trim();
                if (playerName.isEmpty()) {
                    showAlert("Error", "Player name is required!");
                    return null;
                }
                return new PlayerCardDTO(0, playerName, redCardCheckBox.isSelected());
            }
            return null;
        });

        return dialog;
    }

    private void clearCreateForm() {
        clearField(lineup1CaptainField);
        clearField(lineup1GoalkeeperField);
        clearField(lineup1DefenderField);
        clearField(lineup1RightWingerField);
        clearField(lineup1LeftWingerField);
        clearField(lineup1PivotField);
        clearField(lineup1TeamIdField);

        clearField(lineup2CaptainField);
        clearField(lineup2GoalkeeperField);
        clearField(lineup2DefenderField);
        clearField(lineup2RightWingerField);
        clearField(lineup2LeftWingerField);
        clearField(lineup2PivotField);
        clearField(lineup2TeamIdField);

        clearField(placeField);
        clearField(refereeIdField);
    }

    private void clearUpdateForm() {
        clearField(idField);
        clearCreateForm();
    }

    private void clearField(TextField field) {
        if (field != null) {
            field.clear();
        }
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

    public void switchToPlaceView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/place_scene.fxml");
    }

    public void switchToMatchView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/matches_scene.fxml");
    }

    public void switchToCreationView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/create_match_scene.fxml");
    }

    public void switchToUpdateView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/update_match_scene.fxml");
    }

    public void switchToTournamentView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/tournament_scene.fxml");
    }

    public void switchToLoginView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/login_scene.fxml");
    }
}
