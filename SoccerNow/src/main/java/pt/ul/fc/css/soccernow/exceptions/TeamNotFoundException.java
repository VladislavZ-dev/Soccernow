package pt.ul.fc.css.soccernow.exceptions;

public class TeamNotFoundException extends RuntimeException {

    public TeamNotFoundException(String teamName) {
        super("The team with name " + teamName + " has not been found");
    }

    public TeamNotFoundException() {
        super("Team has not been found");
    }
}
