package pt.ul.fc.css.soccernow.exceptions;

public class RefereeNotFoundException extends RuntimeException {

    public RefereeNotFoundException(Long id) {
        super("Referee with ID: " + id + " not found");
    }

    public RefereeNotFoundException(String name) {
        super("Referee with name: " + name + " not found");
    }
}
