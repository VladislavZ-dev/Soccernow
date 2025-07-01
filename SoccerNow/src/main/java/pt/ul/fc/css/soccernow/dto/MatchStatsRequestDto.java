package pt.ul.fc.css.soccernow.dto;

import pt.ul.fc.css.soccernow.entities.MatchStats;

public class MatchStatsRequestDto {
    private long matchId;
    private MatchStats stats;
    
	public long getMatchId() {
		return matchId;
	}
	public void setMatchId(long matchId) {
		this.matchId = matchId;
	}
	public MatchStats getStats() {
		return stats;
	}
	public void setStats(MatchStats stats) {
		this.stats = stats;
	}

    
}