package be.kdg.runtracker.backend.persistence;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.competition.CompetitionType;
import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.Gender;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;


/**
 * Created by Wout on 3/02/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MySQLTest.class)
@ComponentScan("be.kdg.runtracker")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MySQLTest {
    @Value("Jan")
    private String userFirstName;
    @Value("Jansens")
    private String userLastName;
    @Value("Jan_Jansens1")
    private String username;
    @Value("123")
    private long authId;

    @Value("TestGoal")
    private String goalname;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private TrackingRepository trackingRepository;


    @Test
    public void aCreate()
    {

        User u = new User();
        u.setUsername(username);
        u.setAuthId(authId);
        u.setFirstname(userFirstName);
        u.setLastname(userLastName);
        userRepository.save(u);

        //COMPETITION
        Goal g =  new Goal(goalname,1000);
        goalRepository.save(g);
        competitionRepository.save(new Competition(u,g, CompetitionType.REALTIME, 0,4));

        //TRACKING
        List<Tracking> trackings = (u.getTrackings() == null)?(new ArrayList<>()):u.getTrackings();
        Tracking tracking = new Tracking(60*20,5000,15,14.2);
        trackingRepository.save(tracking);
        trackings.add(tracking);
        u.setTrackings(trackings);

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

        Tracking tracking = u.getTrackings().get(u.getTrackings().size() - 1);
        tracking.setTotalDuration(60 * 60);
        tracking.setTotalDistance(10000);
        tracking.setMaxSpeed(12.5);
        tracking.setAvgSpeed(10);
        trackingRepository.save(tracking);
        assertEquals(trackingRepository.findOne(tracking.getTracking_id()).getAvgSpeed(), tracking.getAvgSpeed());

    }



    @Test
    public void delete()
    {
        competitionRepository.delete(userRepository.findUserByUsername(username).getCompetitionsCreated());
        userRepository.delete(userRepository.findUserByAuthId(authId).getUser_id());
        goalRepository.delete(goalRepository.findGoalByName(goalname));
        trackingRepository.delete(trackingRepository.findAll().get(trackingRepository.findAll().size() - 1));
        assertNull(userRepository.findUserByAuthId(authId));
    }




}
