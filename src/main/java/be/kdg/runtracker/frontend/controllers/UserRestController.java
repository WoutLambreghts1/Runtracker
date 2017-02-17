package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.exceptions.NoContentException;
import be.kdg.runtracker.backend.exceptions.UnauthorizedUserException;
import be.kdg.runtracker.backend.exceptions.UserNotFoundException;
import be.kdg.runtracker.backend.persistence.api.CompetitionRepository;
import be.kdg.runtracker.backend.persistence.api.GoalRepository;
import be.kdg.runtracker.backend.persistence.api.TrackingRepository;
import be.kdg.runtracker.backend.persistence.api.UserRepository;
import be.kdg.runtracker.frontend.util.CustomErrorType;
import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RepositoryRestController
@CrossOrigin
@RequestMapping("/users/")
public class UserRestController {

    private UserRepository userRepository;
    private TrackingRepository trackingRepository;
    private CompetitionRepository competitionRepository;
    private GoalRepository goalRepository;

    @Autowired
    public UserRestController(UserRepository userRepository, TrackingRepository trackingRepository, CompetitionRepository competitionRepository, GoalRepository goalRepository) {
        this.userRepository = userRepository;
        this.trackingRepository = trackingRepository;
        this.competitionRepository = competitionRepository;
        this.goalRepository = goalRepository;
    }

    protected UserRestController() { }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers(@RequestHeader("token") String token) {
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch all Users!");

        List<User> users = userRepository.findAll();
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
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UserNotFoundException("User with token " + token + " not found!");

        updateUserPrestations(JWT.decode(token).getSubject());

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
        if (userRepository.findUserByAuthId(user.getAuthId()) != null) {
            return new ResponseEntity("A User with name " + user.getFirstname() + " " + user.getLastname() + " already exists!",
                    HttpStatus.CONFLICT
            );
        }
        userRepository.save(user);

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
        User currentUser = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (currentUser == null) throw new UserNotFoundException("User with token " + token + " not found, cannot update User!");

        if (!user.getUsername().equals(currentUser.getUsername())) {
            if (this.userRepository.findUserByUsername(user.getUsername()) != null) {
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

        userRepository.save(currentUser);
        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }

    /**
     * Delete an existing {@link User}.
     * @param token authorization id
     * @return HTTP status
     */
    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@RequestHeader("token") String token) {
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UserNotFoundException("User with token " + token + " not found, cannot delete User!");

        List<Competition> competitionsCreated = user.getCompetitionsCreated();
        List<Tracking> trackings = user.getTrackings();

        user.setCompetitionsRun(new ArrayList<>());
        user.setCompetitionsWon(new ArrayList<>());
        user.setCompetitionsCreated(new ArrayList<>());
        user.setTrackings(new ArrayList<>());
        this.userRepository.save(user);

        if (trackings != null && !trackings.isEmpty()) this.trackingRepository.delete(trackings);

        if (competitionsCreated != null && !competitionsCreated.isEmpty()) {
            List<Goal> goals = new ArrayList<>();
            for (Competition competition : competitionsCreated) {
                competition.setUserCreated(null);
                competition.setUsersRun(null);
                competition.setUserWon(null);
                goals.add(competition.getGoal());
                this.competitionRepository.save(competition);
                this.competitionRepository.delete(competition.getCompetitionId());
            }
            if (!goals.isEmpty()) this.goalRepository.delete(goals);
        }

        this.userRepository.delete(user.getUserId());

        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/addFriend/{username}", method = RequestMethod.PUT)
    public ResponseEntity<?> befriendUser(@RequestHeader("token") String token, @PathVariable("username") String username) {
        User currentUser = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (currentUser == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot add friend!");

        if (currentUser.getUsername().equals(username)) {
            return new ResponseEntity(new CustomErrorType("A User can't befriend itself!"),
                    HttpStatus.UNAUTHORIZED
            );
        }

        User friend = userRepository.findUserByUsername(username);
        if (friend == null) throw new UserNotFoundException("User with username " + username + " not found, cannot add friend!");

        currentUser.addFriend(friend);
        friend.addFriend(currentUser);

        userRepository.save(currentUser);
        userRepository.save(friend);
        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }

    @RequestMapping(value = "/checkUsername/{username}", method = RequestMethod.GET)
    public ResponseEntity<?> checkUsernameAvailability(@RequestHeader("token") String token, @PathVariable("username") String username) {
        boolean available = false;

        if (this.userRepository.findUserByUsername(username) == null || this.userRepository.findUserByAuthId(JWT.decode(token).getSubject()).getUsername().equals(username)) available = true;

        return new ResponseEntity<Boolean>(available, HttpStatus.OK);
    }

    //Update user prestations
    private void updateUserPrestations(String authid){
        User user = userRepository.findUserByAuthId(authid);

        if(user.getTrackings() != null && user.getTrackings().size() > 0){
            //Calculate avg speed
            if (user.getTrackings() != null) {
                double avgSpeed = user.getTrackings().stream().map(t -> t.getAvgSpeed() * t.getTotalDuration()).mapToDouble(Number::doubleValue).sum() /
                        user.getTrackings().stream().map(tt -> tt.getTotalDuration()).mapToLong(Number::longValue).sum();
                user.setAvgSpeed(avgSpeed);
            } else {
                user.setAvgSpeed(0);
            }

            //Calculate avg distance
            if (user.getTrackings() != null) {
                double avgDistance = user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).average().getAsDouble();
                user.setAvgDistance(avgDistance);
            } else {
                user.setAvgDistance(0);
            }

            //Calculate total distance
            if (user.getTrackings() != null) {
                double totalDistance = user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).sum();
                user.setTotalDistance((long) totalDistance);
            } else {
                user.setTotalDistance(0);
            }

            //Calculate max distance
            if (user.getTrackings() != null) {
                double maxDistance = user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).max().getAsDouble();
                user.setMaxDistance((long) maxDistance);
            } else {
                user.setMaxDistance(0);
            }

            //Calculate max speed
            if (user.getTrackings() != null) {
                double maxSpeed = user.getTrackings().stream().map(t -> t.getMaxSpeed()).mapToDouble(Number::doubleValue).max().getAsDouble();
                user.setMaxSpeed(maxSpeed);
            } else {
                user.setMaxSpeed(0);
            }

            //Calculate nr of wins
            if (user.getCompetitionsWon() != null) {
                int nrOfWins = user.getCompetitionsWon().size();
                user.setNrOfCompetitionsWon(nrOfWins);
            } else {
                user.setNrOfCompetitionsWon(0);
            }

            //Calculate ran marathon
            boolean ranMarathon = false;
            if (user.getCompetitionsWon() != null) {
                if (user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).max().getAsDouble() > 42195)
                    ranMarathon = true;
                user.setRanMarathon(ranMarathon);
            } else {
                user.setRanMarathon(false);
            }

            //Calculate ran 10KM
            boolean ran10 = false;
            if (user.getCompetitionsWon() != null) {
                if (user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).max().getAsDouble() > 10000)
                    ran10 = true;
                user.setRanTenKm(ran10);
            } else {
                user.setRanTenKm(false);
            }

            //Calculate ran 20KM
            boolean ran20 = false;
            if (user.getCompetitionsWon() != null) {
                if (user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).max().getAsDouble() > 20000)
                    ran20 = true;
                user.setRanTwentyKm(ran20);
            } else {
                user.setRanTwentyKm(false);
            }

            //Update user
            userRepository.save(user);
        }


    }

}
