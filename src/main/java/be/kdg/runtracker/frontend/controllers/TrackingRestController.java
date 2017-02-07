package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
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
@RequestMapping("/api/{authId}/trackings/")
public class TrackingRestController {

    public static final Logger logger = Logger.getLogger(TrackingRestController.class);
    private TrackingRepository trackingRepository;
    private UserRepository userRepository;

    @Autowired
    public TrackingRestController(TrackingRepository trackingRepository, UserRepository userRepository) {
        this.trackingRepository = trackingRepository;
        this.userRepository = userRepository;
    }

    protected TrackingRestController() { }

    /**
     * Get all {@link Tracking}s for a specific {@link User}.
     * @param authId Authorization id
     * @return List of Trackings
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Tracking>> getAllTrackingsOfUser(@PathVariable("authId") long authId) {
        logger.info("Fetching all Trackings for User with authId " + authId + ".");

        if (userRepository.findUserByAuthId(authId) == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        List<Tracking> trackings = userRepository.findUserByAuthId(authId).getTrackings();
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
    @RequestMapping(value = "/{trackingId}", method = RequestMethod.GET)
    public ResponseEntity<Tracking> getTrackingFromUser(@PathVariable("authId") long authId, @PathVariable("authId") long trackingId) {
        logger.info("Fetching a Tracking with id " + trackingId + " for User with authId " + authId + ".");

        if (userRepository.findUserByAuthId(authId) == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        Tracking tracking = trackingRepository.findTrackingByTracking_id(trackingId);
        if (tracking == null) {
            return new ResponseEntity(new CustomErrorType("No Tracking with id " + trackingId + " for User with authId " + authId + "!"),
                    HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<Tracking>(tracking, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createTracking(@PathVariable("authId") long authId, @RequestBody Tracking tracking, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a Tracking " + tracking + " for User with authId " + authId + ".");

        if (userRepository.findUserByAuthId(authId) == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        User user = userRepository.findUserByAuthId(authId);
        tracking.setUser(user);
        user.addTracking(tracking);

        userRepository.save(user);
        trackingRepository.save(tracking);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/{authId}").buildAndExpand(user.getUser_id()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * Delete a specific {@link Tracking} for a User.
     * @param authId Authorization id
     * @param trackingId Tracking id
     * @return HTTP status
     */
    @RequestMapping(value = "/{trackingId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTrackingFromUser(@PathVariable("authId") long authId, @PathVariable("authId") long trackingId) {
        logger.info("Deleting a Tracking with id " + trackingId + " for User with authId " + authId + ".");

        if (userRepository.findUserByAuthId(authId) == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        Tracking tracking = trackingRepository.findTrackingByTracking_id(trackingId);
        if (tracking == null) {
            return new ResponseEntity(new CustomErrorType("No Tracking with id " + trackingId + " for User with authId " + authId + "!"),
                    HttpStatus.NOT_FOUND);
        }

        userRepository.delete(trackingId);

        return new ResponseEntity<Tracking>(HttpStatus.NO_CONTENT);
    }

}
