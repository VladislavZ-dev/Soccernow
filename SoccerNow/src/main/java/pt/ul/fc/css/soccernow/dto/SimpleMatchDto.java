package pt.ul.fc.css.soccernow.dto;

public class SimpleMatchDto {
    private long placeId;
    private long refereeId;
    private long lineUp1Id;
    private long lineUp2Id;

    public SimpleMatchDto() {}

    public long getPlaceId() {
        return placeId; 
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public long getRefereeId() {
        return refereeId;
    }

    public void setRefereeId(long refereeId) {
        this.refereeId = refereeId;
    }

    public long getLineUp1Id() {
        return lineUp1Id;
    }

    public void setLineUp1Id(long lineUp1Id) {
        this.lineUp1Id = lineUp1Id;
    }

    public long getLineUp2Id() {
        return lineUp2Id;
    }

    public void setLineUp2Id(long lineUp2Id) {
        this.lineUp2Id = lineUp2Id;
    }
}