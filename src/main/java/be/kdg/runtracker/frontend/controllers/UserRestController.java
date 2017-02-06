package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.services.UserService;
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
    private UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    protected UserRestController() { }

    /**
     * Get all {@link User}s.
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<User>> listAllUsers() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }

    /**
     * Get {@link User} by its userId.
     * @param userId
     * @return
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable("userId") long userId) {
        logger.info("Fetching User with userId " + userId + ".");

        User user = userService.findUserByUserId(userId);
        if (user == null) {
            logger.error("User with userId " + userId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with userId " + userId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    /**
     * Create a {@link User}.
     * @param user
     * @param ucBuilder
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        logger.info("Creating User: " + user + ".");

        if (userService.findUser(user) != null) {
            logger.error("A User with name " + user.getFirstname() + " " + user.getLastname() + " already exists!");
            return new ResponseEntity("A User with name " + user.getFirstname() + " " + user.getLastname() + " already exists!",
                    HttpStatus.CONFLICT
            );
        }
        userService.saveUser(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/{userId}").buildAndExpand(user.getUser_id()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * Update an existing {@link User}.
     * @param userId
     * @param user
     * @return
     */
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable("userId") long userId, @RequestBody User user) {
        logger.info("Updating User with userId: " + userId + ".");

        User currentUser = userService.findUserByUserId(userId);

        if (currentUser == null) {
            logger.error("User with userId: " + userId + " not found!");
            return new ResponseEntity(new CustomErrorType("User with userId: " + userId + " not found!"),
                    HttpStatus.NOT_FOUND
            );
        }

        currentUser.setUsername(user.getUsername());
        currentUser.setFirstname(user.getFirstname());
        currentUser.setLastname(user.getLastname());
        currentUser.setGender(user.getGender());
        currentUser.setCity(user.getCity());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhone(user.getPhone());
        currentUser.setPictureURL(user.getPictureURL());
        currentUser.setBirthday(user.getBirthday());
        currentUser.setWeight(user.getWeight());
        currentUser.setPlength(user.getPlength());

        userService.saveUser(currentUser);
        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }

    /**
     * Delete an existing {@link User}.
     * @param userId
     * @return
     */
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("userId") long userId) {
        logger.info("Fetching & Deleting User with id: " + userId + ".");

        User user = userService.findUserByUserId(userId);
        if (user == null) {
            logger.error("User with id " + userId + " does not exist!");
            return new ResponseEntity(new CustomErrorType("User with id " + userId + " does not exist!"),
                    HttpStatus.NOT_FOUND
            );
        }
        userService.deleteUserById(userId);
        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

}
