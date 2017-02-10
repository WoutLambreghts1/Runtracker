package be.kdg.runtracker.frontend.controllers.rest;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.persistence.TrackingRepository;
import be.kdg.runtracker.backend.persistence.UserRepository;
import com.auth0.jwt.JWT;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@Transactional
public class TrackingRestControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrackingRepository trackingRepository;
    private MockMvc mockMvc;
    private Gson gson;

    private User alexander;
    private Tracking tracking1;
    private Tracking tracking2;

    private String token;

    @Before
    public void setup() {
        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";
        this.alexander = new User();
        this.alexander.setAuthId(JWT.decode(token).getSubject());
        System.out.println(this.alexander.getAuthId());
        this.alexander.setFirstname("Alexander");
        this.alexander.setLastname("van Ravestyn");
        this.alexander.setUsername("alexvr");

        this.tracking1 = new Tracking(10, 10, 10, 10);
        this.tracking2 = new Tracking(10, 10, 10, 10);

        this.alexander.addTracking(tracking1);
        this.alexander.addTracking(tracking2);
        this.tracking1.setUser(alexander);
        this.tracking2.setUser(alexander);

        this.userRepository.save(alexander);
        this.trackingRepository.save(tracking1);
        this.trackingRepository.save(tracking2);

        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.gson = new Gson();
    }

    @Test
    public void testGetAllTrackingsForUser() throws Exception {
        this.mockMvc.perform(get("/trackings/getAllTrackings").header("token",token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @After
    public void deleteUserAndTrackings() {
        trackingRepository.delete(tracking1.getTrackingId());
        trackingRepository.delete(tracking2.getTrackingId());
        userRepository.delete(alexander.getUserId());
    }

}
