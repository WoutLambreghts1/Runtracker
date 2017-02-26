package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.exceptions.NoContentException;
import be.kdg.runtracker.backend.exceptions.NotFoundException;
import be.kdg.runtracker.backend.exceptions.UnauthorizedUserException;
import be.kdg.runtracker.backend.services.api.CompetitionService;
import be.kdg.runtracker.backend.services.api.TrackingService;
import be.kdg.runtracker.backend.services.api.UserService;
import be.kdg.runtracker.frontend.dto.ShortCompetition;
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
@RequestMapping("/competitions/")
public class CompetitionRestController {

    private CompetitionService competitionService;
    private UserService userService;
    private TrackingService trackingService;

    @Autowired
    public CompetitionRestController(CompetitionService competitionService, UserService userService, TrackingService trackingService) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.trackingService = trackingService;
    }

    protected CompetitionRestController() { }

    /**
     * Get all {@link Competition}s.
     * @return List of Competitions
     */
    @RequestMapping(value = "/getCompetitions", method = RequestMethod.GET)
    public ResponseEntity<List<ShortCompetition>> getAllCompetitions(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Competitions!");

        List<Competition> competitions = this.competitionService.findAllCompetitions();
        if (competitions == null || competitions.isEmpty()) throw new NoContentException("No Competitions were found!");

        List<ShortCompetition> competitionsDTO = new ArrayList<>();
        competitions.stream().forEach(competition -> competitionsDTO.add(new ShortCompetition(competition)));

        return new ResponseEntity<List<ShortCompetition>>(competitionsDTO, HttpStatus.OK);
    }

    /**
     * Get specific competition
     * @param token
     * @param competitionId
     * @return
     */
    @RequestMapping(value = "/{competitionId}", method = RequestMethod.GET)
    public ResponseEntity<ShortCompetition> getCompetitionById(@RequestHeader("token") String token,@PathVariable("competitionId") long competitionId) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Competitions!");

        Competition competition = this.competitionService.findCompetitionByCompetitionId(competitionId);
        if (competition == null) throw new NoContentException("No Competition was found!");


        return new ResponseEntity<ShortCompetition>(new ShortCompetition(competition), HttpStatus.OK);
    }

    /**
     * Get all {@link Competition}s created by {@link User}.
     * @param token Token
     * @return List of Competitions
     */
    @RequestMapping(value = "/getCreatedCompetitions", method = RequestMethod.GET)
    public ResponseEntity<List<ShortCompetition>> getAllCreatedCompetitionsFromUser(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch created Competitions!");

        List<Competition> competitions = user.getCompetitionsCreated();
        if (competitions == null || competitions.isEmpty()) throw new NoContentException("No created Competitions found for User with token " + token + "!");

        List<ShortCompetition> competitionsDTO = new ArrayList<>();
        competitions.stream().forEach(competition -> competitionsDTO.add(new ShortCompetition(competition)));

        return new ResponseEntity<List<ShortCompetition>>(competitionsDTO, HttpStatus.OK);
    }

    /**
     * Get all {@link Competition}s won by {@link User}.
     * @param token Token
     * @return List of Competitions
     */
    @RequestMapping(value = "/getWonCompetitions", method = RequestMethod.GET)
    public ResponseEntity<List<ShortCompetition>> getAllWonCompetitionsFromUser(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch won Competitions!");

        List<Competition> competitions = user.getCompetitionsWon();
        if (competitions == null || competitions.isEmpty()) throw new NoContentException("No won Competitions found for User with token " + token + "!");

        List<ShortCompetition> competitionsDTO = new ArrayList<>();
        competitions.stream().forEach(competition -> competitionsDTO.add(new ShortCompetition(competition)));

        return new ResponseEntity<List<ShortCompetition>>(competitionsDTO, HttpStatus.OK);
    }

    /**
     * Get all {@link Competition}s ran by {@link User}.
     * @param token Token
     * @return List of Competitions
     */
    @RequestMapping(value = "/getRanCompetitions", method = RequestMethod.GET)
    public ResponseEntity<List<ShortCompetition>> getAllRunCompetitionsFromUser(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch ran Competitions!");

        List<Competition> competitions = user.getCompetitionsRun();
        if (competitions == null || competitions.isEmpty()) throw new NoContentException("No ran Competitions found for User with token " + token + "!");

        List<ShortCompetition> competitionsDTO = new ArrayList<>();
        competitions.stream().forEach(competition -> competitionsDTO.add(new ShortCompetition(competition)));

        return new ResponseEntity<List<ShortCompetition>>(competitionsDTO, HttpStatus.OK);
    }

    /**
     * {@link User} creates a {@link Competition}.
     * @param token Token
     * @param competition Created Competition
     * @param ucBuilder Uri Builder
     * @return HTTP Status
     */
    @RequestMapping(value = "/createCompetition", method = RequestMethod.POST)
    public ResponseEntity<?> createCompetition(@RequestHeader("token") String token, @RequestBody Competition competition, UriComponentsBuilder ucBuilder) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot create Competitions!");

        competition.setUserCreated(user);
        competition.addRunner(user);
        user.addCompetitionsCreated(competition);
        user.addCompetitionsRan(competition);

        competitionService.saveCompetition(competition);
        userService.saveUser(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/getUser").buildAndExpand(user.getAuthId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * {@link User} runs in a {@link Competition}.
     * @param token Token
     * @param competitionId Id of Competition in which a User runs
     * @param ucBuilder Uri Builder
     * @return HTTP Status
     */
    @RequestMapping(value = "/running/{competitionId}", method = RequestMethod.POST)
    public ResponseEntity<?> runCompetition(@RequestHeader("token") String token, @PathVariable("competitionId") long competitionId, UriComponentsBuilder ucBuilder) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch ran Competitions!");

        Competition competition = this.competitionService.findCompetitionByCompetitionId(competitionId);
        if (competition == null) throw new NotFoundException("Competition with id " + competitionId + " not found!");


        competition.addRunner(user);
        user.addCompetitionsRan(competition);

        competitionService.saveCompetition(competition);
        userService.saveUser(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/getUser").buildAndExpand(user.getAuthId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * {@link User} wins a {@link Competition}.
     * @param token Token
     * @param competitionId Id of Competition which a User won
     * @param ucBuilder Uri Builder
     * @return HTTP Status
     */
    @RequestMapping(value = "/wins/{competitionId}", method = RequestMethod.POST)
    public ResponseEntity<?> wonCompetition(@RequestHeader("token") String token, @PathVariable("competitionId") long competitionId, UriComponentsBuilder ucBuilder) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, User cannot win Competition!");

        Competition competition = this.competitionService.findCompetitionByCompetitionId(competitionId);
        if (competition == null) throw new NotFoundException("Competition with id " + competitionId + " not found!");

        competition.setUserWon(user);
        user.addCompetitionsWon(competition);

        competitionService.saveCompetition(competition);
        userService.saveUser(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/getUser").buildAndExpand(user.getAuthId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * Delete a {@link Competition}.
     * @param token Token
     * @param competitionId Competition id
     * @return HTTP Status
     */
    @RequestMapping(value = "/delete/{competitionId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCompetition(@RequestHeader("token") String token, @PathVariable("competitionId") long competitionId) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot delete Competition!");

        Competition competition = this.competitionService.findCompetitionByCompetitionId(competitionId);
        if (competition == null) throw new NotFoundException("Competition with id " + competitionId + " not found!");

        if (!user.getCompetitionsCreated().contains(competition)) {
            return new ResponseEntity(new CustomErrorType("Cannot delete Competition that User did not create!"),
                    HttpStatus.UNAUTHORIZED);
        }

        this.competitionService.deleteCompetition(user, competition);

        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

    /**
     * Add a {@link Tracking} to a {@link Competition}.
     * @param token authorization id
     * @param competitionId competition id
     * @param tracking Tracking object
     * @param ucBuilder URI builder
     * @return HTTP status
     */
    @RequestMapping(value = "/addTracking/{competitionId}", method = RequestMethod.POST)
    public ResponseEntity<?> addTrackingToCompetition(@RequestHeader("token") String token, @PathVariable("competitionId") long competitionId, @RequestBody Tracking tracking, UriComponentsBuilder ucBuilder) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot add Tracking to Competition!");

        Competition competition = this.competitionService.findCompetitionByCompetitionId(competitionId);
        if (competition == null) throw new NoContentException("Competition with id " + competitionId + " not found!");

        competition.addTracking(tracking);
        user.addTracking(tracking);

        this.trackingService.saveTracking(tracking, user);
        this.competitionService.saveCompetition(competition);
        this.userService.saveUser(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/getUser").buildAndExpand(user.getAuthId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }


    /**
     * Get the {@link Goal} from a {@link Competition}.
     * @param token authorization id
     * @param competitionId competition id
     * @return Goal
     */
    @RequestMapping(value = "/{competitionId}/getGoal", method = RequestMethod.GET)
    public ResponseEntity<?> getGoalFromCompetition(@RequestHeader("token") String token, @PathVariable("competitionId") long competitionId) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch available Competitions!");
        System.err.println("\nUser with token " + token + " not found, cannot fetch available Competitions!\n");

        Competition competition = this.competitionService.findCompetitionByCompetitionId(competitionId);
        if (competition == null) throw new NoContentException("Competition with id " + competitionId + " not found!");
        System.err.println("\nCompetition with id " + competitionId + " not found!\n");

        Goal goal = competition.getGoal();
        if (goal == null) throw new NoContentException("Goal for Competition with id " + competitionId + " not found!");
        System.err.println("\nGoal for Competition with id " + competitionId + " not found!\n");

        return new ResponseEntity<>(goal, HttpStatus.OK);
    }

}
