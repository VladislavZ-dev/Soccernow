package pt.ul.fc.css.javafx.dto;
import java.util.List;

public class PlayerDTO extends UserDTO{
    private String position;
    private List<String> teams;

    public PlayerDTO() {}

    public PlayerDTO(String name, Long id, String position, List<String> teams) {
        super(name, id);
        this.position = position;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
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
