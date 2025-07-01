package pt.ul.fc.css.soccernow.handlers;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ul.fc.css.soccernow.dto.UserDTO;
import pt.ul.fc.css.soccernow.entities.User;
import pt.ul.fc.css.soccernow.repositories.UserRepository;

@Service
public class UserHandler {
	@Autowired
    private UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToDto)
                .toList();
    }

    public UserDTO saveUser(User user) {
        return mapToDto(userRepository.save(user));
    }

    public UserDTO getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return null;
        }
        return mapToDto(userOptional.get());
    }

    public UserDTO mapToDto(User user) {
        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        return userDto;
    }

    public UserDTO createUser(UserDTO userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());

        User savedUser = userRepository.save(user);

        userDto.setId(savedUser.getId());
        
        return userDto;
    }

}
