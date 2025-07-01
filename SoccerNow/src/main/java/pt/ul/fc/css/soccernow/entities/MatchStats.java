package pt.ul.fc.css.soccernow.entities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class MatchStats {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "matchId")
    private Match match;

    private int team1Score;
    private int team2Score;
    private String winner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "playerWithRedCards",
        joinColumns = @JoinColumn(name = "matchId"),
        inverseJoinColumns = @JoinColumn(name = "playerId"))
    private List<Player> redCards;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "playerWithYellowCards",
        joinColumns = @JoinColumn(name = "matchId"),
        inverseJoinColumns = @JoinColumn(name = "playerId"))
    private List<Player> yellowCards;

    @ElementCollection
    @CollectionTable(
        name = "player_goals",
        joinColumns = @JoinColumn(name = "match_stats_id")
    )
    @MapKeyJoinColumn(name = "playerId")
    @Column(name = "goals")
    private Map<Player, Integer> playerGoals;

	public MatchStats(){
        this.setTeam1Score(0);
        this.setTeam2Score(0);
		this.redCards = new ArrayList<>();
        this.yellowCards = new ArrayList<>();
		this.playerGoals = new HashMap<>();
	}

	public MatchStats(Match match, int team1Score, int team2Score, String winner, List<Player> redCards, List<Player> yellowCards, Map<Player, Integer> playerGoals) {

		this.setMatch(match);
		this.setTeam1Score(team1Score);
		this.setTeam2Score(team2Score);
		this.setWinner(winner);
		this.redCards = new ArrayList<>();
        this.yellowCards = new ArrayList<>();
		this.playerGoals = new HashMap<>();
	}

	public void updateTeamScores() {
        this.team1Score = calculateTeamScore(match.getLineUp1());
        this.team2Score = calculateTeamScore(match.getLineUp2());
        this.match.updateMatchResult();
    }

    private int calculateTeamScore(LineUp lineup) {
        if (lineup == null) return 0;
        return playerGoals.entrySet().stream()
            .filter(entry -> isPlayerInLineup(entry.getKey(), lineup))
            .mapToInt(Map.Entry::getValue)
            .sum();
    }

    private boolean isPlayerInLineup(Player player, LineUp lineup) {
        return player.equals(lineup.getCaptain()) ||
               player.equals(lineup.getPivot()) ||
               player.equals(lineup.getLeftWinger()) ||
               player.equals(lineup.getRightWinger()) ||
               player.equals(lineup.getDefender()) ||
               player.equals(lineup.getGoalkeeper());
    }

	public void addPlayerGoals(Player player, int goals) {
        if (goals <= 0) throw new IllegalArgumentException("Goals must be positive");
        playerGoals.merge(player, goals, Integer::sum);
    }

    public int getTeam1Score() {
        return match != null ? calculateTeamScore(match.getLineUp1()) : 0;
    }

    public int getTeam2Score() {
        return match != null ? calculateTeamScore(match.getLineUp2()) : 0;
    }

	public void setTeam1Score(int team1Score) {
        this.team1Score = team1Score;
    }

    public void setTeam2Score(int team2Score) {
        this.team2Score = team2Score;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<Player> getRedCards() {
        return redCards;
    }

    public void setRedCards(List<Player> redCards) {
        this.redCards = redCards;
    }

    public List<Player> getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(List<Player> yellowCards) {
        this.yellowCards = yellowCards;
    }

    public Map<Player, Integer> getPlayerGoals() {
        return playerGoals;
    }

    public void setPlayerGoals(Map<Player, Integer> playerGoals) {
        this.playerGoals = playerGoals;
    }

}
