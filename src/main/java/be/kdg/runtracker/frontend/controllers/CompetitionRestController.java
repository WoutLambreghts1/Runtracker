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
@RequestMapping("/competitions/")
public class CompetitionRestController {

    public static final Logger logger = Logger.getLogger(CompetitionRestController.class);
    private CompetitionRepository competitionRepository;
    private UserRepository userRepository;
    private TrackingRepository trackingRepository;

    @Autowired
    public CompetitionRestController(CompetitionRepository competitionRepository, UserRepository userRepository, TrackingRepository trackingRepository) {
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
        this.trackingRepository = trackingRepository;
    }

    protected CompetitionRestController() { }

    /**
     * Get all {@link Competition}s.
     * @return List of Competitions
     */
    @RequestMapping(value = "getAllCompetitions", method = RequestMethod.GET)
    public ResponseEntity<List<Competition>> getAllCompetitions() {
        logger.info("Fetching all Competitions.");

        List<Competition> competitions = this.competitionRepository.findAll();
        if (competitions.isEmpty()) {
            logger.error("No Competitions found!");
            return new ResponseEntity(new CustomErrorType("No Competitions found!"), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Competition>>(competitions, HttpStatus.OK);
    }

    /**
     * Get all {@link Competition}s created by a certain {@link User}.
     * @param token Token
     * @return List of Competitions
     */
    @RequestMapping(value = "/getCreatedCompetitions", method = RequestMethod.GET)
    public ResponseEntity<List<Competition>> getAllCreatedCompetitionsFromUser(@RequestHeader("token") String token) {
        logger.info("Fetching all Competitions created by User with token: " + token + ".");

        User user = this.userRepository.findUserByAuthId(JWT.decode(token).getSubject());

        if (user == null) {
            logger.error("User with token " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        List<Competition> competitions = user.getCompetitionsCreated();
        if (competitions.isEmpty()) {
            logger.error("No Competitions found for User with token " + token + "!");
            return new ResponseEntity(new CustomErrorType("No Competitions found for User with token " + token + "!"), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Competition>>(competitions, HttpStatus.OK);
    }

    /**
     * Get all {@link Competition}s won by a certain {@link User}.
     * @param token Token
     * @return List of Competitions
     */
    @RequestMapping(value = "/GetWonCompetitions", method = RequestMethod.GET)
    public ResponseEntity<List<Competition>> getAllWonCompetitionsFromUser(@RequestHeader("token") String token) {
        logger.info("Fetching all Competitions won by User with token: " + token + ".");

        User user = this.userRepository.findUserByAuthId(JWT.decode(token).getSubject());

        if (user == null) {
            logger.error("User with token " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        List<Competition> competitions = user.getCompetitionsWon();
        if (competitions.isEmpty()) {
            logger.error("No Competitions found for User with token " + token + "!");
            return new ResponseEntity(new CustomErrorType("No Competitions found for User with token " + token + "!"), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Competition>>(competitions, HttpStatus.OK);
    }

    /**
     * Get all {@link Competition}s ran by a certain {@link User}.
     * @param token Token
     * @return List of Competitions
     */
    @RequestMapping(value = "/getRanCompetitions", method = RequestMethod.GET)
    public ResponseEntity<List<Competition>> getAllRunCompetitionsFromUser(@RequestHeader("token") String token) {
        logger.info("Fetching all Competitions run by User with token: " + token + ".");

        User user = this.userRepository.findUserByAuthId(JWT.decode(token).getSubject());

        if (user == null) {
            logger.error("User with token " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        List<Competition> competitions = user.getCompetitionsRun();
        if (competitions.isEmpty()) {
            logger.error("No Competitions found for User with token " + token + "!");
            return new ResponseEntity(new CustomErrorType("No Competitions found for User with token " + token + "!"), HttpStatus.NO_CONTENT);
        }

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
        logger.info("Creating a Competition " + competition + " for User with token " + token + ".");

        User user = this.userRepository.findUserByAuthId(JWT.decode(token).getSubject());

        if (user == null) {
            logger.error("User with token " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        competition.setUserCreated(user);
        user.addCompetitionsCreated(competition);

        competitionRepository.save(competition);
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/getUser").buildAndExpand(user.getAuthId()).toUri());
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
    public ResponseEntity<?> runCompetition(@RequestHeader("token") String token, @PathVariable("competitionId") String competitionId, UriComponentsBuilder ucBuilder) {
        logger.info("Competition " + competitionId + ", ran by User with token " + token + ".");

        User user = this.userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        Competition competition = this.competitionRepository.findCompetitionByCompetitionId(Long.valueOf(competitionId));

        if (user == null) {
            logger.error("User with token " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        if (competition == null) {
            logger.error("Competition with id " + competitionId + " not found!");
            return new ResponseEntity(new CustomErrorType("Competition with id " + competitionId + " not found!"),
                    HttpStatus.NOT_FOUND);
        }

        competition.addRunner(user);
        user.addCompetitionsRan(competition);

        competitionRepository.save(competition);
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/getUser").buildAndExpand(user.getAuthId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    /**
     * {@link User} wins a {@link Competition}.
     * @param token Token
     * @param competitionId Id of Competition which a User won
     * @param ucBuilder Uri Builder
     * @return HTTP Status
     */
    @RequestMapping(value = "/wins/{competitionId}", method = RequestMethod.POST)
    public ResponseEntity<?> wonCompetition(@RequestHeader("token") String token, @PathVariable("competitionId") String competitionId, UriComponentsBuilder ucBuilder) {
        logger.info("Competition " + competitionId + ", won by User with token " + token + ".");

        User user = this.userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        Competition competition = this.competitionRepository.findCompetitionByCompetitionId(Long.valueOf(competitionId));

        if (user == null) {
            logger.error("User with token " + token + "not found!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        if (competition == null) {
            logger.error("Competition with id " + competitionId + " not found!");
            return new ResponseEntity(new CustomErrorType("Competition with id " + competitionId + " not found!"),
                    HttpStatus.NOT_FOUND);
        }

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
        logger.info("Fetching & Deleting Competition with id: " + competitionId + ".");

        User user = userRepository.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) {
            logger.error("User with token " + token + " does not exist!");
            return new ResponseEntity(new CustomErrorType("User with token " + token + " does not exist!"),
                    HttpStatus.NOT_FOUND);
        }

        Competition competition = competitionRepository.findCompetitionByCompetitionId(competitionId);
        if (user == null) {
            logger.error("Competition with id " + competitionId + " does not exist!");
            return new ResponseEntity(new CustomErrorType("Competition with id " + competitionId + " does not exist!"),
                    HttpStatus.NOT_FOUND);
        }

        if (!user.getCompetitionsCreated().contains(competition)) {
            logger.error("Cannot delete Competition that User did not create!");
            return new ResponseEntity(new CustomErrorType("Cannot delete Competition that User did not create!"),
                    HttpStatus.UNAUTHORIZED);
        }

        List<Tracking> trackings = competition.getTrackings();
        competition.setTrackings(new ArrayList<>());
        competition.setUsersRun(new ArrayList<>());
        competition.setUserWon(null);
        competition.setUserCreated(null);
        this.trackingRepository.delete(trackings);
        competitionRepository.delete(competition.getCompetitionId());

        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

}
