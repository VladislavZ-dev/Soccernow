package pt.ul.fc.css.javafx.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import pt.ul.fc.css.javafx.dto.TournamentDTO;

public class TournamentModel {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final ListProperty<String> teams = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Long> futureMatches = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Long> matchesPlayed = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final MapProperty<String, String> teamsPlacements = new SimpleMapProperty<>(FXCollections.observableHashMap());

    public TournamentModel(TournamentDTO dto) {
        setId((int) dto.getId());
        setName(dto.getName());
        setType(dto.getType());
        if (dto.getTeamsNames() != null) {
            teams.setAll(dto.getTeamsNames());
        }
        if (dto.getScheduledMatchesIds() != null) {
            futureMatches.setAll(dto.getScheduledMatchesIds());
        }
        if (dto.getPlayedMatchesIds() != null) {
            matchesPlayed.setAll(dto.getPlayedMatchesIds());
        }
        if (dto.getTeamsPlacements() != null) {
            teamsPlacements.putAll(dto.getTeamsPlacements());
        }
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public StringProperty typeProperty() {
        return type;
    }

    public ObservableList<String> getTeams() {
        return teams.get();
    }

    public void setTeams(ObservableList<String> teams) {
        this.teams.set(teams);
    }

    public ListProperty<String> teamsProperty() {
        return teams;
    }

    public ObservableList<Long> getFutureMatches() {
        return futureMatches.get();
    }

    public void setFutureMatches(ObservableList<Long> futureMatches) {
        this.futureMatches.set(futureMatches);
    }

    public ListProperty<Long> futureMatchesProperty() {
        return futureMatches;
    }

    public ObservableList<Long> getMatchesPlayed() {
        return matchesPlayed.get();
    }

    public void setMatchesPlayed(ObservableList<Long> matchesPlayed) {
        this.matchesPlayed.set(matchesPlayed);
    }

    public ListProperty<Long> matchesPlayedProperty() {
        return matchesPlayed;
    }

    public ObservableMap<String, String> getTeamsPlacements() {
        return teamsPlacements.get();
    }

    public void setTeamsPlacements(ObservableMap<String, String> teamsPlacements) {
        this.teamsPlacements.set(teamsPlacements);
    }

    public MapProperty<String, String> teamsPlacementsProperty() {
        return teamsPlacements;
    }
}
