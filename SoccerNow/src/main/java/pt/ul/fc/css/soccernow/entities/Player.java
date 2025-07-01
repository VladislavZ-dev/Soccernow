package pt.ul.fc.css.soccernow.entities;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
public class Player extends User {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Position position;

    @ManyToMany
    @JoinTable(name = "player_team",
               joinColumns = @JoinColumn(name = "playerId"),
               inverseJoinColumns = @JoinColumn(name = "teamId"))
    private List<Team> teams;

    public Player() {}

    public Player(String name, Position position) {
        super(name);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return getId() != null && getId().equals(player.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
