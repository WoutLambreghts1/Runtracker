package be.kdg.runtracker;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.persistence.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Wout on 3/02/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = be.kdg.runtracker.RuntrackerApplicationTests.class)
@ComponentScan("be.kdg.runtracker")
public class MySQLTest {
    @Value("Jan")
    private String userFirstName;

    @Value("Jansens")
    private String userLastName;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void createUserTest()
    {
        User testUser = new User();
        testUser.setUsername("Auth0ID");
        testUser.setPassword("Auth0Secret");
        testUser.setFirstname(userFirstName);
        testUser.setLastname(userLastName);
        userRepository.save(testUser);

    }

    @Test
    public void deleterUserTest()
    {
        userRepository.deleteAll();
    }
}
