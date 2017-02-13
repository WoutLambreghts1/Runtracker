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
import org.springframework.web.context.WebApplicationContext;

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
public class UserRestControllerTest {

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
    private User stijn;
    private User jens;

    private Goal goalAlex;
    private Competition competitionAlex;

    private String token1;
    private String token2;
    private String token3;
    private String token4;
    private String token5;

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

        token4 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDQifQ.HmBSbppuUKbIo2M4ZKgfbcBzrmIb2nE9AThoTfJXCaY";
        this.stijn = new User();
        this.stijn.setAuthId(JWT.decode(token4).getSubject());
        this.stijn.setFirstname("Stijn");
        this.stijn.setLastname("Ergeerts");
        this.stijn.setUsername("stijne");

        token5 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDUifQ.mmEMpKfNaBlkvd-mGpbDDeJOAxU9ASiq0F_mBMckrLw";
        this.jens = new User();
        this.jens.setAuthId(JWT.decode(token5).getSubject());
        this.jens.setFirstname("Jens");
        this.jens.setLastname("Schadron");
        this.jens.setUsername("jenss");

        this.goalAlex = new Goal();
        this.goalAlex.setName("Goal1");
        this.goalAlex.setDistance(10);

        this.competitionAlex = new Competition(alexander, goalAlex, CompetitionType.NOT_REALTIME, 10, 5);
        this.competitionAlex.addRunner(alexander);
        this.alexander.addCompetitionsCreated(competitionAlex);
        this.alexander.addCompetitionsRan(competitionAlex);
        this.competitionAlex.addRunner(wout);
        this.wout.addCompetitionsRan(competitionAlex);
        this.competitionAlex.setUserWon(wout);
        this.wout.addCompetitionsWon(competitionAlex);

        this.competitionRepository.save(competitionAlex);
        this.goalRepository.save(goalAlex);

        this.userRepository.save(alexander);
        this.userRepository.save(wout);
        this.userRepository.save(jelle);
        this.userRepository.save(stijn);
        this.userRepository.save(jens);

        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.mapper = new ObjectMapper();
    }

    @Test
    public void testGetAllUsers() throws Exception {
        this.mockMvc.perform(get("/users/").header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetAllUsersUnauthorized() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";

        this.mockMvc.perform(get("/users/").header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    private void removeAllUsers() {
        this.userRepository.findAll().stream().forEach(user -> this.userRepository.delete(user.getUserId()));
    }

    private void addAllUsers() {
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

        token4 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDQifQ.HmBSbppuUKbIo2M4ZKgfbcBzrmIb2nE9AThoTfJXCaY";
        this.stijn = new User();
        this.stijn.setAuthId(JWT.decode(token4).getSubject());
        this.stijn.setFirstname("Stijn");
        this.stijn.setLastname("Ergeerts");
        this.stijn.setUsername("stijne");

        token5 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDUifQ.mmEMpKfNaBlkvd-mGpbDDeJOAxU9ASiq0F_mBMckrLw";
        this.jens = new User();
        this.jens.setAuthId(JWT.decode(token5).getSubject());
        this.jens.setFirstname("Jens");
        this.jens.setLastname("Schadron");
        this.jens.setUsername("jenss");

        this.userRepository.save(alexander);
        this.userRepository.save(wout);
        this.userRepository.save(jelle);
        this.userRepository.save(stijn);
        this.userRepository.save(jens);

        this.mapper = new ObjectMapper();
    }

    @Test
    public void testGetUser() throws Exception {
        String authId = JWT.decode(token1).getSubject();
        this.mockMvc.perform(get("/users/getUser").header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.authId", is(authId)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetNonExistingUser() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";
        this.mockMvc.perform(get("/users/getUser").header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateUser() throws Exception {
        String testToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";
        User testUser = new User();
        testUser.setAuthId(JWT.decode(testToken).getSubject());
        testUser.setFirstname("Test");
        testUser.setLastname("User");
        testUser.setUsername("TestUser");

        this.mockMvc.perform(post("/users/createUser").content(mapper.writeValueAsString(testUser)).header("token", testToken).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        this.userRepository.delete(userRepository.findUserByAuthId(JWT.decode(testToken).getSubject()).getUserId());
    }

    @Test
    public void testCreateAlreadyExistingUser() throws Exception {
        User testUser = new User();
        testUser.setAuthId(JWT.decode(token1).getSubject());
        testUser.setFirstname("Test");
        testUser.setLastname("User");
        testUser.setUsername("TestUser");

        this.mockMvc.perform(post("/users/createUser").header("token", token1).content(mapper.writeValueAsString(testUser)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateUser() throws Exception {
        User updateAlexander = userRepository.findUserByUsername("alexvr");
        updateAlexander.setFirstname("Alex");

        this.mockMvc.perform(put("/users/updateUser").header("token", token1).content(mapper.writeValueAsString(updateAlexander)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("Alex")));
    }

    @Test
    public void testUpdateNonExistingUser() throws Exception {
        String testToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";
        User testUser = new User();
        testUser.setAuthId("100");
        testUser.setFirstname("Test");
        testUser.setLastname("User");
        testUser.setUsername("TestUser");

        this.mockMvc.perform(put("/users/updateUser").header("token", testToken).content(mapper.writeValueAsString(testUser)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser() throws Exception {
        this.mockMvc.perform(delete("/users/deleteUser").header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        this.alexander = new User();
        this.alexander.setAuthId(JWT.decode(token1).getSubject());
        this.alexander.setFirstname("Alexander");
        this.alexander.setLastname("van Ravestyn");
        this.alexander.setUsername("alexvr");
        this.userRepository.save(alexander);
    }

    @Test
    public void testDeleteNonExistingUser() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";
        this.mockMvc.perform(delete("/users/deleteUser").header("token", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testBefriendOtherUser() throws Exception {
        String username = "woutl";
        this.mockMvc.perform(put("/users/addFriend/" + username).header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testBefriendSelf() throws Exception {
        String username = "alexvr";
        this.mockMvc.perform(put("/users/addFriend/" + username).header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testBefriendNonExistingUser() throws Exception {
        String username = "testuser";
        this.mockMvc.perform(put("/users/addFriend/" + username).header("token", token1).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void checkAvailableUsername() throws Exception {
        String username = "testuser";
        this.mockMvc.perform(get("/users/checkUsername/" + username).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void checkNonAvailableUsername() throws Exception {
        String username = "alexvr";
        this.mockMvc.perform(get("/users/checkUsername/" + username).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @After
    public void removeTestUsers() {
        this.alexander.setCompetitionsCreated(new ArrayList<>());
        this.alexander.setCompetitionsRun(new ArrayList<>());
        this.alexander.setCompetitionsWon(new ArrayList<>());
        this.userRepository.save(alexander);

        this.wout.setCompetitionsCreated(new ArrayList<>());
        this.wout.setCompetitionsRun(new ArrayList<>());
        this.wout.setCompetitionsWon(new ArrayList<>());
        this.userRepository.save(wout);

        this.userRepository.findAll().stream().forEach(user -> this.userRepository.delete(user.getUserId()));
    }

}
