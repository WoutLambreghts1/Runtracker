package be.kdg.runtracker.backend.persistence;

/**
 * Created by Wout on 3/02/2017.
 */

import be.kdg.runtracker.backend.dom.profile.Gender;
import be.kdg.runtracker.backend.dom.profile.User;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
/**
 * A simple test to check the DB-connection, write, read, update & delete
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MySQLTest.class)
@ComponentScan("be.kdg.runtracker")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MySQLTest {
    @Value("Test")
    private String userFirstName;
    @Value("Tester")
    private String userLastName;
    @Value("Test123")
    private String username;
    @Value("fakeAuthId")
    private String authId;


    @Autowired
    private UserRepository userRepository;

    @Test
    public void aCreateUser()
    {

        User u = new User();
        u.setUsername(username);
        u.setAuthId(authId);
        u.setFirstname(userFirstName);
        u.setLastname(userLastName);
        userRepository.save(u);

        userRepository.save(u);

        assertEquals((userRepository.findUserByUsername(username).getFirstname() + userRepository.findUserByUsername(username).getLastname()), (u.getFirstname() + u.getLastname()));
    }



    @Test
    public void bUpdateUser()
    {
        User u = userRepository.findUserByUsername(username);
        u.setGender(Gender.MALE);
        userRepository.save(u);
        assertEquals(userRepository.findUserByUsername(username).getGender(), Gender.MALE);
    }

    @Test
    public void deleteUser()
    {
        userRepository.delete(userRepository.findUserByAuthId(authId).getUserId());
        assertNull(userRepository.findUserByAuthId(authId));
    }

}
