package pt.ul.fc.css.soccernow.entities;

import java.util.*;

import jakarta.persistence.*;


@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(mappedBy = "history")
    private Team team;

    @ManyToMany
    @JoinTable(
        name = "history_matchstats", 
        joinColumns = @JoinColumn(name = "history_id"),
        inverseJoinColumns = @JoinColumn(name = "matchstats_id")
    )
    private List<MatchStats> playedMatches;

    @ElementCollection
    @CollectionTable(
        name = "history_tournament_placements",
        joinColumns = @JoinColumn(name = "history_id")
    )
    @MapKeyJoinColumn(name = "tournament_id")
    @Column(name = "placement")
    @Enumerated(EnumType.STRING)
    private Map<Tournament, Placement> tournamentPlacements;

    public History() {}

    public History(Team team) {
        this.team = team;
        playedMatches = new LinkedList<>();
        this.tournamentPlacements = new HashMap<>();

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

    public List<MatchStats> getPlayedMatches() {
        return playedMatches;
    }

    public void setPlayedMatches(List<MatchStats> playedMatches) {
        this.playedMatches = playedMatches;
    }

    public boolean addPlayedMatch(MatchStats playedMatch) {
        return playedMatches.add(playedMatch);
    }

    public Map getTournamentPlacements() {
        return tournamentPlacements;
    }

    public void setTournamentPlacements(Map tournamentPlacements) {
        this.tournamentPlacements = tournamentPlacements;
    }

    // Add this method to your History class
    public void removePlayedMatch(MatchStats matchStats) {
        if (this.playedMatches != null) {
            this.playedMatches.remove(matchStats);
        }
    }
}
