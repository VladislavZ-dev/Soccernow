package pt.ul.fc.css.soccernow.dto;

import java.util.ArrayList;
import java.util.List;

public class UserCatalog {
    private List<UserDTO> userCatalog;

    public UserCatalog() {
        this.userCatalog = new ArrayList<>();
    }

    public boolean userExists(UserDTO user) {
        return userCatalog.contains(user);
    }

    public UserDTO getUserById(int id) {
        for (UserDTO user : this.userCatalog) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public void addUser(UserDTO user) {
        this.userCatalog.add(user);
    }

    public void removeUser(UserDTO user) {
        this.userCatalog.remove(user);
    }
}