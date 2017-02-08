package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
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

    @Autowired
    public TrackingRestController(TrackingRepository trackingRepository, UserRepository userRepository, CoordinatesRepository coordinatesRepository) {
        this.trackingRepository = trackingRepository;
        this.userRepository = userRepository;
        this.coordinatesRepository = coordinatesRepository;
    }

    protected TrackingRestController() { }

    public ResponseEntity<List<Tracking>> getAllTrackingsOfUser(@PathVariable("authId") String authId) {
        logger.info("Fetching all Trackings for User with authId " + authId + ".");

        // Eigen User authenticeren
        if (userRepository.findUserByAuthId(authId) == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        // Vraag alle tracking op.
        List<Tracking> trackings = userRepository.findUserByAuthId(authId).getTrackings();
        if (trackings.isEmpty()) {
            return new ResponseEntity(new CustomErrorType("No Trackings found for User with authId " + authId + "!"),
                    HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Tracking>>(trackings, HttpStatus.OK);
    }

    /**
     * Get all {@link Tracking}s for a specific {@link User}.
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
                    HttpStatus.NOT_FOUND
            );
        }

        // Bevestiging of het een vriend is.
        User friend = userRepository.findUserByUsername(username);
        if (!user.getFriends().contains(friend)) {
            logger.error("Not friends with User with authId " + authId + "!");
            return new ResponseEntity(new CustomErrorType("Not friends with User with authId " + authId + "!"),
                    HttpStatus.UNAUTHORIZED
            );
        }

        // Vraag alle tracking op.
        List<Tracking> trackings = friend.getTrackings();
        if (trackings.isEmpty()) {
            return new ResponseEntity(new CustomErrorType("No Trackings found for User with authId " + authId + "!"),
                    HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Tracking>>(trackings, HttpStatus.OK);
    }

    /**
     * Get a specific {@link Tracking} from a {@link User}.
     * @param authId
     * @param trackingId
     * @return
     */
    @RequestMapping(value = "/{authId}/{trackingId}", method = RequestMethod.GET)
    public ResponseEntity<Tracking> getTrackingFromUser(@PathVariable("authId") String authId, @PathVariable("trackingId") long trackingId) {
        logger.info("Fetching a Tracking with id " + trackingId + " for User with authId " + authId + ".");

        if (userRepository.findUserByAuthId(authId) == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        Tracking tracking = trackingRepository.findTrackingByTrackingId(trackingId);
        if (tracking == null) {
            return new ResponseEntity(new CustomErrorType("No Tracking with id " + trackingId + " for User with authId " + authId + "!"),
                    HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<Tracking>(tracking, HttpStatus.OK);
    }

    /**
     *
     * @param authId
     * @param tracking
     * @param ucBuilder
     * @return
     */
    @RequestMapping(value = "/{authId}", method = RequestMethod.POST)
    public ResponseEntity<?> createTracking(@PathVariable("authId") String authId, @RequestBody Tracking tracking, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a Tracking " + tracking + " for User with authId " + authId + ".");

        User user = userRepository.findUserByAuthId(authId);

        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        tracking.setUser(user);
        user.addTracking(tracking);

        this.userRepository.save(user);
        this.trackingRepository.save(tracking);
        long trackingId = this.trackingRepository.findAll().get(this.trackingRepository.findAll().size() -1).getTrackingId();
        this.coordinatesRepository.createCoordinatesCollection(trackingId, tracking.getCoordinates());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/{authId}").buildAndExpand(user.getUserId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * Delete a specific {@link Tracking} for a User.
     * @param authId Authorization id
     * @param trackingId Tracking id
     * @return HTTP status
     */
    @RequestMapping(value = "/{authId}/{trackingId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTrackingFromUser(@PathVariable("authId") String authId, @PathVariable("authId") long trackingId) {
        logger.info("Deleting a Tracking with id " + trackingId + " for User with authId " + authId + ".");

        if (userRepository.findUserByAuthId(authId) == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        Tracking tracking = trackingRepository.findTrackingByTrackingId(trackingId);
        if (tracking == null) {
            return new ResponseEntity(new CustomErrorType("No Tracking with id " + trackingId + " for User with authId " + authId + "!"),
                    HttpStatus.NOT_FOUND);
        }

        this.coordinatesRepository.deleteCoordinatesCollection(trackingId);

        return new ResponseEntity<Tracking>(HttpStatus.NO_CONTENT);
    }

}
