package pt.ul.fc.css.soccernow.entities;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class LineUp {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private Player captain;

    @ManyToOne
    @JoinColumn(name = "goalkeeper_id")
    private Player goalkeeper;

    @ManyToOne
    @JoinColumn(name = "defender_id")
    private Player defender;

    @ManyToOne
    @JoinColumn(name = "rightWinger_id")
    private Player rightWinger;

    @ManyToOne
    @JoinColumn(name = "leftWinger_id")
    private Player leftWinger;

    @ManyToOne
    @JoinColumn(name = "pivot_id")
    private Player pivot;

    public LineUp() {}

    public LineUp(Team team, Match match) {
        this.team = team;
        this.match = match;
    }

    public boolean lineUpIsComplete() {
        return captain != null && goalkeeper != null && defender != null && rightWinger != null && leftWinger != null && pivot != null;
    }

    public boolean containsPlayer(Player player) {
        return player.equals(captain) ||
            player.equals(goalkeeper) ||
            player.equals(defender) ||
            player.equals(rightWinger) ||
            player.equals(leftWinger) ||
            player.equals(pivot);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Player getCaptain() {
        return captain;
    }

    public void setCaptain(Player captain) {
        this.captain = captain;
    }

    public Player getGoalkeeper() {
        return goalkeeper;
    }

    public void setGoalkeeper(Player goalkeeper) {
        this.goalkeeper = goalkeeper;
    }

    public Player getDefender() {
        return defender;
    }

    public void setDefender(Player defender) {
        this.defender = defender;
    }

    public Player getRightWinger() {
        return rightWinger;
    }

    public void setRightWinger(Player rightWinger) {
        this.rightWinger = rightWinger;
    }

    public Player getLeftWinger() {
        return leftWinger;
    }

    public void setLeftWinger(Player leftWinger) {
        this.leftWinger = leftWinger;
    }

    public Player getPivot() {
        return pivot;
    }

    public void setPivot(Player pivot) {
        this.pivot = pivot;
    }

    public List<Player> getPlayers() {
        List<Player> players = new LinkedList<>();
        players.add(goalkeeper);
        players.add(defender);
        players.add(rightWinger);
        players.add(leftWinger);
        players.add(pivot);
        return players;
    }

}
