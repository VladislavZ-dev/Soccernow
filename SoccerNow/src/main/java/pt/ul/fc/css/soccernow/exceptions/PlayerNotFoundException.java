package pt.ul.fc.css.soccernow.exceptions;

public class PlayerNotFoundException extends RuntimeException {
    
    public PlayerNotFoundException(Long id) {
        super("Player with ID: " + id + " not found");
    }

    public PlayerNotFoundException(String name) {
        super("Player with name: " + name + " not found");
    }
}