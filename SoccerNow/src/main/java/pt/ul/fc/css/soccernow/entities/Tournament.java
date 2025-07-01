package pt.ul.fc.css.soccernow.entities;

import java.util.*;
import jakarta.persistence.*;

@Entity
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private TournamentType type;

    @ManyToMany
    @JoinTable(
        name = "tournament_teams",
        joinColumns = @JoinColumn(name = "tournament_id"),
        inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<Team> teams;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
        name = "tournament_scheduled_matches",
        joinColumns = @JoinColumn(name = "tournament_id"),
        inverseJoinColumns = @JoinColumn(name = "match_id")
    )
    private List<Match> scheduledMatches;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
        name = "tournament_played_matches",
        joinColumns = @JoinColumn(name = "tournament_id"),
        inverseJoinColumns = @JoinColumn(name = "match_id")
    )
    private List<Match> playedMatches;
    
    public Tournament() {
        this.teams = new ArrayList<>();
        this.scheduledMatches = new ArrayList<>();
        this.playedMatches = new ArrayList<>();
    }

    public Tournament(String name, TournamentType type, List<Match> playedMatches, List<Match> scheduledMatches, List<Team> teams) {
        this.name = name;
        this.type = type;
        this.playedMatches = playedMatches != null ? playedMatches : new ArrayList<>();
        this.scheduledMatches = scheduledMatches != null ? scheduledMatches : new ArrayList<>();
        this.teams = teams != null ? teams : new ArrayList<>();
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

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Match> getScheduledMatches() {
        return scheduledMatches;
    }

    public void setScheduledMatches(List<Match> scheduledMatches) {
        this.scheduledMatches = scheduledMatches;
    }

    public List<Match> getPlayedMatches() {
        return playedMatches;
    }

    public void setPlayedMatches(List<Match> playedMatches) {
        this.playedMatches = playedMatches;
    }

    public boolean addTeam(Team team) {
        return teams.add(team);
    }

    public boolean removeTeam(Team team) {
        return teams.remove(team);
    }

    public boolean addScheduledMatch(Match match) {
        return scheduledMatches.add(match);
    }

    public boolean addPlayedMatch(Match match) {
        return playedMatches.add(match);
    }

    public TournamentType getType() {
        return type;
    }

    public void setType(TournamentType type) {
        this.type = type;
    }
}