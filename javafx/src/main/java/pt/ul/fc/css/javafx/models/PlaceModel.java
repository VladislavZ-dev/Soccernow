package pt.ul.fc.css.javafx.models;

import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pt.ul.fc.css.javafx.dto.PlaceDTO;

public class PlaceModel {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty stadium = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> dateTime = new SimpleObjectProperty<>();

    public PlaceModel(PlaceDTO dto) {
        if (dto != null) {
            this.id.set((int) dto.getId());
            this.stadium.set(dto.getStadium());
            this.dateTime.set(dto.getDateTime());
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

    public String getStadium() {
        return stadium.get();
    }

    public void setStadium(String stadium) {
        this.stadium.set(stadium);
    }

    public StringProperty stadiumProperty() {
        return stadium;
    }

    public LocalDateTime getDateTime() {
        return dateTime.get();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime.set(dateTime);
    }

    public ObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }
}
