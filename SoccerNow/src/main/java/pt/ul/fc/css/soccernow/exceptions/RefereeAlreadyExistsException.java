package pt.ul.fc.css.soccernow.exceptions;

public class RefereeAlreadyExistsException extends RuntimeException {
    
    public RefereeAlreadyExistsException(String name) {
        super("Referee with name: " + name + " already exists");
    }
}
