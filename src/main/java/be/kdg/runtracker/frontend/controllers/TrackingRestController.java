package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.persistence.CompetitionRepository;
import be.kdg.runtracker.backend.persistence.CoordinatesRepository;
import be.kdg.runtracker.backend.persistence.TrackingRepository;
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
     * @param authId Authorization id
     * @return List of Trackings
     */
    @RequestMapping(value = "/{authId}", method = RequestMethod.GET)
    public ResponseEntity<List<Tracking>> getAllTrackingsOfUser(@PathVariable("authId") String authId) {
        logger.info("Fetching all Trackings for User with authId " + authId + ".");

        User user = userRepository.findUserByAuthId(authId);

        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        List<Tracking> trackings = user.getTrackings();
        if (trackings.isEmpty()) {
            return new ResponseEntity(new CustomErrorType("No Trackings found for User with authId " + authId + "!"),
                    HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Tracking>>(trackings, HttpStatus.OK);
    }

    /**
     * Get all {@link Tracking}s of a friend.
     * @param authId Authorization id
     * @return List of Trackings
     */
    @RequestMapping(value = "/{authId}/{username}", method = RequestMethod.GET)
    public ResponseEntity<List<Tracking>> getAllTrackingsOfFriend(@PathVariable("authId") String authId, @PathVariable("username") String username) {
        logger.info("Fetching all Trackings for User with authId " + authId + ".");

        User user = userRepository.findUserByAuthId(authId);

        // Eigen User authenticeren
        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        // Bevestiging of het een vriend is.
        User friend = userRepository.findUserByUsername(username);
        if (!user.getFriends().contains(friend)) {
            logger.error("Not friends with User with authId " + authId + "!");
            return new ResponseEntity(new CustomErrorType("Not friends with User with authId " + authId + "!"),
                    HttpStatus.UNAUTHORIZED);
        }

        // Vraag alle tracking op.
        List<Tracking> trackings = friend.getTrackings();
        if (trackings.isEmpty()) {
            return new ResponseEntity(new CustomErrorType("No Trackings found for User with authId " + friend.getAuthId() + "!"),
                    HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Tracking>>(trackings, HttpStatus.OK);
    }

    /**
     * Get a specific {@link Tracking} from a {@link User}.
     * @param authId Authorization id
     * @param trackingId Tracking id
     * @return Tracking
     */
    @RequestMapping(value = "/{authId}/{trackingId}", method = RequestMethod.GET)
    public ResponseEntity<Tracking> getTrackingFromUser(@PathVariable("authId") String authId, @PathVariable("trackingId") long trackingId) {
        logger.info("Fetching a Tracking with id " + trackingId + " for User with authId " + authId + ".");

        User user = userRepository.findUserByAuthId(authId);
        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        Tracking tracking = trackingRepository.findTrackingByTrackingId(trackingId);
        if (tracking == null) {
            return new ResponseEntity(new CustomErrorType("No Tracking with id " + trackingId + " for User with authId " + authId + "!"),
                    HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<Tracking>(tracking, HttpStatus.OK);
    }

    /**
     * Create a {@link Tracking}.
     * @param authId Authorization id
     * @param tracking Tracking id
     * @param ucBuilder Uri Builder
     * @return HTTP Status Created
     */
    @RequestMapping(value = "/{authId}", method = RequestMethod.POST)
    public ResponseEntity<?> createTracking(@PathVariable("authId") String authId, @RequestBody Tracking tracking, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a Tracking " + tracking + " for User with authId " + authId + ".");

        User user = userRepository.findUserByAuthId(authId);

        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        tracking.setUser(user);
        user.addTracking(tracking);

        this.userRepository.save(user);
        this.trackingRepository.save(tracking);
        long trackingId = this.trackingRepository.findAll().get(this.trackingRepository.findAll().size() -1).getTrackingId();
        this.coordinatesRepository.createCoordinatesCollection(trackingId, tracking.getCoordinates());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/{authId}").buildAndExpand(user.getAuthId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * Delete a specific {@link Tracking} for a User.
     * @param authId Authorization id
     * @param trackingId Tracking id
     * @return HTTP status
     */
    @RequestMapping(value = "/{authId}/{trackingId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTrackingFromUser(@PathVariable("authId") String authId, @PathVariable("trackingId") long trackingId) {
        logger.info("Deleting a Tracking with id " + trackingId + " for User with authId " + authId + ".");

        User user = userRepository.findUserByAuthId(authId);
        Tracking tracking = trackingRepository.findTrackingByTrackingId(trackingId);
        Competition competition = tracking.getCompetition();

        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        if (tracking == null) {
            return new ResponseEntity(new CustomErrorType("No Tracking with id " + trackingId + " for User with authId " + authId + "!"),
                    HttpStatus.NOT_FOUND);
        }

        if (user.getTrackings().contains(tracking)) {
            user.getTrackings().remove(tracking);
            this.userRepository.save(user);
        } else {
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + "does not have Tracking with trackingId " + trackingId + "."),
                    HttpStatus.NOT_FOUND);
        }

        if (competition != null) {
            competition.removeTracking(tracking);
        }

        this.coordinatesRepository.deleteCoordinatesCollection(trackingId);

        return new ResponseEntity<Tracking>(HttpStatus.NO_CONTENT);
    }

}
