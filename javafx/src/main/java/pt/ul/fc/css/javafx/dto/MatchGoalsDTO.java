package pt.ul.fc.css.javafx.dto;

import java.util.List;

public class MatchGoalsDTO {
    private long matchId;
    private List<PlayerGoalDTO> playerGoals;

    public MatchGoalsDTO() {}

    public MatchGoalsDTO(long matchId, List<PlayerGoalDTO> playerGoals) {
        this.matchId = matchId;
        this.playerGoals = playerGoals;
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public List<PlayerGoalDTO> getPlayerGoals() {
        return playerGoals;
    }

    public void setPlayerGoals(List<PlayerGoalDTO> playerGoals) {
        this.playerGoals = playerGoals;
    }
}
