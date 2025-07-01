package pt.ul.fc.css.soccernow.dto;

import java.util.List;

public class HistoryDto {

    private long id;
    private String team;
    private List<Long> playedMatches;

    public HistoryDto() {}

    public HistoryDto(long id, String team, List<Long> playedMatches) {
        this.id = id;
        this.team = team;
        this.playedMatches = playedMatches;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public List<Long> getPlayedMatches() {
        return playedMatches;
    }

    public void setPlayedMatches(List<Long> playedMatches) {
        this.playedMatches = playedMatches;
    }
}
