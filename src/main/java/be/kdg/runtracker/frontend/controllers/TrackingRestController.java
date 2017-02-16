package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Coordinate;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.exceptions.NoContentException;
import be.kdg.runtracker.backend.exceptions.NotFoundException;
import be.kdg.runtracker.backend.exceptions.UnauthorizedUserException;
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
@CrossOrigin
@RequestMapping("/trackings/")
public class TrackingRestController {

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
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Trackings!");

        List<Tracking> trackings = user.getTrackings();
        if (trackings == null || trackings.isEmpty()) throw new NoContentException("No Trackings found for User with token " + token + "!");
        if (trackings != null && !trackings.isEmpty()) trackings.stream().forEach(tracking -> tracking.setCoordinates(this.coordinatesRepository.readCoordinatesByTrackingId(tracking.getTrackingId())));

        return new ResponseEntity<List<Tracking>>(trackings, HttpStatus.OK);
    }

    /**
     * Get all {@link Tracking}s of a friend.
     * @param token Token
     * @return List of Trackings
     */
    @RequestMapping(value = "/getAllTrackings/{username}", method = RequestMethod.GET)
    public ResponseEntity<List<Tracking>> getAllTrackingsOfFriend(@RequestHeader("token") String token, @PathVariable("username") String username) {

        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found");

        User friend = userRepository.findUserByUsername(username);
        if (friend == null) throw new UnauthorizedUserException("User with username " + username + " not found, cannot fetch Trackings!");
        if (!user.getFriends().contains(friend)) {
            return new ResponseEntity(new CustomErrorType("Not friends with User with username " + username + "!"),
                    HttpStatus.UNAUTHORIZED);
        }

        List<Tracking> trackings = friend.getTrackings();
        if (trackings == null || trackings.isEmpty()) throw new NoContentException("No Trackings found for User with username " + friend.getUsername() + "!");
        if (trackings != null && !trackings.isEmpty()) trackings.stream().forEach(t -> t.setCoordinates(this.coordinatesRepository.readCoordinatesByTrackingId(t.getTrackingId())));

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
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Trackings!");

        Tracking tracking = this.trackingRepository.findTrackingByTrackingId(trackingId);
        if (tracking == null) throw new NoContentException("No Tracking with id " + trackingId + " for User with token " + token + "!");
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
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Trackings!");

        tracking.setUser(user);
        user.addTracking(tracking);

        this.trackingRepository.save(tracking);
        this.userRepository.save(user);
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
    public ResponseEntity<?> addCoordinateToTracking(@RequestHeader("token") String token, @PathVariable long trackingId, @RequestBody Coordinate coordinate, UriComponentsBuilder ucBuilder) {
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Trackings!");

        Tracking tracking = this.trackingRepository.findTrackingByTrackingId(trackingId);
        if (tracking == null) throw new NotFoundException("Tracking with id " + trackingId + "not found!");

        tracking.addCoordinate(coordinate);
        this.trackingRepository.save(tracking);

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
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Trackings!");

        Tracking tracking = this.trackingRepository.findTrackingByTrackingId(trackingId);
        if (tracking == null) throw new NotFoundException("Tracking with id " + trackingId + "not found!");

        Competition competition = null;
        if (tracking != null) competition = tracking.getCompetition();

        if (user.getTrackings().contains(tracking)) {
            user.getTrackings().remove(tracking);
            this.coordinatesRepository.deleteCoordinatesCollection(trackingId);
            this.userRepository.save(user);
        } else {
            throw new NotFoundException("User with token " + token + "does not have Tracking with trackingId " + trackingId + ".");
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
