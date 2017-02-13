package be.kdg.runtracker.frontend.controllers.rest;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.competition.CompetitionType;
import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.persistence.CompetitionRepository;
import be.kdg.runtracker.backend.persistence.GoalRepository;
import be.kdg.runtracker.backend.persistence.UserRepository;
import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private User alexander;
    private User wout;
    private User jelle;
    private String token1;
    private String token2;
    private String token3;

    private Competition competition1;
    private Competition competition2;
    private Goal goal1;
    private Goal goal2;

    @Before
    public void setup() {
        token1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";
        this.alexander = new User();
        this.alexander.setAuthId(JWT.decode(token1).getSubject());
        this.alexander.setFirstname("Alexander");
        this.alexander.setLastname("van Ravestyn");
        this.alexander.setUsername("alexvr");

        token2 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDIifQ.gbIlCFTC5tyyLg8UqvxcoUUmhgUTdstZnHWNzEO1jVM";
        this.wout = new User();
        this.wout.setAuthId(JWT.decode(token2).getSubject());
        this.wout.setFirstname("Wout");
        this.wout.setLastname("Lambreghts");
        this.wout.setUsername("woutl");

        token3 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDMifQ._Am34giuNzroHLmI482DV46DFq7UuNTdpzQ01aQdyio";
        this.jelle = new User();
        this.jelle.setAuthId(JWT.decode(token3).getSubject());
        this.jelle.setFirstname("Jelle");
        this.jelle.setLastname("Mannaerts");
        this.jelle.setUsername("jellem");

        this.goal1 = new Goal();
        this.goal1.setName("Goal1");
        this.goal1.setDistance(10);

        this.competition1 = new Competition(alexander, goal1, CompetitionType.NOT_REALTIME, 10, 5);
        this.competition1.addRunner(alexander);
        this.alexander.addCompetitionsCreated(competition1);
        this.alexander.addCompetitionsRan(competition1);
        this.competition1.addRunner(wout);
        this.wout.addCompetitionsRan(competition1);
        this.competition1.setUserWon(wout);
        this.wout.addCompetitionsWon(competition1);

        this.goal2 = new Goal();
        this.goal2.setName("Goal2");
        this.goal2.setDistance(10);

        this.competition2 = new Competition(wout, goal2, CompetitionType.NOT_REALTIME, 10, 5);
        this.wout.addCompetitionsCreated(competition2);
        this.competition2.addRunner(wout);

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
        this.mockMvc.perform(get("/competitions/").header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetNoCompetitions() throws Exception {
        removeCompetitions();

        this.mockMvc.perform(get("/competitions/").header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        addCompetitions();
    }

    private void removeCompetitions() {
        List<User> users = this.userRepository.findAll();
        for (User user : users) {
            user.setCompetitionsCreated(new ArrayList<>());
            user.setCompetitionsWon(new ArrayList<>());
            user.setCompetitionsRun(new ArrayList<>());
        }
        users.stream().forEach(user -> this.userRepository.save(user));
        this.competitionRepository.findAll().stream().forEach(competition -> this.competitionRepository.delete(competition.getCompetitionId()));
    }

    private void addCompetitions() {
        this.competition1 = new Competition(alexander, goal1, CompetitionType.NOT_REALTIME, 10, 5);
        this.competition1.addRunner(alexander);
        this.competition1.addRunner(wout);
        this.competition1.addRunner(jelle);
        this.competition1.setUserWon(wout);
        this.competition2 = new Competition(wout, goal2, CompetitionType.NOT_REALTIME, 10, 5);
        this.competition2.addRunner(wout);

        this.competitionRepository.save(competition1);
        this.competitionRepository.save(competition2);
    }

    @Test
    public void getAllCompetitionsUnauthorized() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";

        this.mockMvc.perform(get("/competitions/").header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void getAllRanCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getRanCompetitions").header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getAllRanCompetitionsUnauthorized() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";

        this.mockMvc.perform(get("/competitions/getRanCompetitions").header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void getNoRanCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getRanCompetitions").header("token", token3).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void getAllCreatedCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getCreatedCompetitions").header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getAllCreatedCompetitionsUnauthorized() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";

        this.mockMvc.perform(get("/competitions/getCreatedCompetitions").header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void getNoCreatedCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getCreatedCompetitions").header("token", token3).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void getAllWonCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getWonCompetitions").header("token", token2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getAllWonCompetitionsUnauthorized() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";

        this.mockMvc.perform(get("/competitions/getWonCompetitions").header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void getNoWonCompetitions() throws Exception {
        this.mockMvc.perform(get("/competitions/getWonCompetitions").header("token", token3).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void createCompetition() throws Exception {
        Competition competition = new Competition(alexander, goal1, CompetitionType.REALTIME, 1, 3);

        this.mockMvc.perform(post("/competitions/createCompetition").content(mapper.writeValueAsString(competition)).header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void createCompetitionUnauthorized() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";
        Competition competition = new Competition(alexander, goal1, CompetitionType.REALTIME, 1, 3);

        this.mockMvc.perform(post("/competitions/createCompetition").content(mapper.writeValueAsString(competition)).header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testUserRunsInCompetition() throws Exception {
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(post("/competitions/running/" + competitionId).header("token", token3).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void testNonExistingUserRunsInCompetition() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(post("/competitions/running/" + competitionId).header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUserRunsInNonExistingCompetition() throws Exception {
        long competitionId = 1;

        this.mockMvc.perform(post("/competitions/running/" + competitionId).header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUserWinsCompetition() throws Exception {
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(post("/competitions/wins/" + competitionId).header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void testNonExistingUserWinsCompetition() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(post("/competitions/wins/" + competitionId).header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUserWinsNonExistingCompetition() throws Exception {
        long competitionId = 1;

        this.mockMvc.perform(post("/competitions/wins/" + competitionId).header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testDeleteCompetition() throws Exception {
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(delete("/competitions/delete/" + competitionId).header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testDeleteCompetitionNonExistingUser() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(alexander).get(0).getCompetitionId();

        this.mockMvc.perform(delete("/competitions/delete/" + competitionId).header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testDeleteNonExistingCompetition() throws Exception {
        long competitionId = 1111111111;

        this.mockMvc.perform(delete("/competitions/delete/" + competitionId).header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testDeleteCompetitionFromOtherUser() throws Exception {
        long competitionId = this.competitionRepository.findCompetitionByUserCreated(wout).get(0).getCompetitionId();

        this.mockMvc.perform(delete("/competitions/delete/" + competitionId).header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @After
    public void removeTestObjects() {
        List<User> users = this.userRepository.findAll();
        for (User user : users) {
            user.setCompetitionsRun(new ArrayList<>());
            user.setCompetitionsWon(new ArrayList<>());
            user.setCompetitionsCreated(new ArrayList<>());
            this.userRepository.save(user);
        }

        List<Competition> competitions = this.competitionRepository.findAll();
        for (Competition competition : competitions) {
            competition.setUserWon(null);
            competition.setUsersRun(new ArrayList<>());
            competition.setUserCreated(null);
            competition.setGoal(null);
            this.competitionRepository.save(competition);
        }

        this.userRepository.findAll().stream().forEach(user -> this.userRepository.delete(user.getUserId()));
        this.competitionRepository.findAll().stream().forEach(competition -> this.competitionRepository.delete(competition.getCompetitionId()));
        this.goalRepository.findAll().stream().forEach(goal -> this.goalRepository.delete(goal.getGoalId()));
    }

}
