package be.kdg.runtracker.frontend.controllers.rest;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.persistence.UserRepository;
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
    private MockMvc mockMvc;
    private Gson gson;

    private User alexander;
    private User wout;
    private User jelle;
    private User stijn;
    private User jens;

    @Before
    public void setup() {
        this.alexander = new User();
        this.alexander.setAuthId("123");
        this.alexander.setFirstname("Alexander");
        this.alexander.setLastname("van Ravestyn");
        this.alexander.setUsername("alexvr");

        this.wout = new User();
        this.wout.setAuthId("234");
        this.wout.setFirstname("Wout");
        this.wout.setLastname("Lambreghts");
        this.wout.setUsername("woutl");

        this.jelle = new User();
        this.jelle.setAuthId("345");
        this.jelle.setFirstname("Jelle");
        this.jelle.setLastname("Mannaerts");
        this.jelle.setUsername("jellem");

        this.stijn = new User();
        this.stijn.setAuthId("456");
        this.stijn.setFirstname("Stijn");
        this.stijn.setLastname("Ergeerts");
        this.stijn.setUsername("stijne");

        this.jens = new User();
        this.jens.setAuthId("567");
        this.jens.setFirstname("Jens");
        this.jens.setLastname("Schadron");
        this.jens.setUsername("jenss");

        this.userRepository.save(alexander);
        this.userRepository.save(wout);
        this.userRepository.save(jelle);
        this.userRepository.save(stijn);
        this.userRepository.save(jens);

        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.gson = new Gson();
    }

    @Test
    public void testGetAllUsers() throws Exception {
        this.mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetNoUsers() throws Exception {
        this.userRepository.delete(userRepository.findUserByAuthId("123").getUserId());
        this.userRepository.delete(userRepository.findUserByAuthId("234").getUserId());
        this.userRepository.delete(userRepository.findUserByAuthId("345").getUserId());
        this.userRepository.delete(userRepository.findUserByAuthId("456").getUserId());
        this.userRepository.delete(userRepository.findUserByAuthId("567").getUserId());

        this.mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        this.userRepository.save(alexander);
        this.userRepository.save(wout);
        this.userRepository.save(jelle);
        this.userRepository.save(stijn);
        this.userRepository.save(jens);
    }

    @Test
    public void testGetUserByAuthId() throws Exception {
        String authId = "123";
        this.mockMvc.perform(get("/api/users/" + authId).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.authId", is(123)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserByWrongAuthId() throws Exception {
        String authId = "123123123";
        this.mockMvc.perform(get("/api/users/" + authId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateUser() throws Exception {
        User testUser = new User();
        testUser.setAuthId("100");
        testUser.setFirstname("Test");
        testUser.setLastname("User");
        testUser.setUsername("TestUser");

        this.mockMvc.perform(post("/api/users").content(gson.toJson(testUser)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.userRepository.delete(userRepository.findUserByAuthId("100").getUserId());
    }

    @Test
    public void testCreateAlreadyExistingUser() throws Exception {
        User testUser = new User();
        testUser.setAuthId("123");
        testUser.setFirstname("Test");
        testUser.setLastname("User");
        testUser.setUsername("TestUser");

        this.mockMvc.perform(post("/api/users").content(gson.toJson(testUser)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateUser() throws Exception {
        User updateAlexander = userRepository.findUserByAuthId("123");
        updateAlexander.setFirstname("Alex");

        this.mockMvc.perform(put("/api/users/" + updateAlexander.getAuthId()).content(gson.toJson(updateAlexander)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("Alex")));
    }

    @Test
    public void testUpdateNonExistingUser() throws Exception {
        User testUser = new User();
        testUser.setAuthId("100");
        testUser.setFirstname("Test");
        testUser.setLastname("User");
        testUser.setUsername("TestUser");

        this.mockMvc.perform(put("/api/users/" + testUser.getAuthId()).content(gson.toJson(testUser)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser() throws Exception {
        String authId = "123";
        this.mockMvc.perform(delete("/api/users/" + authId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteNonExistingUser() throws Exception {
        String authId = "100";
        this.mockMvc.perform(delete("/api/users/" + authId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @After
    public void removeTestUsers() {
        if (this.userRepository.findUserByAuthId("123") != null) {
            this.userRepository.delete(alexander.getUserId());
        }
        if (!this.userRepository.findAll().isEmpty()) {
            this.userRepository.delete(wout.getUserId());
            this.userRepository.delete(jelle.getUserId());
            this.userRepository.delete(stijn.getUserId());
            this.userRepository.delete(jens.getUserId());
        }
    }

}
