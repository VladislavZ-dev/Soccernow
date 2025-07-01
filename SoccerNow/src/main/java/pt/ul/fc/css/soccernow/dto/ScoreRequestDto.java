package pt.ul.fc.css.soccernow.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreRequestDto {
    private Long matchId;
    private List<PlayerGoalDto> playerGoals;

    public ScoreRequestDto() {}

    public ScoreRequestDto(Long matchId, Map<Long, Integer> playerGoalsMap) {
        this.matchId = matchId;
        this.playerGoals = playerGoalsMap.entrySet().stream()
            .map(entry -> new PlayerGoalDto(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
    
    // Getters and setters
    public Long getMatchId() {
        return matchId;
    }
    
    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
    
    public List<PlayerGoalDto> getPlayerGoals() {
        return playerGoals;
    }
    
    public void setPlayerGoals(List<PlayerGoalDto> playerGoals) {
        this.playerGoals = playerGoals;
    }
    
    // Helper method to convert back to Map for your existing service logic
    @JsonIgnore
    public Map<Long, Integer> getPlayerGoalsAsMap() {
        if (playerGoals == null) {
            return Map.of();
        }
        return playerGoals.stream()
            .collect(Collectors.toMap(
                PlayerGoalDto::getPlayerId, 
                PlayerGoalDto::getGoals
            ));
    }
    
    // Inner class for better Swagger documentation
    public static class PlayerGoalDto {
        private Long playerId;
        private Integer goals;
        
        public PlayerGoalDto() {}
        
        public PlayerGoalDto(Long playerId, Integer goals) {
            this.playerId = playerId;
            this.goals = goals;
        }
        
        public Long getPlayerId() {
            return playerId;
        }
        
        public void setPlayerId(Long playerId) {
            this.playerId = playerId;
        }
        
        public Integer getGoals() {
            return goals;
        }
        
        public void setGoals(Integer goals) {
            this.goals = goals;
        }
    }
}