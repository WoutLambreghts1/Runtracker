package be.kdg.runtracker.backend.services.api;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.frontend.dto.ShortUser;

import java.util.List;

public interface UserService {

    List<User> findAllUsers();

    User findUserByAuthId(String authId);

    User findUserByUsername(String username);

    void saveUser(User user);

    void deleteUser(User user);

    ShortUser createUser(User user);

}
