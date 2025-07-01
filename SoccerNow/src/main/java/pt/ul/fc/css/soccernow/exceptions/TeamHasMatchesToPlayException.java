package pt.ul.fc.css.soccernow.exceptions;

public class TeamHasMatchesToPlayException extends RuntimeException {

    public TeamHasMatchesToPlayException(String teamName) {
        super("The team with name " + teamName + " has still matches to play, can't be deleted");
    }
}
