package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.persistence.CompetitionRepository;
import be.kdg.runtracker.backend.persistence.TrackingRepository;
import be.kdg.runtracker.backend.persistence.UserRepository;
import be.kdg.runtracker.frontend.util.CustomErrorType;
import com.auth0.jwt.JWT;
import org.apache.log4j.Logger;
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

    public static final Logger logger = Logger.getLogger(UserRestController.class);
    private UserRepository userRepository;
    private TrackingRepository trackingRepository;
    private CompetitionRepository competitionRepository;

    @Autowired
    public UserRestController(UserRepository userRepository, TrackingRepository trackingRepository, CompetitionRepository competitionRepository) {
        this.userRepository = userRepository;
        this.trackingRepository = trackingRepository;
        this.competitionRepository = competitionRepository;
    }

    protected UserRestController() { }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers() {
        logger.info("Fetching all Users.");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            logger.error("No Users were found!");
            return new ResponseEntity(new CustomErrorType("No Users were found!"),
                    HttpStatus.NO_CONTENT
            );
        }

        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }

    /**
     * Get {@link User} by its authId.
     * @param token authorization id
     * @return User
     */

    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@RequestHeader("token") String token) {
        logger.info("Fetching User with token " + token + ".");

        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());

        if (user == null) {
            logger.error("User with token " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

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
        logger.info("Creating User: " + user + ".");
        user.setAuthId(JWT.decode(token).getSubject());
        if (userRepository.findUserByAuthId(user.getAuthId()) != null) {
            logger.error("A User with name " + user.getFirstname() + " " + user.getLastname() + " already exists!");
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
        logger.info("Updating User with token: " + token + ".");

        User currentUser = userRepository.findUserByAuthId(JWT.decode(token).getSubject());

        if (currentUser == null) {
            logger.error("User with token: " + token + " not found!");
            return new ResponseEntity(new CustomErrorType("User with token: " + token + " not found!"),
                    HttpStatus.NOT_FOUND
            );
        }

        if (!user.getUsername().equals(currentUser.getUsername())) {
            if (this.userRepository.findUserByUsername(user.getUsername()) != null) {
                logger.error("Could not update User! Username " + user.getUsername() + " already exists!");
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
        logger.info("Fetching & Deleting User with token: " + token + ".");

        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) {
            logger.error("User with tokrn " + token + " does not exist!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " does not exist!"),
                    HttpStatus.NOT_FOUND
            );
        }

        List<Tracking> trackings = user.getTrackings();
        user.setTrackings(new ArrayList<>());
        if (trackings != null && !trackings.isEmpty()) this.trackingRepository.delete(trackings);
        List<Competition> competitionsRan = user.getCompetitionsRun();
        user.setCompetitionsRun(new ArrayList<>());
        List<Competition> competitionsWon = user.getCompetitionsWon();
        user.setCompetitionsWon(new ArrayList<>());
        List<Competition> competitionsCreated = user.getCompetitionsCreated();
        user.setCompetitionsCreated(new ArrayList<>());
        if (competitionsCreated != null && !competitionsCreated.isEmpty()) this.competitionRepository.delete(competitionsCreated);

        this.userRepository.delete(user.getUserId());

        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/addFriend/{username}", method = RequestMethod.PUT)
    public ResponseEntity<?> befriendUser(@RequestHeader("token") String token, @PathVariable("username") String username) {
        logger.info("Creating friendship between User with token: " + token + " and User with username " + username + ".");

        User currentUser = userRepository.findUserByAuthId(JWT.decode(token).getSubject());

        if (currentUser == null) {
            logger.error("User with token: " + token + " not found!");
            return new ResponseEntity(new CustomErrorType("User with token: " + token + " not found!"),
                    HttpStatus.NOT_FOUND
            );
        }

        if (currentUser.getUsername().equals(username)) {
            logger.error("A User can't befriend itself!");
            return new ResponseEntity(new CustomErrorType("A User can't befriend itself!"),
                    HttpStatus.UNAUTHORIZED
            );
        }

        User friend = userRepository.findUserByUsername(username);

        if (friend == null) {
            logger.error("User with username: " + username + " not found!");
            return new ResponseEntity(new CustomErrorType("User with username: " + username + " not found!"),
                    HttpStatus.NOT_FOUND
            );
        }

        currentUser.addFriend(friend);
        friend.addFriend(currentUser);

        userRepository.save(currentUser);
        userRepository.save(friend);
        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }

    @RequestMapping(value = "/checkUsername/{username}", method = RequestMethod.GET)
    public ResponseEntity<?> checkUsernameAvailability(@PathVariable("username") String username) {
        logger.info("Checking if username " + username + " is available.");

        boolean available = false;

        if (this.userRepository.findUserByUsername(username) == null) available = true;

        return new ResponseEntity<Boolean>(available, HttpStatus.OK);
    }

    //Update user prestations
    private void updateUserPrestations(String authid){
        User user = userRepository.findUserByAuthId(authid);

        if(user.getTrackings() != null && user.getTrackings().size() > 0){
            //Calculate avg speed
            double avgSpeed = user.getTrackings().stream().map(t -> t.getAvgSpeed() * t.getTotalDuration()).mapToDouble(Number::doubleValue).sum() /
                    user.getTrackings().stream().map(tt -> tt.getTotalDuration()).mapToLong(Number::longValue).sum();
            user.setAvgSpeed(avgSpeed);

            //Calculate avg distance
            double avgDistance = user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).average().getAsDouble();
            user.setAvgDistance(avgDistance);

            //Calculate total distance
            double totalDistance = user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).sum();
            user.setTotalDistance((long) totalDistance);

            //Calculate max distance
            double maxDistance = user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).max().getAsDouble();
            user.setMaxDistance((long) maxDistance);

            //Calculate max speed
            double maxSpeed = user.getTrackings().stream().map(t -> t.getMaxSpeed()).mapToDouble(Number::doubleValue).max().getAsDouble();
            user.setMaxSpeed(maxSpeed);

            //Calculate nr of wins
            int nrOfWins = user.getCompetitionsWon().size();
            user.setNrOfCompetitionsWon(nrOfWins);

            //Calculate ran marathon
            boolean ranMarathon = false;
            if (user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).max().getAsDouble() > 42195) ranMarathon = true;
            user.setRanMarathon(ranMarathon);

            //Calculate ran 10KM
            boolean ran10 = false;
            if (user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).max().getAsDouble() > 10000) ran10 = true;
            user.setRanTenKm(ran10);

            //Calculate ran 20KM
            boolean ran20 = false;
            if (user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).max().getAsDouble() > 20000) ran20 = true;
            user.setRanTwentyKm(ran20);

            //Update user
            userRepository.save(user);
        }


    }

}
