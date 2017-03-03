package be.kdg.runtracker.frontend.controllers.rest;

import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.persistence.api.UserRepository;
import com.auth0.jwt.JWT;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@Transactional
public class MQTTRestControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    private User jens;
    private String tokenJens;

    @Before
    public void setup() {
        tokenJens = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDUifQ.mmEMpKfNaBlkvd-mGpbDDeJOAxU9ASiq0F_mBMckrLw";
        this.jens = new User();
        this.jens.setAuthId(JWT.decode(tokenJens).getSubject());
        this.jens.setFirstname("Jens");
        this.jens.setLastname("Schadron");
        this.jens.setUsername("jenss");

        this.userRepository.save(jens);

        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetConfig() throws Exception {
        this.mockMvc.perform(get("/mqtt/getConfig").header("token", tokenJens).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetConfigWithCompId() throws Exception {
        this.mockMvc.perform(get("/mqtt/getConfig/1").header("token", tokenJens).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetConfigUnauthorized() throws Exception {
        String wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";

        this.mockMvc.perform(get("/mqtt/getConfig").header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @After
    public void deleteTestObjects() {
        this.userRepository.delete(jens.getUserId());
    }

}
