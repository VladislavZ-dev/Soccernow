package pt.ul.fc.css.javafx.dto;

public class PlayerCardDTO {
    private long matchId;
    private String playerName;
    private boolean redCard;

    public PlayerCardDTO() {}

    public PlayerCardDTO(long matchId, String playerName, boolean redCard) {
        this.matchId = matchId;
        this.playerName = playerName;
        this.redCard = redCard;
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isRedCard() {
        return redCard;
    }

    public void setRedCard(boolean redCard) {
        this.redCard = redCard;
    }
}
