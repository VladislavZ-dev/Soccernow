package pt.ul.fc.css.javafx.dto;

public class PlayerGoalDTO {
    private long playerId;
    private int goals;

    public PlayerGoalDTO() {}

    public PlayerGoalDTO(long playerId, int goals) {
        this.playerId = playerId;
        this.goals = goals;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }
}
