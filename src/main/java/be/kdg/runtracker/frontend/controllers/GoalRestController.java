package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.exceptions.NoContentException;
import be.kdg.runtracker.backend.exceptions.UnauthorizedUserException;
import be.kdg.runtracker.backend.services.api.GoalService;
import be.kdg.runtracker.backend.services.api.UserService;
import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/goals/")
public class GoalRestController {

    private GoalService goalService;
    private UserService userService;

    @Autowired
    public GoalRestController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    /**
     * Get all {@link be.kdg.runtracker.backend.dom.competition.Goal}s.
     * @return List of Competitions
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Goal>> getAllGoals(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Competitions!");

        List<Goal> goals = this.goalService.findAllGoals();
        if (goals == null || goals.isEmpty()) throw new NoContentException("No Goals were found!");

        return new ResponseEntity<List<Goal>>(goals, HttpStatus.OK);
    }

}
