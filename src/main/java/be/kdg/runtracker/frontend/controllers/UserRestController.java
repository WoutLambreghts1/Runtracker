package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.persistence.UserRepository;
import be.kdg.runtracker.frontend.util.CustomErrorType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RepositoryRestController
@RequestMapping("/api/users")
public class UserRestController {

    public static final Logger logger = Logger.getLogger(UserRestController.class);
    private UserRepository userRepository;

    @Autowired
    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected UserRestController() { }

    /**
     * Get all {@link User}s.
     * @return List of Users
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }

    /**
     * Get {@link User} by its authId.
     * @param authId authorization id
     * @return User
     */
    @RequestMapping(value = "/{authId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable("authId") long authId) {
        logger.info("Fetching User with authId " + authId + ".");

        User user = userRepository.findUserByAuthId(authId);
        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    /**
     * Create a {@link User}.
     * @param user User from body
     * @param ucBuilder Uri Builder
     * @return HTTP status
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        logger.info("Creating User: " + user + ".");

        if (userRepository.findUserByAuthId(user.getAuthId()) != null) {
            logger.error("A User with name " + user.getFirstname() + " " + user.getLastname() + " already exists!");
            return new ResponseEntity("A User with name " + user.getFirstname() + " " + user.getLastname() + " already exists!",
                    HttpStatus.CONFLICT
            );
        }
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/{authId}").buildAndExpand(user.getUser_id()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * Update an existing {@link User}.
     * @param authId authorization id
     * @param user User from body
     * @return HTTP status
     */
    @RequestMapping(value = "/{authId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable("authId") long authId, @RequestBody User user) {
        logger.info("Updating User with authId: " + authId + ".");

        User currentUser = userRepository.findUserByAuthId(authId);

        if (currentUser == null) {
            logger.error("User with authId: " + authId + " not found!");
            return new ResponseEntity(new CustomErrorType("User with authId: " + authId + " not found!"),
                    HttpStatus.NOT_FOUND
            );
        }

        currentUser.setUsername(user.getUsername());
        currentUser.setFirstname(user.getFirstname());
        currentUser.setLastname(user.getLastname());
        currentUser.setGender(user.getGender());
        currentUser.setCity(user.getCity());;
        currentUser.setBirthday(user.getBirthday());
        currentUser.setWeight(user.getWeight());
        currentUser.setPlength(user.getPlength());

        userRepository.save(currentUser);
        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }

    /**
     * Delete an existing {@link User}.
     * @param authId authorization id
     * @return HTTP status
     */
    @RequestMapping(value = "/{authId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("authId") long authId) {
        logger.info("Fetching & Deleting User with id: " + authId + ".");

        User user = userRepository.findUserByAuthId(authId);
        if (user == null) {
            logger.error("User with id " + authId + " does not exist!");
            return new ResponseEntity(new CustomErrorType("User with id " + authId + " does not exist!"),
                    HttpStatus.NOT_FOUND
            );
        }
        userRepository.delete(user.getUser_id());
        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

}
