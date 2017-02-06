package be.kdg.runtracker.backend.services;

import be.kdg.runtracker.backend.dom.profile.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Wout on 3/02/2017.
 */
@Service
public interface UserService {

    List<User> findAll();

    User findUserByUserId(long userId);

    User findUserByUsername(String userId);

    User findUser(User user);

    void saveUser(User user);

    void deleteUserById(long userId);

}
