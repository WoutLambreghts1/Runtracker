package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.exceptions.NoContentException;
import be.kdg.runtracker.backend.exceptions.NotFoundException;
import be.kdg.runtracker.backend.exceptions.UnauthorizedUserException;
import be.kdg.runtracker.backend.persistence.*;
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
@RequestMapping("/competitions/")
public class CompetitionRestController {

    public static final Logger logger = Logger.getLogger(CompetitionRestController.class);
    private CompetitionRepository competitionRepository;
    private UserRepository userRepository;
    private TrackingRepository trackingRepository;
    private CoordinatesRepository coordinatesRepository;
    private GoalRepository goalRepository;

    @Autowired
    public CompetitionRestController(CompetitionRepository competitionRepository, UserRepository userRepository, TrackingRepository trackingRepository, CoordinatesRepository coordinatesRepository, GoalRepository goalRepository) {
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
        this.trackingRepository = trackingRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.goalRepository = goalRepository;
    }

    protected CompetitionRestController() { }

    /**
     * Get all {@link Competition}s.
     * @return List of Competitions
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Competition>> getAllCompetitions(@RequestHeader("token") String token) {
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Competitions!");

        List<Competition> competitions = this.competitionRepository.findAll();
        if (competitions == null || competitions.isEmpty()) throw new NoContentException("No Competitions were found!");

        return new ResponseEntity<List<Competition>>(competitions, HttpStatus.OK);
    }

    /**
     * Get all {@link Competition}s created by {@link User}.
     * @param token Token
     * @return List of Competitions
     */
    @RequestMapping(value = "/getCreatedCompetitions", method = RequestMethod.GET)
    public ResponseEntity<List<Competition>> getAllCreatedCompetitionsFromUser(@RequestHeader("token") String token) {
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch created Competitions!");

        List<Competition> competitions = user.getCompetitionsCreated();
        if (competitions == null || competitions.isEmpty()) throw new NoContentException("No created Competitions found for User with token " + token + "!");

        return new ResponseEntity<List<Competition>>(competitions, HttpStatus.OK);
    }

    /**
     * Get all {@link Competition}s won by {@link User}.
     * @param token Token
     * @return List of Competitions
     */
    @RequestMapping(value = "/getWonCompetitions", method = RequestMethod.GET)
    public ResponseEntity<List<Competition>> getAllWonCompetitionsFromUser(@RequestHeader("token") String token) {
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch won Competitions!");

        List<Competition> competitions = user.getCompetitionsWon();
        if (competitions == null || competitions.isEmpty()) throw new NoContentException("No won Competitions found for User with token " + token + "!");

        return new ResponseEntity<List<Competition>>(competitions, HttpStatus.OK);
    }

    /**
     * Get all {@link Competition}s ran by {@link User}.
     * @param token Token
     * @return List of Competitions
     */
    @RequestMapping(value = "/getRanCompetitions", method = RequestMethod.GET)
    public ResponseEntity<List<Competition>> getAllRunCompetitionsFromUser(@RequestHeader("token") String token) {
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch ran Competitions!");

        List<Competition> competitions = user.getCompetitionsRun();
        if (competitions == null || competitions.isEmpty()) throw new NoContentException("No ran Competitions found for User with token " + token + "!");

        return new ResponseEntity<List<Competition>>(competitions, HttpStatus.OK);
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
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot create Competitions!");

        competition.setUserCreated(user);
        user.addCompetitionsCreated(competition);

        competitionRepository.save(competition);
        userRepository.save(user);

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
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch ran Competitions!");

        Competition competition = this.competitionRepository.findCompetitionByCompetitionId(competitionId);
        if (competition == null) throw new NotFoundException("Competition with id " + competitionId + " not found!");

        competition.addRunner(user);
        user.addCompetitionsRan(competition);

        competitionRepository.save(competition);
        userRepository.save(user);

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
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, User cannot win Competition!");

        Competition competition = this.competitionRepository.findCompetitionByCompetitionId(competitionId);
        if (competition == null) throw new NotFoundException("Competition with id " + competitionId + " not found!");

        competition.setUserWon(user);
        user.addCompetitionsWon(competition);

        competitionRepository.save(competition);
        userRepository.save(user);

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
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot delete Competition!");

        Competition competition = this.competitionRepository.findCompetitionByCompetitionId(competitionId);
        if (competition == null) throw new NotFoundException("Competition with id " + competitionId + " not found!");

        if (!user.getCompetitionsCreated().contains(competition)) {
            logger.error("Cannot delete Competition that User did not create!");
            return new ResponseEntity(new CustomErrorType("Cannot delete Competition that User did not create!"),
                    HttpStatus.UNAUTHORIZED);
        }

        List<Tracking> trackings = competition.getTrackings();
        if (trackings != null && !trackings.isEmpty()) {
            for (Tracking tracking : trackings) {
                tracking.setCompetition(null);
                tracking.setUser(null);
                this.trackingRepository.save(tracking);
                this.trackingRepository.delete(tracking.getTrackingId());
            }
        }

        if (user.getCompetitionsCreated() != null && user.getCompetitionsCreated().contains(competition))
            user.getCompetitionsCreated().remove(competition);
        if (user.getCompetitionsRun() != null && user.getCompetitionsRun().contains(competition))
            user.getCompetitionsRun().remove(competition);
        if (user.getCompetitionsWon() != null && user.getCompetitionsWon().contains(competition))
            user.getCompetitionsWon().remove(competition);
        this.userRepository.save(user);

        competition.setUserCreated(null);
        competition.setUsersRun(null);
        competition.setUserWon(null);
        competition.setTrackings(null);
        this.competitionRepository.save(competition);

        Goal goal = competition.getGoal();
        if (goal != null) this.goalRepository.delete(goal.getGoalId());

        this.competitionRepository.delete(competition.getCompetitionId());

        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/addTracking/{competitionId}", method = RequestMethod.POST)
    public ResponseEntity<?> addTrackingToCompetition(@RequestHeader("token") String token, @PathVariable("competitionId") long competitionId, @RequestBody Tracking tracking, UriComponentsBuilder ucBuilder) {
        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot add Tracking to Competition!");

        Competition competition = this.competitionRepository.findCompetitionByCompetitionId(competitionId);
        if (competition == null) throw new NoContentException("Competition with id " + competitionId + " not found!");

        competition.addTracking(tracking);
        user.addTracking(tracking);

        this.trackingRepository.save(tracking);
        this.competitionRepository.save(competition);
        this.userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/getUser").buildAndExpand(user.getAuthId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

}
