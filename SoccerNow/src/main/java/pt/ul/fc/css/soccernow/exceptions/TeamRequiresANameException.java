package pt.ul.fc.css.soccernow.exceptions;

public class TeamRequiresANameException extends RuntimeException {

    public TeamRequiresANameException() {
        super("A team must have a name");
    }

}
