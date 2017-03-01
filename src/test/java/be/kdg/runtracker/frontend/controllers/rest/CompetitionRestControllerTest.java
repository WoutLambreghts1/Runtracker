package be.kdg.runtracker.frontend.controllers.rest;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Coordinate;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.persistence.api.CompetitionRepository;
import be.kdg.runtracker.backend.persistence.api.GoalRepository;
import be.kdg.runtracker.backend.persistence.api.TrackingRepository;
import be.kdg.runtracker.backend.persistence.api.UserRepository;
import be.kdg.runtracker.backend.services.api.FriendshipService;
import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@Transactional
public class CompetitionRestControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private GoalRepository goalRepository;
    @Autowired
    private TrackingRepository trackingRepository;
    @Autowired
    private FriendshipService friendshipService;
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private User alexander;
    private User wout;
    private User jelle;

    private String tokenAlexander;
    private String tokenWout;
    private String tokenJelle;

    private Competition competition1;
    private Competition competition2;
    private Goal goal1;
    private Goal goal2;

    private Tracking trackingAlex;

    @Before
    public void setup() {
        tokenAlexander = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";
        this.alexander = new User();
        this.alexander.setAuthId(JWT.decode(tokenAlexander).getSubject());
        this.alexander.setFirstname("Alexander");
        this.alexander.setLastname("van Ravestyn");
        this.alexander.setUsername("alexvr");

        tokenWout = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDIifQ.gbIlCFTC5tyyLg8UqvxcoUUmhgUTdstZnHWNzEO1jVM";
        this.wout = new User();
        this.wout.setAuthId(JWT.decode(tokenWout).getSubject());
        this.wout.setFirstname("Wout");
        this.wout.setLastname("Lambreghts");
        this.wout.setUsername("woutl");

        tokenJelle = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDMifQ._Am34giuNzroHLmI482DV46DFq7UuNTdpzQ01aQdyio";
        this.jelle = new User();
        this.jelle.setAuthId(JWT.decode(tokenJelle).getSubject());
        this.jelle.setFirstname("Jelle");
        this.jelle.setLastname("Mannaerts");
        this.jelle.setUsername("jellem");

        this.goal1 = new Goal();
        this.goal1.setName("Goal1");
        this.goal1.setDistance(10);

        this.competition1 = new Competition(alexander, goal1, "topicABC", "comp1");
        this.competition1.addRunner(alexander);
        this.alexander.addCompetitionsCreated(competition1);
        this.alexander.addCompetitionsRan(competition1);
        this.competition1.addRunner(wout);
        this.wout.addCompetitionsRan(competition1);
        this.competition1.setUserWon(wout);
        this.wout.addCompetitionsWon(competition1);

        this.trackingAlex = new Tracking(10, 10, 10, 10);
        this.trackingAlex.setCompetition(competition1);
        this.alexander.addTracking(trackingAlex);

        this.goal2 = new Goal();
        this.goal2.setName("Goal2");
        this.goal2.setDistance(10);

        this.competition2 = new Competition(wout, goal2, "topicABC", "compet");
        this.wout.addCompetitionsCreated(competition2);
        this.competition2.addRunner(wout);

        this.trackingRepository.save(trackingAlex);

        this.userRepository.save(alexander);
        this.userRepository.save(wout);
        this.userRepository.save(jelle);

        this.goalRepository.save(goal1);
        this.goalRepository.save(goal2);

        this.competitionRepository.save(competition1);
        this.competitionRepository.save(competition2);

        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.mapper = new ObjectMapper();
    }

    @Test
    public void testGetAllCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getCompetitions").header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetCompetitionById() throws Exception {
        long competitionId = competitionRepository.findAll().get(0).getCompetitionId();
        this.mockMvc.perform(get("/competitions/" + competitionId).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetCompetitionByIdNonExistingComp() throws Exception {
        long competitionId = 0;
        this.mockMvc.perform(get("/competitions/" + competitionId).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetAllCompetitionsUnauthorized() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";

        this.mockMvc.perform(get("/competitions/getCompetitions").header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testGetAllRanCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getRanCompetitions").header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetAllRanCompetitionsFriends() throws Exception {
        //Add friend
        String username = "woutl";
        this.mockMvc.perform(put("/users/addFriend/" + username).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(put("/users/acceptFriend/" + alexander.getUsername()).header("token", tokenWout).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        //Create competition
        Competition competition = new Competition(wout, goal1, "topicABC", "comp6");
        this.mockMvc.perform(post("/competitions/createCompetition").content(mapper.writeValueAsString(competition)).header("token", tokenWout).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        //Add runner to competition
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(wout).get(this.competitionRepository.findCompetitionByUserCreated(wout).size() - 1).getCompetitionId();

        this.mockMvc.perform(post("/competitions/running/" + competitionId).header("token", tokenJelle).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());


        //Get ran competitions friends
        this.mockMvc.perform(get("/competitions/getRanCompetitionsFriends").header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(username)));
    }

    @Test
    public void testGetAllWonCompetitionsFriends() throws Exception {
        //Add friend
        String username = "woutl";
        this.mockMvc.perform(put("/users/addFriend/" + username).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(put("/users/acceptFriend/" + alexander.getUsername()).header("token", tokenWout).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        //Create competition
        Competition competition = new Competition(wout, goal1, "topicABC", "comp6");
        this.mockMvc.perform(post("/competitions/createCompetition").content(mapper.writeValueAsString(competition)).header("token", tokenWout).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        //Add runner to competition
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(wout).get(this.competitionRepository.findCompetitionByUserCreated(wout).size() - 1).getCompetitionId();

        this.mockMvc.perform(post("/competitions/running/" + competitionId).header("token", tokenJelle).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());


        //Win competition
        this.mockMvc.perform(post("/competitions/wins/" + competitionId).header("token", tokenWout).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        //Get won competitions friends
        this.mockMvc.perform(get("/competitions/getWonCompetitionsFriends").header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetAllRanCompetitionsUnauthorized() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";

        this.mockMvc.perform(get("/competitions/getRanCompetitions").header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testGetNoRanCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getRanCompetitions").header("token", tokenJelle).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetAllCreatedCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getCreatedCompetitions").header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetAllCreatedCompetitionsUnauthorized() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";

        this.mockMvc.perform(get("/competitions/getCreatedCompetitions").header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testGetNoCreatedCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getCreatedCompetitions").header("token", tokenJelle).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetAllWonCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getWonCompetitions").header("token", tokenWout).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetAllWonCompetitionsUnauthorized() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";

        this.mockMvc.perform(get("/competitions/getWonCompetitions").header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testGetNoWonCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getWonCompetitions").header("token", tokenJelle).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testCreateCompetition() throws Exception {
        Competition competition = new Competition(jelle, goal1, "topicABC", "comp6");

        this.mockMvc.perform(post("/competitions/createCompetition").content(mapper.writeValueAsString(competition)).header("token", tokenJelle).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        Competition createdCompetition = this.competitionRepository.findCompetitionByUserCreated(jelle).get(0);
        assertTrue("Username should be jellem for User created!", createdCompetition.getUserCreated().getUsername().equals("jellem"));
        assertTrue("Username should be jellem for User ran!", createdCompetition.getUsersRun().get(0).getUsername().equals("jellem"));
    }

    @Test
    public void testCreateCompetitionUnauthorized() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";
        Competition competition = new Competition(alexander, goal1, "topicABC", "comp123");

        this.mockMvc.perform(post("/competitions/createCompetition").content(mapper.writeValueAsString(competition)).header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testUserRunsInCompetition() throws Exception {
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(post("/competitions/running/" + competitionId).header("token", tokenJelle).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void testNonExistingUserRunsInCompetition() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(post("/competitions/running/" + competitionId).header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testUserRunsInNonExistingCompetition() throws Exception {
        long competitionId = 1;

        this.mockMvc.perform(post("/competitions/running/" + competitionId).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUserWinsCompetition() throws Exception {
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(post("/competitions/wins/" + competitionId).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void testNonExistingUserWinsCompetition() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(post("/competitions/wins/" + competitionId).header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testUserWinsNonExistingCompetition() throws Exception {
        long competitionId = 1;

        this.mockMvc.perform(post("/competitions/wins/" + competitionId).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testDeleteCompetition() throws Exception {
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(delete("/competitions/delete/" + competitionId).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testDeleteCompetitionNonExistingUser() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(delete("/competitions/delete/" + competitionId).header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testDeleteNonExistingCompetition() throws Exception {
        long competitionId = 1111111111;

        this.mockMvc.perform(delete("/competitions/delete/" + competitionId).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testDeleteCompetitionFromOtherUser() throws Exception {
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(wout).get(0).getCompetitionId();

        this.mockMvc.perform(delete("/competitions/delete/" + competitionId).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testAddTrackingToCompetition() throws Exception {
        List<Coordinate> coordinates = new ArrayList<>();
        Coordinate coordinate = new Coordinate();
        coordinate.setLat(10);
        coordinate.setLon(10);
        coordinate.setSpeed(10);
        coordinate.setTime(10);
        coordinates.add(coordinate);
        Tracking testTracking = new Tracking(10, 10, 10, 10, coordinates);
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(post("/competitions/addTracking/" + competitionId)
                .header("token", tokenAlexander)
                .content(mapper.writeValueAsString(testTracking))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetGoalFromCompetition() throws Exception {
        long competitionId = this.userRepository.findUserByUsername("alexvr").getCompetitionsCreated().get(0).getCompetitionId();

        this.mockMvc.perform(get("/competitions/" + competitionId + "/getGoal").header("token", tokenAlexander)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetAllNonFinishedCompetitions() throws Exception {
        //Create competition
        Competition competition = new Competition(wout, goal1, "topicABC", "comp6");
        this.mockMvc.perform(post("/competitions/createCompetition").content(mapper.writeValueAsString(competition)).header("token", tokenWout).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        //Add runner to competition
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(wout).get(this.competitionRepository.findCompetitionByUserCreated(wout).size() - 1).getCompetitionId();

        this.mockMvc.perform(post("/competitions/running/" + competitionId).header("token", tokenJelle).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        //Get non-finished competitions (no winnner)
        this.mockMvc.perform(get("/competitions/getNonFinishedCompetitions").header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(competition.getName())))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(jelle.getUsername())));
    }

    @Test
    public void testGetAllNonFinishedCompetitionsFriends() throws Exception {
        //Add friend
        String username = "woutl";
        this.mockMvc.perform(put("/users/addFriend/" + username).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(put("/users/acceptFriend/" + alexander.getUsername()).header("token", tokenWout).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        //Create competition
        Competition competition = new Competition(wout, goal1, "topicABC", "comp6");
        this.mockMvc.perform(post("/competitions/createCompetition").content(mapper.writeValueAsString(competition)).header("token", tokenWout).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        //Add runner to competition
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(wout).get(this.competitionRepository.findCompetitionByUserCreated(wout).size() - 1).getCompetitionId();

        this.mockMvc.perform(post("/competitions/running/" + competitionId).header("token", tokenJelle).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        //Get non-finished competitions (no winnner)
        this.mockMvc.perform(get("/competitions/getNonFinishedCompetitionsFriends").header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(competition.getName())))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(jelle.getUsername())));
    }

    @After
    public void removeTestObjects() {
        this.competition2.setTrackings(new ArrayList<>());
        this.competition2.setUsersRun(new ArrayList<>());
        this.competition2.setUserWon(null);
        this.competition2.setUserCreated(null);
        this.competition2.setGoal(null);
        this.competitionRepository.save(competition2);

        this.alexander.setCompetitionsRun(new ArrayList<>());
        this.alexander.setCompetitionsCreated(new ArrayList<>());
        this.alexander.setCompetitionsWon(new ArrayList<>());
        this.alexander.setTrackings(new ArrayList<>());
        this.alexander.setFriendships(new ArrayList<>());
        this.userRepository.save(alexander);

        this.wout.setCompetitionsRun(new ArrayList<>());
        this.wout.setCompetitionsCreated(new ArrayList<>());
        this.wout.setCompetitionsWon(new ArrayList<>());
        this.wout.setTrackings(new ArrayList<>());
        this.wout.setFriendships(new ArrayList<>());
        this.userRepository.save(wout);

        this.jelle.setCompetitionsRun(new ArrayList<>());
        this.jelle.setCompetitionsCreated(new ArrayList<>());
        this.jelle.setCompetitionsWon(new ArrayList<>());
        this.jelle.setTrackings(new ArrayList<>());
        this.jelle.setFriendships(new ArrayList<>());
        this.userRepository.save(jelle);

        this.trackingAlex.setCompetition(null);
        this.trackingAlex.setUser(null);
        this.trackingAlex.setCoordinates(new ArrayList<>());
        this.trackingRepository.save(trackingAlex);

        this.trackingRepository.findAll().stream().forEach(tracking -> this.trackingRepository.delete(tracking.getTrackingId()));
        this.userRepository.findAll().stream().forEach(user -> this.userRepository.delete(user.getUserId()));
        this.competitionRepository.delete(competition2);
        this.goalRepository.findAll().stream().forEach(goal -> this.goalRepository.delete(goal.getGoalId()));
    }

}
