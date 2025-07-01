package pt.ul.fc.css.soccernow.exceptions;

public class TeamAlreadyExistsException extends RuntimeException{

    public TeamAlreadyExistsException(String teamName) {
        super("The team with name " + teamName + " already exists");
    }
}
