package pt.ul.fc.css.javafx.dto;

public class UserDTO {
    private String name;
    private Long id;

    public UserDTO() {}

    public UserDTO(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final UserDTO other = (UserDTO) obj;

        return other.getId() == this.getId();
    }

    @Override
    public int hashCode() {
    	return this.name.hashCode() + id.hashCode();
    }
}
