package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.profile.Friendship;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.exceptions.NoContentException;
import be.kdg.runtracker.backend.exceptions.UnauthorizedUserException;
import be.kdg.runtracker.backend.exceptions.UserNotFoundException;
import be.kdg.runtracker.backend.services.api.FriendshipService;
import be.kdg.runtracker.backend.services.api.UserService;
import be.kdg.runtracker.frontend.dto.ShortUser;
import be.kdg.runtracker.frontend.util.CustomErrorType;
import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/users/")
public class UserRestController {

    private UserService userService;
    private FriendshipService friendshipService;

    @Autowired
    public UserRestController(UserService userService, FriendshipService friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    protected UserRestController() {
    }

    @RequestMapping(value = "/getUsers", method = RequestMethod.GET)
    public ResponseEntity<List<ShortUser>> getAllUsers(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null)
            throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch all Users!");

        List<User> users = userService.findAllUsers();
        if (users == null || users.isEmpty()) throw new NoContentException("No Users were found!");

        List<ShortUser> usersDTO = new ArrayList<>();
        users.stream().forEach(u -> usersDTO.add(new ShortUser(u)));

        return new ResponseEntity<List<ShortUser>>(usersDTO, HttpStatus.OK);
    }

    /**
     * Get {@link User} by its authId.
     *
     * @param token authorization id
     * @return User
     */

    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    public ResponseEntity<ShortUser> getUser(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UserNotFoundException("User with token " + token + " not found!");

        ShortUser userDTO = new ShortUser(user);

        return new ResponseEntity<ShortUser>(userDTO, HttpStatus.OK);
    }

    /**
     * Get specific user by username
     *
     * @param token
     * @param username
     * @return
     */
    @RequestMapping(value = "/getUser/{username}", method = RequestMethod.GET)
    public ResponseEntity<ShortUser> getFriend(@RequestHeader("token") String token, @PathVariable("username") String username) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null)
            throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch user!");


        User otherUser = userService.findUserByUsername(username);
        if (otherUser == null)
            throw new UnauthorizedUserException("User with username " + username + " not found, cannot fetch user!");


        return new ResponseEntity<ShortUser>(new ShortUser(otherUser), HttpStatus.OK);
    }

    /**
     * Create a {@link User}.
     *
     * @param user      User from body
     * @param ucBuilder Uri Builder
     * @return HTTP status
     */
    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestHeader("token") String token, @RequestBody User user, UriComponentsBuilder ucBuilder) {
        user.setAuthId(JWT.decode(token).getSubject());
        if (userService.findUserByAuthId(user.getAuthId()) != null) {
            return new ResponseEntity("A User with name " + user.getFirstname() + " " + user.getLastname() + " already exists!",
                    HttpStatus.CONFLICT
            );
        }
        userService.createUser(user);

        return new ResponseEntity<ShortUser>(new ShortUser(user), HttpStatus.CREATED);
    }

    /**
     * Update an existing {@link User}.
     *
     * @param token authorization id
     * @param user  User from body
     * @return HTTP status
     */
    @RequestMapping(value = "/updateUser", method = RequestMethod.PUT)
    public ResponseEntity<ShortUser> updateUser(@RequestHeader("token") String token, @RequestBody User user) {
        User currentUser = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (currentUser == null)
            throw new UserNotFoundException("User with token " + token + " not found, cannot update User!");

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
        currentUser.setCity(user.getCity());
        ;
        currentUser.setBirthday(user.getBirthday());
        currentUser.setAvatar(user.getAvatar());

        userService.saveUser(currentUser);

        ShortUser userDTO = new ShortUser(currentUser);

        return new ResponseEntity<ShortUser>(userDTO, HttpStatus.OK);
    }

    /**
     * Delete an existing {@link User}.
     *
     * @param token authorization id
     * @return HTTP status
     */
    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null)
            throw new UserNotFoundException("User with token " + token + " not found, cannot delete User!");

        this.userService.deleteUser(user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Check if username is available.
     *
     * @param token    authorization id
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

    /**
     * Check if username is online.
     *
     * @param token    authorization id
     * @param username username to check online
     * @return true if user is online
     */
    @RequestMapping(value = "/checkOnline/{username}", method = RequestMethod.GET)
    public ResponseEntity<?> checkOnline(@RequestHeader("token") String token, @PathVariable("username") String username) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found!");

        boolean online = this.userService.findUserByUsername(username).isOnline();

        return new ResponseEntity<Boolean>(online, HttpStatus.OK);
    }


    /**
     * @param token
     * @return {@link ShortUser}.
     */
    @RequestMapping(value = "/setOnline", method = RequestMethod.PUT)
    public ResponseEntity<?> setOnline(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found!");

        user.setOnline(true);
        userService.saveUser(user);

        return new ResponseEntity<ShortUser>(new ShortUser(user), HttpStatus.OK);
    }

    /**
     * @param token
     * @return {@link ShortUser}.
     */
    @RequestMapping(value = "/setOffline", method = RequestMethod.PUT)
    public ResponseEntity<?> setOffline(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found!");

        user.setOnline(false);
        userService.saveUser(user);

        return new ResponseEntity<ShortUser>(new ShortUser(user), HttpStatus.OK);
    }

    /**
     * Get all online users
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/getAllOnlineUsers", method = RequestMethod.GET)
    public ResponseEntity<?> getOnlineUsers(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found!");


        List<ShortUser> onlineUsers = new ArrayList<>();
        userService.findAllUsers().stream().filter(u -> u.isOnline()).forEach(uu -> onlineUsers.add(new ShortUser(uu)));


        return new ResponseEntity<List<ShortUser>>(onlineUsers, HttpStatus.OK);
    }

    /**
     * Get all users sorted
     *
     * @param token
     * @param sortoption
     * @return
     */

    @RequestMapping(value = "/getAllUsersSorted/{sortoption}", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsersSorted(@RequestHeader("token") String token, @PathVariable("sortoption") int sortoption) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found!");

        List<ShortUser> sortedUsers = new ArrayList<>();
        switch (sortoption) {
            //1. Order by avg distance
            case 1:
                sortedUsers = userService.findAllUsers()
                        .stream()
                        .sorted(Comparator.comparingDouble(User::getAvgDistance))
                        .map(ShortUser::new)
                        .collect(Collectors.toList());
                break;
            //2. Order by avg speed
            case 2:
                sortedUsers = userService.findAllUsers()
                        .stream()
                        .sorted(Comparator.comparingDouble(User::getAvgSpeed))
                        .map(ShortUser::new)
                        .collect(Collectors.toList());
                break;
            //3. Order by max distance
            case 3:
                sortedUsers = userService.findAllUsers()
                        .stream()
                        .sorted(Comparator.comparingLong(User::getMaxDistance))
                        .map(ShortUser::new)
                        .collect(Collectors.toList());
                break;
            //4. Order by max speed
            case 4:
                sortedUsers = userService.findAllUsers()
                        .stream()
                        .sorted(Comparator.comparingDouble(User::getMaxSpeed))
                        .map(ShortUser::new)
                        .collect(Collectors.toList());
                break;
            //5. Order by nr of competitions won
            case 5:
                sortedUsers = userService.findAllUsers()
                        .stream()
                        .sorted(Comparator.comparingInt(User::getNrOfCompetitionsWon))
                        .map(ShortUser::new)
                        .collect(Collectors.toList());
                break;
            //6. Order by nr of competitions done
            case 6:
                sortedUsers = userService.findAllUsers()
                        .stream()
                        .sorted(Comparator.comparingInt(User::getNrOfCompetitionsDone))
                        .map(ShortUser::new)
                        .collect(Collectors.toList());
                break;
            //7. Order by ratio done/won
            case 7:
                sortedUsers = userService.findAllUsers()
                        .stream()
                        .sorted(Comparator.comparingDouble(u -> (u.getNrOfCompetitionsDone() > 0 ? ((double) u.getNrOfCompetitionsWon()) / ((double) u.getNrOfCompetitionsDone()) : 0)))
                        .map(ShortUser::new)
                        .collect(Collectors.toList());
                break;
            //8. Order by total distance
            case 8:
                sortedUsers = userService.findAllUsers()
                        .stream()
                        .sorted(Comparator.comparingLong(User::getTotalDistance))
                        .map(ShortUser::new)
                        .collect(Collectors.toList());
                break;
        }
        if (sortedUsers.size() > 10)
            sortedUsers = sortedUsers.subList(sortedUsers.size() - 10, sortedUsers.size());
        Collections.reverse(sortedUsers);

        return new ResponseEntity<List<ShortUser>>(sortedUsers, HttpStatus.OK);
    }


    /**
     * set avatar of user
     *
     * @param token
     * @param avatarname
     * @return
     */
    @RequestMapping(value = "/setAvatar/{avatarname}", method = RequestMethod.PUT)
    public ResponseEntity<?> setAvatar(@RequestHeader("token") String token, @PathVariable("avatarname") String avatarname) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found!");

        user.setAvatar(avatarname);
        userService.saveUser(user);

        return new ResponseEntity<ShortUser>(new ShortUser(user), HttpStatus.OK);
    }

    /*
    FRIENDSHIP PART OF USERS
    *---------------------------------------
     */

    /**
     * Befriend another {@link User}.
     *
     * @param token    authorization id
     * @param username username of friend
     * @return HTTP status
     */
    @RequestMapping(value = "/addFriend/{username}", method = RequestMethod.PUT)
    public ResponseEntity<ShortUser> befriendUser(@RequestHeader("token") String token, @PathVariable("username") String username) {
        User currentUser = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (currentUser == null)
            throw new UnauthorizedUserException("User with token " + token + " not found, cannot add friend!");

        if (currentUser.getUsername().equals(username)) {
            return new ResponseEntity(new CustomErrorType("A User can't befriend itself!"),
                    HttpStatus.UNAUTHORIZED
            );
        }

        User friend = userService.findUserByUsername(username);
        if (friend == null)
            throw new UserNotFoundException("User with username " + username + " not found, cannot add friend!");

        friendshipService.addFriend(currentUser, friend);

        ShortUser userDTO = new ShortUser(currentUser);

        return new ResponseEntity<ShortUser>(userDTO, HttpStatus.OK);
    }


    /**
     * Defriend another {@link User}.
     *
     * @param token    authorization id
     * @param username username of friend
     * @return HTTP status
     */
    @RequestMapping(value = "/removeFriend/{username}", method = RequestMethod.DELETE)
    public ResponseEntity<ShortUser> defriendUser(@RequestHeader("token") String token, @PathVariable("username") String username) {
        User currentUser = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (currentUser == null)
            throw new UnauthorizedUserException("User with token " + token + " not found, cannot add friend!");

        User friend = userService.findUserByUsername(username);
        if (friend == null)
            throw new UserNotFoundException("User with username " + username + " not found, cannot add friend!");

        friendshipService.removeFriend(currentUser, friend);

        ShortUser userDTO = new ShortUser(currentUser);

        return new ResponseEntity<ShortUser>(userDTO, HttpStatus.OK);
    }


    /**
     * Get all friends
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/getAllFriends", method = RequestMethod.GET)
    public ResponseEntity<List<ShortUser>> getAllFriends(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null)
            throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch friends!");

        List<ShortUser> friends = new ArrayList<>();
        if (user.getFriendships() == null || user.getFriendships().isEmpty())
            throw new NoContentException("No friends found for User with id: " + user.getAuthId() + "!");


        for (Friendship friendship : user.getFriendships()) {
            if (friendshipService.checkFriendship(user, friendship.getFriend()))
                friends.add(new ShortUser(friendship.getFriend()));
        }

        return new ResponseEntity<List<ShortUser>>(friends, HttpStatus.OK);
    }

    /**
     * Get all friends sorted
     *
     * @param token
     * @param sortoption
     * @return
     */
    @RequestMapping(value = "/getAllFriendsSorted/{sortoption}", method = RequestMethod.GET)
    public ResponseEntity<?> getAllFriendsSorted(@RequestHeader("token") String token, @PathVariable("sortoption") int sortoption) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found!");

        List<ShortUser> friendsSorted = new ArrayList<>();
        List<ShortUser> friends = new ArrayList<>();
        friends.add(new ShortUser(user));
        for (Friendship friendship : user.getFriendships()) {
            if (friendshipService.checkFriendship(user, friendship.getFriend()))
                friends.add(new ShortUser(friendship.getFriend()));
        }

        switch (sortoption) {
            //1. Order by avg distance
            case 1:
                friendsSorted = friends
                        .stream()
                        .sorted(Comparator.comparingDouble(ShortUser::getAvgDistance))
                        .collect(Collectors.toList());
                break;
            //2. Order by avg speed
            case 2:
                friendsSorted = friends
                        .stream()
                        .sorted(Comparator.comparingDouble(ShortUser::getAvgSpeed))
                        .collect(Collectors.toList());
                break;
            //3. Order by max distance
            case 3:
                friendsSorted = friends
                        .stream()
                        .sorted(Comparator.comparingLong(ShortUser::getMaxDistance))
                        .collect(Collectors.toList());
                break;
            //4. Order by max speed
            case 4:
                friendsSorted = friends
                        .stream()
                        .sorted(Comparator.comparingDouble(ShortUser::getMaxSpeed))
                        .collect(Collectors.toList());
                break;
            //5. Order by nr of competitions won
            case 5:
                friendsSorted = friends
                        .stream()
                        .sorted(Comparator.comparingInt(ShortUser::getNrOfCompetitionsWon))
                        .collect(Collectors.toList());
                break;
            //6. Order by nr of competitions done
            case 6:
                friendsSorted = friends
                        .stream()
                        .sorted(Comparator.comparingInt(ShortUser::getNrOfCompetitionsDone))
                        .collect(Collectors.toList());
                break;
            //7. Order by ratio done/won
            case 7:
                friendsSorted = friends
                        .stream()
                        .sorted(Comparator.comparingDouble(u -> (u.getNrOfCompetitionsDone() > 0 ? ((double) u.getNrOfCompetitionsWon()) / ((double) u.getNrOfCompetitionsDone()) : 0)))
                        .collect(Collectors.toList());
                break;
            //8. Order by total distance
            case 8:
                friendsSorted = friends
                        .stream()
                        .sorted(Comparator.comparingLong(ShortUser::getTotalDistance))
                        .collect(Collectors.toList());
                break;
        }
        if (friendsSorted.size() > 10)
            friendsSorted = friendsSorted.subList(friendsSorted.size() - 10, friendsSorted.size());
        Collections.reverse(friendsSorted);

        return new ResponseEntity<List<ShortUser>>(friendsSorted, HttpStatus.OK);

    }

    /**
     * Get all online friends
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/getAllOnlineFriends", method = RequestMethod.GET)
    public ResponseEntity<?> getOnlineFriends(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found!");


        List<ShortUser> onlineFriends = new ArrayList<>();
        for (Friendship friendship : user.getFriendships()) {
            if (friendshipService.checkFriendship(user, friendship.getFriend()) && friendship.getFriend().isOnline())
                onlineFriends.add(new ShortUser(friendship.getFriend()));
        }


        return new ResponseEntity<List<ShortUser>>(onlineFriends, HttpStatus.OK);
    }


    /**
     * Accept a friendrequest
     *
     * @param token
     * @param username
     * @return
     */
    @RequestMapping(value = "/acceptFriend/{username}", method = RequestMethod.PUT)
    public ResponseEntity<?> acceptFriend(@RequestHeader("token") String token, @PathVariable("username") String username) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found!");


        User friend = userService.findUserByUsername(username);
        if (friend == null)
            throw new UserNotFoundException("User with username " + username + " not found, cannot add friend!");

        friendshipService.acceptFriend(user, friend);

        ShortUser currShortUser = new ShortUser(user);
        return new ResponseEntity<ShortUser>(currShortUser, HttpStatus.OK);
    }


    /**
     * Get all potential friends (users - own user - friends)
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/getAllPotentialFriends", method = RequestMethod.GET)
    public ResponseEntity<List<ShortUser>> getAllPotentialFriends(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null)
            throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch friends!");

        List<User> users = userService.findAllUsers();
        if (users == null || users.isEmpty()) throw new NoContentException("No Users were found!");

        List<User> friends = new ArrayList<>();
        user.getFriendships().forEach(f -> friends.add(f.getFriend()));
        friends.add(user);

        List<ShortUser> potentialFriends = new ArrayList<>();
        for (User potFriend : users) {
            if (!friends.contains(potFriend)) potentialFriends.add(new ShortUser(potFriend));
        }

        return new ResponseEntity<List<ShortUser>>(potentialFriends, HttpStatus.OK);
    }


    /**
     * get unaccepted {@link Friendship}.
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/getFriendrequests", method = RequestMethod.GET)
    public ResponseEntity<List<ShortUser>> getFriendrequests(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null)
            throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch friends!");

        List<ShortUser> friendRequests = new ArrayList<>();

        for (Friendship friendship : user.getFriendships()) {
            if (!friendship.isAccepted()) friendRequests.add(new ShortUser(friendship.getFriend()));
        }

        return new ResponseEntity<List<ShortUser>>(friendRequests, HttpStatus.OK);
    }


}
