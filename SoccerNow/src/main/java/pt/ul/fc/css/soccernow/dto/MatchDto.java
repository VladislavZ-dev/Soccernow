package pt.ul.fc.css.soccernow.dto;

public class MatchDto {
	private long id;
	private LineUpDto lineUp1Id;
	private LineUpDto lineUp2Id;
	private long refId;
	private long placeId;
	private long statsId;
	private long tournamentId;

	public MatchDto() {}
	
	public MatchDto(long id, LineUpDto lineUp1Id, LineUpDto lineUp2Id, long refId, long placeId, long statsId, long tournamnetId) {
		super();
		this.id = id;
		this.lineUp1Id = lineUp1Id;
		this.lineUp2Id = lineUp2Id;
		this.refId = refId;
		this.placeId = placeId;
		this.statsId = statsId;
		this.tournamentId = tournamnetId;
	}

	public long getId() { 
		return id;
		}
	public void setId(long id) { 
		this.id = id; 
		}

	public long getRefId() { 
		return refId; 
		}
	public void setRefId(long refId) { 
		this.refId = refId; 
		}

	public long getPlaceId() { 
		return placeId; 
		}
	public void setPlaceId(long placeId) { 
		this.placeId = placeId; 
		}

	public Long getStatsId() {
		return statsId; 
		}
	public void setStatsId(Long statsId) { 
		this.statsId = statsId; 
		}

	public LineUpDto getLineUp1Id() {
		return lineUp1Id;
	}

	public void setLineUp1Id(LineUpDto lineUp1Id) {
		this.lineUp1Id = lineUp1Id;
	}

	public LineUpDto getLineUp2Id() {
		return lineUp2Id;
	}

	public void setLineUp2Id(LineUpDto lineUp2Id) {
		this.lineUp2Id = lineUp2Id;
	}

    public long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(long tournamentId) {
        this.tournamentId = tournamentId;
    }
}
