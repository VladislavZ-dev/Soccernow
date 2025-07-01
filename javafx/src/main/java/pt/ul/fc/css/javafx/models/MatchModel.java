package pt.ul.fc.css.javafx.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import pt.ul.fc.css.javafx.dto.LineUpDTO;
import pt.ul.fc.css.javafx.dto.MatchDTO;

public class MatchModel {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty placeId = new SimpleIntegerProperty();
    private final ObjectProperty<LineUpDTO> lineup1 = new SimpleObjectProperty<>();
    private final ObjectProperty<LineUpDTO> lineup2 = new SimpleObjectProperty<>();
    private final StringProperty lineup1Display = new SimpleStringProperty();
    private final StringProperty lineup2Display = new SimpleStringProperty();
    private final IntegerProperty refereeId = new SimpleIntegerProperty();
    private final IntegerProperty statsId = new SimpleIntegerProperty();

    public MatchModel() {
    }

    public MatchModel(MatchDTO dto) {
        if (dto != null) {
            setId((int) dto.getId());
            setPlace(dto.getPlace());
            setLineup1(dto.getLineUp1());
            setLineup2(dto.getLineUp2());
            setRefereeId((int) dto.getRefId());
            setStatsId((int) dto.getStatsId());
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

    public int getPlace() {
        return placeId.get();
    }

    public void setPlace(int place) {
        this.placeId.set(place);
    }

    public IntegerProperty placeProperty() {
        return placeId;
    }

    public LineUpDTO getLineup1() {
        return lineup1.get();
    }

    public void setLineup1(LineUpDTO lineup1) {
        this.lineup1.set(lineup1);
        this.lineup1Display.set(formatLineupForDisplay(lineup1));
    }

    public ObjectProperty<LineUpDTO> lineup1Property() {
        return lineup1;
    }

    public LineUpDTO getLineup2() {
        return lineup2.get();
    }

    public void setLineup2(LineUpDTO lineup2) {
        this.lineup2.set(lineup2);
        this.lineup2Display.set(formatLineupForDisplay(lineup2));
    }

    public ObjectProperty<LineUpDTO> lineup2Property() {
        return lineup2;
    }

    public String getLineup1Display() {
        return lineup1Display.get();
    }

    public StringProperty lineup1DisplayProperty() {
        return lineup1Display;
    }

    public String getLineup2Display() {
        return lineup2Display.get();
    }

    public StringProperty lineup2DisplayProperty() {
        return lineup2Display;
    }

    public int getRefereeId() {
        return refereeId.get();
    }

    public void setRefereeId(int refereeId) {
        this.refereeId.set(refereeId);
    }

    public IntegerProperty refereeIdProperty() {
        return refereeId;
    }

    public int getStatsId() {
        return statsId.get();
    }

    public void setStatsId(int statsId) {
        this.statsId.set(statsId);
    }

    public IntegerProperty statsIdProperty() {
        return statsId;
    }

    private String formatLineupForDisplay(LineUpDTO lineup) {
        if (lineup == null) {
            return "N/A";
        }
        return String.format("%s (GK: %s, DEF: %s, RW: %s, LW: %s, PIV: %s)",
            lineup.getTeam(),
            lineup.getGoalkeeper(),
            lineup.getDefender(),
            lineup.getRightWinger(),
            lineup.getLeftWinger(),
            lineup.getPivot());
    }
}
