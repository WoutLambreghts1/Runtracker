package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.config.MQTTConfig;
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
@RequestMapping("/mqtt/")
public class MQTTRestController {

    private UserService userService;

    @Autowired
    public MQTTRestController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get {@link MQTTConfig}.
     * @return The MQTTConfig file
     */
    @RequestMapping(value = "/getConfig", method = RequestMethod.GET)
    public ResponseEntity<MQTTConfig> getConfig(@RequestHeader("token") String token) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Competitions!");

        MQTTConfig config = new MQTTConfig(user.getUserId());

        return new ResponseEntity<MQTTConfig>(config, HttpStatus.OK);
    }

    /**
     * Get {@link MQTTConfig}.
     * @return The MQTTConfig file with competition topic
     */
    @RequestMapping(value = "/getConfig/{compId}", method = RequestMethod.GET)
    public ResponseEntity<MQTTConfig> getConfigWithCompId(@RequestHeader("token") String token, @PathVariable("compId") long compId) {
        User user = userService.findUserByAuthId(JWT.decode(token).getSubject());
        if (user == null) throw new UnauthorizedUserException("User with token " + token + " not found, cannot fetch Competitions!");

        MQTTConfig config = new MQTTConfig(user.getUserId(), compId);

        return new ResponseEntity<MQTTConfig>(config, HttpStatus.OK);
    }
}
