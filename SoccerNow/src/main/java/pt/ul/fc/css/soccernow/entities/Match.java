package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Match {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "placeId")
    private Place place;

    @ManyToOne
    @JoinColumn(name = "lineup1Id")
    private LineUp lineUp1;

    @ManyToOne
    @JoinColumn(name = "lineup2Id")
    private LineUp lineUp2;

    @ManyToOne
    @JoinColumn(name = "refId")
    private Referee ref;

    @OneToOne
    private MatchStats stats;

	@ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

	public Match () {}

	public Match (Place place, LineUp LineUp1, LineUp LineUp2, Referee ref, MatchStats stats) {
		this.setPlace(place);
		this.setLineUp1(LineUp1);
		this.setLineUp2(LineUp2);
		this.setRef(ref);
		this.setStats(stats);
	}

	public void updateMatchResult() {
        if (stats != null && lineUp1 != null && lineUp2 != null) {
            int team1Score = stats.getTeam1Score();
            int team2Score = stats.getTeam2Score();
            
            if (team1Score > team2Score) {
                stats.setWinner(lineUp1.getTeam().getName());
            } else if (team2Score > team1Score) {
                stats.setWinner(lineUp2.getTeam().getName());
            } else {
                stats.setWinner("Draw");
            }
        }
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}


	public LineUp getLineUp1() {
		return this.lineUp1;
	}

	public void setLineUp1(LineUp LineUp1) {
		this.lineUp1 = LineUp1;
	}

	public LineUp getLineUp2() {
		return lineUp2;
	}

	public void setLineUp2(LineUp LineUp2) {
		this.lineUp2 = LineUp2;
	}

	public Referee getRef() {
		return ref;
	}

	public void setRef(Referee ref) {
		this.ref = ref;
	}


	public MatchStats getStats() {
		return stats;
	}

	public void setStats(MatchStats stats) {
		this.stats = stats;
	}

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

	public String getStadium(){
		return this.place.getStadium();
	}
}
