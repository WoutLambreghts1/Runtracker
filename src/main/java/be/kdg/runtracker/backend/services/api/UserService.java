package be.kdg.runtracker.backend.services.api;

import be.kdg.runtracker.backend.dom.profile.User;

import java.util.List;

public interface UserService {

    List<User> findAllUsers();

    User findUserByAuthId(long authId);

    User findUserByUsername(String username);

    void saveUser(User user);

    void deleteUser(long userId);

}
