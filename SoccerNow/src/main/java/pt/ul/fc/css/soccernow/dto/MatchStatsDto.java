package pt.ul.fc.css.soccernow.dto;

import java.util.List;

public class MatchStatsDto {

	private long id;

	private long matchId;

	private int team1Score;

	private int team2Score;

	private String winner;

	private List<String> redCards;


	private List<String> yellowCards;

	public MatchStatsDto(){}

	public MatchStatsDto(long id, long matchId, int team1Score, int team2Score, String winner, List<String> redCards, List<String> yellowCards ) {
		this.setId(id);
		this.setMatchId(matchId);
		this.setTeam1Score(team1Score);
		this.setTeam2Score(team2Score);
		this.setWinner(winner);
		this.setRedCards(redCards);
		this.setYellowCards(yellowCards);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMatchId() {
		return matchId;
	}

	public void setMatchId(long matchId) {
		this.matchId = matchId;
	}

	public int getTeam1Score() {
		return team1Score;
	}

	public void setTeam1Score(int team1Score) {
		this.team1Score = team1Score;
	}

	public int getTeam2Score() {
		return team2Score;
	}

	public void setTeam2Score(int team2Score) {
		this.team2Score = team2Score;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public List<String> getRedCards() {
		return redCards;
	}

	public void setRedCards(List<String> redCards) {
		this.redCards = redCards;
	}

	public List<String> getYellowCards() {
		return yellowCards;
	}

	public void setYellowCards(List<String> yellowCards) {
		this.yellowCards = yellowCards;
	}

}
