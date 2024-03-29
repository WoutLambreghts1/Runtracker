package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.profile.Friendship;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Coordinate;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.exceptions.NoContentException;
import be.kdg.runtracker.backend.exceptions.NotFoundException;
import be.kdg.runtracker.backend.exceptions.UnauthorizedUserException;
import be.kdg.runtracker.backend.services.api.CoordinatesService;
import be.kdg.runtracker.backend.services.api.FriendshipService;
import be.kdg.runtracker.backend.services.api.TrackingService;
import be.kdg.runtracker.backend.services.api.UserService;
import be.kdg.runtracker.frontend.dto.ShortTracking;
import be.kdg.runtracker.frontend.dto.TrackingDTO;
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
@RequestMapping("/trackings/")
public class TrackingRestController {

    private TrackingService trackingService;
    private UserService userService;
    private CoordinatesService coordinatesService;
    private FriendshipService friendshipService;

    @Autowired
    public TrackingRestController(TrackingService trackingService, UserService userService, CoordinatesService coordinatesService,FriendshipService friendshipService) {
        this.trackingService = trackingService;
        this.userService = userService;
        this.coordinatesService = coordinatesService;
        this.friendshipService = friendshipService;
    }

    protected TrackingRestController() { }

    /**
     * Get all {@link Tracking}s of a {@link User}.
     * @param token Token
     * @return List of Trackings
     */
    @RequestMapping(value = "/getTrackings", method = RequestMethod.GET)
    public ResponseEntity<List<ShortTracking>> getAllTrackingsOfUser(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Trackings!");

        List<Tracking> trackings = user.getTrackings();
        if (trackings == null || trackings.isEmpty()) throw new NoContentException("No Trackings found for User with token " + token + "!");
        if (trackings != null && !trackings.isEmpty()) trackings.stream().forEach(tracking -> tracking.setCoordinates(this.coordinatesService.readCoordinatesByTrackingId(tracking.getTrackingId())));

        List<ShortTracking> trackingsDTO = new ArrayList<>();

        for (Tracking tracking : trackings) {
            trackingsDTO.add(new ShortTracking(tracking));
        }

        return new ResponseEntity<List<ShortTracking>>(trackingsDTO, HttpStatus.OK);
    }

    /**
     * Get all {@link Tracking}s of a friend.
     * @param token Token
     * @return List of Trackings
     */
    @RequestMapping(value = "/getAllTrackings/{username}", method = RequestMethod.GET)
    public ResponseEntity<List<ShortTracking>> getAllTrackingsOfFriend(@RequestHeader("token") String token, @PathVariable("username") String username) {

        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found");

        User friend = userService.findUserByUsername(username);
        if (friend == null) throw new UnauthorizedUserException("User with username " + username + " not found, cannot fetch Trackings!");

        if (!friendshipService.checkFriendship(user,friend)) {
            return new ResponseEntity(new CustomErrorType("Not friends with User with username " + username + "!"),
                    HttpStatus.UNAUTHORIZED);
        }

        List<Tracking> trackings = friend.getTrackings();
        if (trackings == null || trackings.isEmpty()) throw new NoContentException("No Trackings found for User with username " + friend.getUsername() + "!");

        trackings.stream().forEach(t -> t.setCoordinates(this.coordinatesService.readCoordinatesByTrackingId(t.getTrackingId())));

        List<ShortTracking> trackingsDTO = new ArrayList<>();
        for (Tracking tracking : trackings) {
            trackingsDTO.add(new ShortTracking(tracking));
        }

        return new ResponseEntity<List<ShortTracking>>(trackingsDTO, HttpStatus.OK);
    }

    /**
     * Get all {@link Tracking}s of all friends.
     * @param token Token
     * @return List of Trackings
     */
    @RequestMapping(value = "/getAllTrackingsFriends", method = RequestMethod.GET)
    public ResponseEntity<List<ShortTracking>> getAllTrackingsFriends(@RequestHeader("token") String token) {

        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found");

        List<Tracking> trackings=new ArrayList<>();
        if (user.getFriendships() == null || user.getFriendships().isEmpty()) throw new NoContentException("No friends found for User with id: " + user.getAuthId() + "!");
        for (Friendship friendship : user.getFriendships()) {
            if(friendshipService.checkFriendship(user,friendship.getFriend())){
                trackings.addAll(friendship.getFriend().getTrackings());
            }
        }

        if (trackings == null || trackings.isEmpty()) throw new NoContentException("No Trackings found for friends of user with username " + user.getUsername() + "!");

        trackings.stream().forEach(t -> t.setCoordinates(this.coordinatesService.readCoordinatesByTrackingId(t.getTrackingId())));

        List<ShortTracking> trackingsDTO = new ArrayList<>();
        for (Tracking tracking : trackings) {
            trackingsDTO.add(new ShortTracking(tracking));
        }

        return new ResponseEntity<List<ShortTracking>>(trackingsDTO, HttpStatus.OK);
    }

    /**
     * Get a specific {@link Tracking} from a {@link User}.
     * @param token Token
     * @param trackingId Tracking id
     * @return Tracking
     */
    @RequestMapping(value = "getTracking/{trackingId}", method = RequestMethod.GET)
    public ResponseEntity<ShortTracking> getTrackingFromUser(@RequestHeader("token") String token, @PathVariable("trackingId") long trackingId) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Trackings!");

        Tracking tracking = this.trackingService.findTrackingByTrackingId(trackingId);
        if (tracking == null) throw new NoContentException("No Tracking with id " + trackingId + " for User with token " + token + "!");
        tracking.setCoordinates(this.coordinatesService.readCoordinatesByTrackingId(trackingId));

        ShortTracking trackingDTO = new ShortTracking(tracking);

        return new ResponseEntity<ShortTracking>(trackingDTO, HttpStatus.OK);
    }

    /**
     * Create a {@link Tracking}.
     * @param token Token
     * @param trackingDTO Tracking
     * @param ucBuilder Uri Builder
     * @return HTTP Status Created
     */
    @RequestMapping(value = "/createTracking",method = RequestMethod.POST)
    public ResponseEntity<?> createTracking(@RequestHeader("token") String token, @RequestBody TrackingDTO trackingDTO, UriComponentsBuilder ucBuilder) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Trackings!");

        this.trackingService.saveTracking(trackingDTO.getAsTrackingWithoutCompetition(), user);

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
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Trackings!");

        Tracking tracking = this.trackingService.findTrackingByTrackingId(trackingId);
        if (tracking == null) throw new NotFoundException("Tracking with id " + trackingId + "not found!");

        tracking.addCoordinate(coordinate);
        this.trackingService.saveTracking(tracking, user);

        coordinate.setTrackingId(trackingId);
        this.coordinatesService.addCoordinateToCollection(trackingId, coordinate);

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
    @RequestMapping(value = "/deleteTracking/{trackingId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTrackingFromUser(@RequestHeader("token") String token, @PathVariable("trackingId") long trackingId) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Trackings!");

        Tracking tracking = this.trackingService.findTrackingByTrackingId(trackingId);
        if (tracking == null) throw new NotFoundException("Tracking with id " + trackingId + "not found!");

        this.trackingService.deleteTracking(tracking, user);

        return new ResponseEntity<Tracking>(HttpStatus.NO_CONTENT);
    }


}
