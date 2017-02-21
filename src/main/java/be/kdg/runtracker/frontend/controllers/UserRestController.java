package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.exceptions.NoContentException;
import be.kdg.runtracker.backend.exceptions.UnauthorizedUserException;
import be.kdg.runtracker.backend.exceptions.UserNotFoundException;
import be.kdg.runtracker.backend.services.api.UserService;
import be.kdg.runtracker.frontend.dto.ShortUser;
import be.kdg.runtracker.frontend.util.CustomErrorType;
import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/users/")
public class UserRestController {

    private UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    protected UserRestController() { }

    @RequestMapping(value = "/getUsers",method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch all Users!");

        List<User> users = userService.findAllUsers();
        if (users == null || users.isEmpty()) throw new NoContentException("No Users were found!");

        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }

    /**
     * Get {@link User} by its authId.
     * @param token authorization id
     * @return User
     */

    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UserNotFoundException("User with token " + token + " not found!");

        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    /**
     * Create a {@link User}.
     * @param user User from body
     * @param ucBuilder Uri Builder
     * @return HTTP status
     */
    @RequestMapping(value = "/createUser",method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestHeader("token") String token,@RequestBody User user, UriComponentsBuilder ucBuilder) {
        user.setAuthId(JWT.decode(token).getSubject());
        if (userService.findUserByAuthId(user.getAuthId()) != null) {
            return new ResponseEntity("A User with name " + user.getFirstname() + " " + user.getLastname() + " already exists!",
                    HttpStatus.CONFLICT
            );
        }
        userService.saveUser(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/getuser").buildAndExpand(token).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * Update an existing {@link User}.
     * @param token authorization id
     * @param user User from body
     * @return HTTP status
     */
    @RequestMapping(value = "/updateUser", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@RequestHeader("token") String token, @RequestBody User user) {
        User currentUser = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (currentUser == null) throw new UserNotFoundException("User with token " + token + " not found, cannot update User!");

        if (!user.getUsername().equals(currentUser.getUsername())) {
            if (this.userService.findUserByUsername(user.getUsername()) != null) {
                return new ResponseEntity(new CustomErrorType("Could not update User! Username " + user.getUsername() + " already exists!"),
                        HttpStatus.UNAUTHORIZED
                );
            }
        }

        currentUser.setUsername(user.getUsername());
        currentUser.setFirstname(user.getFirstname());
        currentUser.setLastname(user.getLastname());
        currentUser.setGender(user.getGender());
        currentUser.setCity(user.getCity());;
        currentUser.setBirthday(user.getBirthday());

        userService.saveUser(currentUser);
        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }

    /**
     * Delete an existing {@link User}.
     * @param token authorization id
     * @return HTTP status
     */
    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UserNotFoundException("User with token " + token + " not found, cannot delete User!");

        this.userService.deleteUser(user);

        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

    /**
     * Befriend another {@link User}.
     * @param token authorization id
     * @param username username of friend
     * @return HTTP status
     */
    @RequestMapping(value = "/addFriend/{username}", method = RequestMethod.PUT)
    public ResponseEntity<?> befriendUser(@RequestHeader("token") String token, @PathVariable("username") String username) {
        User currentUser = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (currentUser == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot add friend!");

        if (currentUser.getUsername().equals(username)) {
            return new ResponseEntity(new CustomErrorType("A User can't befriend itself!"),
                    HttpStatus.UNAUTHORIZED
            );
        }

        User friend = userService.findUserByUsername(username);
        if (friend == null) throw new UserNotFoundException("User with username " + username + " not found, cannot add friend!");

        currentUser.addFriend(friend);
        friend.addFriend(currentUser);

        userService.saveUser(currentUser);
        userService.saveUser(friend);
        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }


    /**
     *Defriend another {@link User}.
     * @param token authorization id
     * @param username username of friend
     * @return HTTP status
     */
    @RequestMapping(value = "/removeFriend/{username}", method = RequestMethod.DELETE)
    public ResponseEntity<?> defriendUser(@RequestHeader("token") String token, @PathVariable("username") String username) {
        User currentUser = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (currentUser == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot add friend!");

        User friend = userService.findUserByUsername(username);
        if (friend == null) throw new UserNotFoundException("User with username " + username + " not found, cannot add friend!");


        currentUser.getFriends().remove(friend);
        friend.getFriends().remove(currentUser);

        userService.saveUser(currentUser);
        userService.saveUser(friend);
        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }

    /**
     * Check if username is available.
     * @param token authorization id
     * @param username username to check
     * @return true if username is available, otherwise false
     */
    @RequestMapping(value = "/checkUsername/{username}", method = RequestMethod.GET)
    public ResponseEntity<?> checkUsernameAvailability(@RequestHeader("token") String token, @PathVariable("username") String username) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found!");

        boolean available = false;

        if (this.userService.findUserByUsername(username) == null || this.userService.findUserByAuthId(JWT.decode(token).getSubject()).getUsername().equals(username))
            available = true;

        return new ResponseEntity<Boolean>(available, HttpStatus.OK);
    }

    @RequestMapping(value = "/getAllFriends", method = RequestMethod.GET)
    public ResponseEntity<List<ShortUser>> getAllFriends(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch friends!");

        List<ShortUser> friends = new ArrayList<>();
        if (user.getFriends() == null || user.getFriends().isEmpty())
            throw new NoContentException("No friends found for User with id: " + user.getAuthId() + "!");

        for (User friend : user.getFriends()) {
            friends.add(new ShortUser(friend));
        }

        return new ResponseEntity<List<ShortUser>>(friends, HttpStatus.OK);
    }

}
