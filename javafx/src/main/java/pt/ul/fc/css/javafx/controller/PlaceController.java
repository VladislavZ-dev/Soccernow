package pt.ul.fc.css.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import pt.ul.fc.css.javafx.models.PlaceModel;
import pt.ul.fc.css.javafx.api.PlaceApiClient;
import pt.ul.fc.css.javafx.dto.PlaceDTO;
import javafx.scene.control.cell.PropertyValueFactory;

public class PlaceController {
    @FXML private TableView<PlaceModel> placesTable;
    @FXML private TableColumn<PlaceModel, Integer> placeIdColumn;
    @FXML private TableColumn<PlaceModel, String> stadiumColumn;
    @FXML private TableColumn<PlaceModel, String> dateTimeColumn;

    private ObservableList<PlaceModel> placesData = FXCollections.observableArrayList();


    @FXML
    private TextField stadiumField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField timeField;

    @FXML
    public void initialize() {
        setupPlaceTable();
        loadPlaces();
    }

    private void setupPlaceTable() {
        placeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        stadiumColumn.setCellValueFactory(new PropertyValueFactory<>("stadium"));
        dateTimeColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return new SimpleStringProperty(cellData.getValue().getDateTime().format(formatter));
        });
        placesTable.setItems(placesData);
    }

    private void loadPlaces() {
        new Thread(() -> {
            try {
                List<PlaceDTO> placeDTOs = PlaceApiClient.getAllPlaces();
                List<PlaceModel> places = placeDTOs.stream()
                        .map(PlaceModel::new)
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    placesData.setAll(places);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    System.err.println("Error loading places: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void createPlace(ActionEvent event) {
        String stadium = stadiumField.getText().trim();
        String time = timeField.getText().trim();
        LocalDate date = datePicker.getValue();

        if (stadium.isEmpty()) {
            showAlert("Error", "Stadium name is required!");
            return;
        }

        if (date == null) {
            showAlert("Error", "Date is required!");
            return;
        }

        if (time.isEmpty()) {
            showAlert("Error", "Time is required!");
            return;
        }

        if (!time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showAlert("Error", "Please enter time in HH:MM format (e.g., 14:30)");
            return;
        }

        try {
            LocalTime localTime = LocalTime.parse(time);
            LocalDateTime dateTime = LocalDateTime.of(date, localTime);

            PlaceDTO newPlace = new PlaceDTO();
            newPlace.setStadium(stadium);
            newPlace.setDateTime(dateTime);

            new Thread(() -> {
                try {
                    PlaceDTO createdPlace = PlaceApiClient.createPlace(newPlace);

                    Platform.runLater(() -> {
                        showAlert("Success", "Place created successfully!");
                        clearCreateForm();
                        loadPlaces();
                    });

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showAlert("Error", "Failed to create place: " + e.getMessage());
                    });
                    e.printStackTrace();
                }
            }).start();

        } catch (DateTimeParseException e) {
            showAlert("Error", "Invalid time format. Please use HH:MM (e.g., 14:30)");
        }
    }

    private void clearCreateForm() {
        stadiumField.clear();
        datePicker.setValue(null);
        timeField.clear();
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

    public void switchToCreationView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/create_place_scene.fxml");
    }

    public void switchToTournamentView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/tournament_scene.fxml");
    }

    public void switchToLoginView(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/pt/ul/fc/css/javafx/view/login_scene.fxml");
    }
}
