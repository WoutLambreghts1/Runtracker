package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Coordinate;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.persistence.CompetitionRepository;
import be.kdg.runtracker.backend.persistence.CoordinatesRepository;
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

import java.util.List;

@RepositoryRestController
@RequestMapping("/trackings/")
public class TrackingRestController {

    public static final Logger logger = Logger.getLogger(TrackingRestController.class);
    private TrackingRepository trackingRepository;
    private UserRepository userRepository;
    private CoordinatesRepository coordinatesRepository;
    private CompetitionRepository competitionRepository;

    @Autowired
    public TrackingRestController(TrackingRepository trackingRepository, UserRepository userRepository, CoordinatesRepository coordinatesRepository, CompetitionRepository competitionRepository) {
        this.trackingRepository = trackingRepository;
        this.userRepository = userRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.competitionRepository = competitionRepository;
    }

    protected TrackingRestController() { }

    /**
     * Get all {@link Tracking}s of a {@link User}.
     * @param token Token
     * @return List of Trackings
     */
    @RequestMapping(value = "/getAllTrackings", method = RequestMethod.GET)
    public ResponseEntity<List<Tracking>> getAllTrackingsOfUser(@RequestHeader("token") String token) {
        logger.info("Fetching all Trackings for User with authId " + token + ".");

        System.out.println(JWT.decode(token).getSubject());
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());

        if (user == null) {
            logger.error("User with authId " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        List<Tracking> trackings = user.getTrackings();
        trackings.stream().forEach(t -> t.setCoordinates(this.coordinatesRepository.readCoordinatesByTrackingId(t.getTrackingId())));

        if (trackings.isEmpty()) {
            return new ResponseEntity(new CustomErrorType("No Trackings found for User with token " + token + "!"),
                    HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Tracking>>(trackings, HttpStatus.OK);
    }

    /**
     * Get all {@link Tracking}s of a friend.
     * @param token Token
     * @return List of Trackings
     */
    @RequestMapping(value = "/getAllTrackings/{username}", method = RequestMethod.GET)
    public ResponseEntity<List<Tracking>> getAllTrackingsOfFriend(@RequestHeader("token") String token, @PathVariable("username") String username) {
        logger.info("Fetching all Trackings for User with token " + token + ".");

        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());

        if (user == null) {
            logger.error("User with token " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        User friend = userRepository.findUserByUsername(username);
        if (!user.getFriends().contains(friend)) {
            logger.error("Not friends with User with token " + token + "!");
            return new ResponseEntity(new CustomErrorType("Not friends with User with token " + token + "!"),
                    HttpStatus.UNAUTHORIZED);
        }

        // Vraag alle tracking op.
        List<Tracking> trackings = friend.getTrackings();
        trackings.stream().forEach(t -> t.setCoordinates(this.coordinatesRepository.readCoordinatesByTrackingId(t.getTrackingId())));

        if (trackings.isEmpty()) {
            return new ResponseEntity(new CustomErrorType("No Trackings found for User with username " + friend.getUsername() + "!"),
                    HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Tracking>>(trackings, HttpStatus.OK);
    }

    /**
     * Get a specific {@link Tracking} from a {@link User}.
     * @param token Token
     * @param trackingId Tracking id
     * @return Tracking
     */
    @RequestMapping(value = "getTracking/{trackingId}", method = RequestMethod.GET)
    public ResponseEntity<Tracking> getTrackingFromUser(@RequestHeader("token") String token, @PathVariable("trackingId") long trackingId) {
        logger.info("Fetching a Tracking with id " + trackingId + " for User with token " + token + ".");

        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) {
            logger.error("User with token " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        Tracking tracking = this.trackingRepository.findTrackingByTrackingId(trackingId);
        if (tracking == null) {
            return new ResponseEntity(new CustomErrorType("No Tracking with id " + trackingId + " for User with token " + token + "!"),
                    HttpStatus.NO_CONTENT);
        }
        tracking.setCoordinates(this.coordinatesRepository.readCoordinatesByTrackingId(trackingId));
        return new ResponseEntity<Tracking>(tracking, HttpStatus.OK);
    }

    /**
     * Create a {@link Tracking}.
     * @param token Token
     * @param tracking Tracking
     * @param ucBuilder Uri Builder
     * @return HTTP Status Created
     */
    @RequestMapping(value = "/createTracking",method = RequestMethod.POST)
    public ResponseEntity<?> createTracking(@RequestHeader("token") String token, @RequestBody Tracking tracking, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a Tracking " + tracking + " for User with token " + token + ".");

        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());

        if (user == null) {
            logger.error("User with token " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        tracking.setUser(user);
        user.addTracking(tracking);

        this.trackingRepository.save(tracking);
        this.userRepository.save(user);
        // TODO: Performantere manier zoeken voor onderstaande functie.
        long trackingId = this.trackingRepository.findAll().get(this.trackingRepository.findAll().size() -1).getTrackingId();

        tracking.getCoordinates().stream().forEach(t -> t.setTrackingId(trackingId));
        this.coordinatesRepository.createCoordinatesCollection(trackingId, tracking.getCoordinates());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/getuser").buildAndExpand(user.getAuthId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * Create a {@link Tracking}.
     * @param token Token
     * @param coordinate coordinate
     * @param trackingId tracking id
     * @param ucBuilder Uri Builder
     * @return HTTP Status Created
     */
    @RequestMapping(value = "/addCoordinateToTracking/{trackingId}",method = RequestMethod.POST)
    public ResponseEntity<?> addCoordinateToTracking(@RequestHeader("token") String token,@PathVariable long trackingId, @RequestBody Coordinate coordinate, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a Coordinate " + coordinate + " for User with token " + token + ".");

        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());

        if (user == null) {
            logger.error("User with token " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND);
        }


        coordinate.setTrackingId(trackingId);
        this.coordinatesRepository.addCoordinateToCollection(trackingId, coordinate);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/getuser").buildAndExpand(user.getAuthId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * Delete a specific {@link Tracking} for a User.
     * @param token Token
     * @param trackingId Tracking id
     * @return HTTP status
     */
    @RequestMapping(value = "deleteTracking/{trackingId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTrackingFromUser(@RequestHeader("token") String token, @PathVariable("trackingId") long trackingId) {
        logger.info("Deleting a Tracking with id " + trackingId + " for User with token " + token + ".");

        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        Tracking tracking = trackingRepository.findTrackingByTrackingId(trackingId);
        Competition competition = tracking.getCompetition();

        if (user == null) {
            logger.error("User with token " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        if (tracking.getTrackingId() == 0) {
            logger.error("No Tracking with id " + trackingId + " for User with token " + token + "!");
            return new ResponseEntity(new CustomErrorType("No Tracking with id " + trackingId + " for User with token " + token + "!"),
                    HttpStatus.NOT_FOUND);
        }

        if (user.getTrackings().contains(tracking)) {
            user.getTrackings().remove(tracking);
            this.coordinatesRepository.deleteCoordinatesCollection(trackingId);
            this.userRepository.save(user);
        } else {
            return new ResponseEntity(new CustomErrorType("User with token " + token + "does not have Tracking with trackingId " + trackingId + "."),
                    HttpStatus.NOT_FOUND);
        }

        if (competition != null) {
            competition.removeTracking(tracking);
            this.competitionRepository.save(competition);
        }

        this.coordinatesRepository.deleteCoordinatesCollection(trackingId);
        this.trackingRepository.delete(trackingId);

        return new ResponseEntity<Tracking>(HttpStatus.NO_CONTENT);
    }

}
