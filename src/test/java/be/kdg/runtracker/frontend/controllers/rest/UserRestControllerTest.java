package be.kdg.runtracker.frontend.controllers.rest;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.persistence.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


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

    @Before
    public void setup() {

        User alexander = new User();
        alexander.setAuthId(123);
        alexander.setFirstname("Alexander");
        alexander.setLastname("van Ravestyn");
        alexander.setUsername("alexvr");

        User wout = new User();
        wout.setAuthId(234);
        wout.setFirstname("Wout");
        wout.setLastname("Lambreghts");
        wout.setUsername("woutl");

        User jelle = new User();
        jelle.setAuthId(345);
        jelle.setFirstname("Jelle");
        jelle.setLastname("Mannaerts");
        jelle.setUsername("jellem");

        User stijn = new User();
        stijn.setAuthId(456);
        stijn.setFirstname("Stijn");
        stijn.setLastname("Ergeerts");
        stijn.setUsername("stijne");

        User jens = new User();
        jens.setAuthId(567);
        jens.setFirstname("Jens");
        jens.setLastname("Schadron");
        jens.setUsername("jenss");

        userRepository.save(alexander);
        userRepository.save(wout);
        userRepository.save(jelle);
        userRepository.save(stijn);
        userRepository.save(jens);

        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

    }

    @Test
    public void getAllUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @After
    public void removeTestUsers() {

        userRepository.delete(userRepository.findUserByAuthId(123).getUser_id());
        userRepository.delete(userRepository.findUserByAuthId(234).getUser_id());
        userRepository.delete(userRepository.findUserByAuthId(345).getUser_id());
        userRepository.delete(userRepository.findUserByAuthId(456).getUser_id());
        userRepository.delete(userRepository.findUserByAuthId(567).getUser_id());

    }

}
