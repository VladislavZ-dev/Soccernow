package pt.ul.fc.css.javafx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchDTO {
    private long id;

    @JsonProperty("lineUp1Id")
    private LineUpDTO lineUp1Id;

    @JsonProperty("lineUp2Id")
    private LineUpDTO lineUp2Id;

    private long refId;

    @JsonProperty("placeId")
    private int place;

    private long statsId;

    private long tournamentId;

    public MatchDTO() {}

    public MatchDTO(long id, LineUpDTO lineUp1Id, LineUpDTO lineUp2Id, long refId, int place, long statsId) {
        super();
        this.id = id;
        this.lineUp1Id = lineUp1Id;
        this.lineUp2Id = lineUp2Id;
        this.refId = refId;
        this.place = place;
        this.statsId = statsId;
    }

    public long getId() {
        return id;
    }

    public void setLineUp1Id(LineUpDTO lineUp1Id) {
        this.lineUp1Id = lineUp1Id;
    }

    public void setLineUp2Id(LineUpDTO lineUp2Id) {
        this.lineUp2Id = lineUp2Id;
    }

    public void setTournamentId(long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public LineUpDTO getLineUp1Id() {
        return lineUp1Id;
    }

    public LineUpDTO getLineUp2Id() {
        return lineUp2Id;
    }

    public long getTournamentId() {
        return tournamentId;
    }

    public LineUpDTO getLineUp1() {
        return lineUp1Id;
    }

    public LineUpDTO getLineUp2() {
        return lineUp2Id;
    }

    public long getRefId() {
        return refId;
    }

    public int getPlace() {
        return place;
    }

    public long getStatsId() {
        return statsId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLineUp1(LineUpDTO lineUp1) {
        this.lineUp1Id = lineUp1;
    }

    public void setLineUp2(LineUpDTO lineUp2) {
        this.lineUp2Id = lineUp2;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public void setStatsId(long statsId) {
        this.statsId = statsId;
    }
}
