package pt.ul.fc.css.javafx.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pt.ul.fc.css.javafx.dto.PlayerDTO;
import java.util.List;

public class PlayerModel {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty position = new SimpleStringProperty();
    private StringProperty teamsString = new SimpleStringProperty();
    private List<String> teams;

    public PlayerModel(PlayerDTO dto) {
        this.id = new SimpleIntegerProperty((int) dto.getId());
        this.name = new SimpleStringProperty(dto.getName());
        this.position = new SimpleStringProperty(dto.getPosition());
        this.teams = dto.getTeams();
        this.teamsString = new SimpleStringProperty(formatTeamsString(dto.getTeams()));
    }

    private String formatTeamsString(List<String> teamsList) {
        if (teamsList == null || teamsList.isEmpty()) {
            return "No teams";
        }
        return String.join(", ", teamsList);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getPosition() {
        return position.get();
    }

    public void setPosition(String value) {
        position.set(value);
    }

    public StringProperty positionProperty() {
        return position;
    }

    public String getTeamsString() {
        return teamsString.get();
    }

    public void setTeamsString(String value) {
        teamsString.set(value);
    }

    public StringProperty teamsStringProperty() {
        return teamsString;
    }

    public List<String> getTeams() {
        return teams;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
        this.teamsString.set(formatTeamsString(teams));
    }
}
