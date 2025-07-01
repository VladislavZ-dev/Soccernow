package pt.ul.fc.css.soccernow.dto;

public class CardRequestDto {
    private Long matchId;
    private String playerName;
    private boolean isRedCard;

    public CardRequestDto() {}

    public CardRequestDto(Long matchId, String playerName, boolean isRedCard) {
        this.matchId = matchId;
        this.playerName = playerName;
        this.isRedCard = isRedCard;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isRedCard() {
        return isRedCard;
    }

    public void setRedCard(boolean redCard) {
        isRedCard = redCard;
    }
}
