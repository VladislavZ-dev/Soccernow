package pt.ul.fc.css.soccernow.exceptions;

public class PlayerAlreadyExistsException extends RuntimeException {

    public PlayerAlreadyExistsException(String name) {
        super("Player with name: " + name + " already exists");
    }
}
