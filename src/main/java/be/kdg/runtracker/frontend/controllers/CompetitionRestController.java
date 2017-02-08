package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.persistence.CompetitionRepository;
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
@RequestMapping("/competitions/")
public class CompetitionRestController {

    public static final Logger logger = Logger.getLogger(CompetitionRestController.class);
    private CompetitionRepository competitionRepository;
    private UserRepository userRepository;

    @Autowired
    public CompetitionRestController(CompetitionRepository competitionRepository, UserRepository userRepository) {
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
    }

    protected CompetitionRestController() { }

    /**
     * Get all {@link Competition}s.
     * @return List of Competitions
     */
    @RequestMapping(method = RequestMethod.GET)
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
     * @param authId Authorization id
     * @return List of Competitions
     */
    @RequestMapping(value = "/{authId}/created", method = RequestMethod.GET)
    public ResponseEntity<List<Competition>> getAllCreatedCompetitionsFromUser(@PathVariable("authId") String authId) {
        logger.info("Fetching all Competitions created by User with authorization id: " + authId + ".");

        User user = this.userRepository.findUserByAuthId(authId);

        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        List<Competition> competitions = user.getCompetitionsCreated();
        if (competitions.isEmpty()) {
            logger.error("No Competitions found!");
            return new ResponseEntity(new CustomErrorType("No Competitions found!"), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Competition>>(competitions, HttpStatus.OK);
    }

    /**
     * Get all {@link Competition}s won by a certain {@link User}.
     * @param authId Authorization id
     * @return List of Competitions
     */
    @RequestMapping(value = "/{authId}/won", method = RequestMethod.GET)
    public ResponseEntity<List<Competition>> getAllWonCompetitionsFromUser(@PathVariable("authId") String authId) {
        logger.info("Fetching all Competitions won by User with authorization id: " + authId + ".");

        User user = this.userRepository.findUserByAuthId(authId);

        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        List<Competition> competitions = user.getCompetitionsWon();
        if (competitions.isEmpty()) {
            logger.error("No Competitions found!");
            return new ResponseEntity(new CustomErrorType("No Competitions found!"), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Competition>>(competitions, HttpStatus.OK);
    }

    /**
     * Get all {@link Competition}s ran by a certain {@link User}.
     * @param authId Authorization id
     * @return List of Competitions
     */
    @RequestMapping(value = "/{authId}/run", method = RequestMethod.GET)
    public ResponseEntity<List<Competition>> getAllRunCompetitionsFromUser(@PathVariable("authId") String authId) {
        logger.info("Fetching all Competitions run by User with authorization id: " + authId + ".");

        User user = this.userRepository.findUserByAuthId(authId);

        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        List<Competition> competitions = user.getCompetitionsRun();
        if (competitions.isEmpty()) {
            logger.error("No Competitions found!");
            return new ResponseEntity(new CustomErrorType("No Competitions found!"), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Competition>>(competitions, HttpStatus.OK);
    }

    /**
     * {@link User} creates a {@link Competition}.
     * @param authId Authorization id
     * @param competition Created Competition
     * @param ucBuilder Uri Builder
     * @return HTTP Status
     */
    @RequestMapping(value = "/{authId}/create", method = RequestMethod.POST)
    public ResponseEntity<?> createCompetition(@PathVariable("authId") String authId, @RequestBody Competition competition, UriComponentsBuilder ucBuilder) {
        logger.info("Creating a Competition " + competition + " for User with authId " + authId + ".");

        User user = this.userRepository.findUserByAuthId(authId);

        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        user.addCompetitionsCreated(competition);
        competition.setUserCreated(user);

        competitionRepository.save(competition);
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/{authId}").buildAndExpand(user.getUserId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * {@link User} runs in a {@link Competition}.
     * @param authId Authorization id
     * @param competition Competition in which a User runs
     * @param ucBuilder Uri Builder
     * @return HTTP Status
     */
    @RequestMapping(value = "/{authId}/running", method = RequestMethod.POST)
    public ResponseEntity<?> runCompetition(@PathVariable("authId") String authId, @RequestBody Competition competition, UriComponentsBuilder ucBuilder) {
        logger.info("Competition " + competition + ", ran by User with authId " + authId + ".");

        User user = this.userRepository.findUserByAuthId(authId);

        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        user.addCompetitionsRan(competition);
        competition.addRunner(user);

        competitionRepository.save(competition);
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/{authId}").buildAndExpand(user.getUserId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * {@link User} wins a {@link Competition}.
     * @param authId Authorization id
     * @param competition Competition which a User won
     * @param ucBuilder Uri Builder
     * @return HTTP Status
     */
    @RequestMapping(value = "/{authId}/winning", method = RequestMethod.POST)
    public ResponseEntity<?> wonCompetition(@PathVariable("authId") String authId, @RequestBody Competition competition, UriComponentsBuilder ucBuilder) {
        logger.info("Competition " + competition + ", won by User with authId " + authId + ".");

        User user = this.userRepository.findUserByAuthId(authId);

        if (user == null) {
            logger.error("User with authId " + authId + "not found!");
            return new ResponseEntity(new CustomErrorType("User with authId " + authId + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        user.addCompetitionsWon(competition);
        competition.setUserWon(user);

        competitionRepository.save(competition);
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/users/{authId}").buildAndExpand(user.getUserId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    /**
     * Delete a {@link Competition}.
     * @param competitionId Competition id
     * @param authId Authorization id
     * @return HTTP Status
     */
    @RequestMapping(value = "/{competitionId}/{authId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCompetition(@PathVariable("competitionId") long competitionId, @PathVariable("authId") String authId) {
        logger.info("Fetching & Deleting Competition with id: " + competitionId + ".");

        User user = userRepository.findUserByAuthId(authId);
        if (user == null) {
            logger.error("User with id " + authId + " does not exist!");
            return new ResponseEntity(new CustomErrorType("User with id " + authId + " does not exist!"),
                    HttpStatus.NOT_FOUND
            );
        }

        Competition competition = competitionRepository.findCompetitionByCompetitionId(competitionId);
        if (user == null) {
            logger.error("Competition with id " + competitionId + " does not exist!");
            return new ResponseEntity(new CustomErrorType("Competition with id " + competitionId + " does not exist!"),
                    HttpStatus.NOT_FOUND
            );
        }

        // TODO: Kan een competition cascading worden gedelete?
        competitionRepository.delete(competition.getCompetitionId());

        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

}
