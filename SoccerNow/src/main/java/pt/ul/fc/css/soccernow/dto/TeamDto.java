package pt.ul.fc.css.soccernow.dto;

import java.util.List;

public class TeamDto {

    private long id;
    private String name;
    private long historyId;
    private List<String> players;

    public TeamDto() {}

    public TeamDto(long id, String name, long historyId, List<String> players) {
        this.id = id;
        this.name = name;
        this.historyId = historyId;
        this.players = players;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(long historyId) {
        this.historyId = historyId;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }



}
