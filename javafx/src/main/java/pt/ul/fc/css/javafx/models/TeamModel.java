package pt.ul.fc.css.javafx.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pt.ul.fc.css.javafx.dto.TeamDTO;

public class TeamModel {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty historyId = new SimpleIntegerProperty();
    private final ListProperty<String> players = new SimpleListProperty<>(FXCollections.observableArrayList());

    public TeamModel(TeamDTO dto) {
        setId((int) dto.getId());
        setName(dto.getName());
        setHistoryId((int) dto.getHistoryId());
        if (dto.getPlayers() != null) {
            players.setAll(dto.getPlayers());
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

    public int getHistoryId() {
        return historyId.get();
    }

    public void setHistoryId(int historyId) {
        this.historyId.set(historyId);
    }

    public IntegerProperty historyIdProperty() {
        return historyId;
    }

    public ObservableList<String> getPlayers() {
        return players.get();
    }

    public void setPlayers(ObservableList<String> players) {
        this.players.set(players);
    }

    public ListProperty<String> playersProperty() {
        return players;
    }

    public int getPlayerCount() {
        return players.get() != null ? players.get().size() : 0;
    }

    public String getPlayersString() {
        if (players.get() != null && !players.get().isEmpty()) {
            return String.join(", ", players.get());
        }
        return "";
    }

    @Override
    public String toString() {
        return getName();
    }
}
