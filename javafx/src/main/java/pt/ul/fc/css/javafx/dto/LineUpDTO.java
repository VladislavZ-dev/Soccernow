package pt.ul.fc.css.javafx.dto;

public class LineUpDTO {
    private long id;
    private String team;
    private long matchId;
    private String captain;
    private String goalkeeper;
    private String defender;
    private String rightWinger;
    private String leftWinger;
    private String pivot;

    public LineUpDTO() {}

    public LineUpDTO(long id, String team, long matchId, String captain, String goalkeeper, String defender,
            String rightWinger, String leftWinger, String pivot) {
        this.id = id;
        this.team = team;
        this.matchId = matchId;
        this.captain = captain;
        this.goalkeeper = goalkeeper;
        this.defender = defender;
        this.rightWinger = rightWinger;
        this.leftWinger = leftWinger;
        this.pivot = pivot;
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

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public String getCaptain() {
        return captain;
    }

    public void setCaptain(String captain) {
        this.captain = captain;
    }

    public String getGoalkeeper() {
        return goalkeeper;
    }

    public void setGoalkeeper(String goalkeeper) {
        this.goalkeeper = goalkeeper;
    }

    public String getDefender() {
        return defender;
    }

    public void setDefender(String defender) {
        this.defender = defender;
    }

    public String getRightWinger() {
        return rightWinger;
    }

    public void setRightWinger(String rightWinger) {
        this.rightWinger = rightWinger;
    }

    public String getLeftWinger() {
        return leftWinger;
    }

    public void setLeftWinger(String leftWinger) {
        this.leftWinger = leftWinger;
    }

    public String getPivot() {
        return pivot;
    }

    public void setPivot(String pivot) {
        this.pivot = pivot;
    }
}
