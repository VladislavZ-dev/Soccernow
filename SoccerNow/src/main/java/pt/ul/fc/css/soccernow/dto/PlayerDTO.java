package pt.ul.fc.css.soccernow.dto;
import java.util.List;

import pt.ul.fc.css.soccernow.entities.Position;

public class PlayerDTO extends UserDTO{
    private Position position;
    private List<String> teams;

    public PlayerDTO() {}

    public PlayerDTO(String name, Long id, Position position, List<String> teams) {
        super(name, id);
        this.position = position;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<String> getTeams() {
        return teams;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
    }

    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof PlayerDTO) {
            return super.equals(obj) && position == ((PlayerDTO) obj).position;
        }
    	return false;
    }
    
    @Override
    public int hashCode() {
    	return super.hashCode() + position.hashCode();
    }
}
