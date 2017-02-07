package be.kdg.runtracker.backend.persistence;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.competition.CompetitionType;
import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.Gender;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.scheduling.Event;
import be.kdg.runtracker.backend.dom.scheduling.Schedule;
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

import static org.junit.Assert.assertTrue;


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
    private ScheduleRepository scheduleRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private EventRepository eventRepository;

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

        //SCHEDULE
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < 7; i+=2) {
            Event e = new Event(i,"Run 5km","Run 5km within 40 minutes.",60*40);
            events.add(e);
            eventRepository.save(e);
        }
        scheduleRepository.save(new Schedule("TestSchedule",4,events));
        u.setSchedule(scheduleRepository.findOne(scheduleRepository.count()-1));
        userRepository.save(u);

        //TRACKING
        List<Tracking> trackings = (u.getTrackings() == null)?(new ArrayList<>()):u.getTrackings();
        Tracking tracking = new Tracking(60*20,5000,15,14.2);
        trackingRepository.save(tracking);
        trackings.add(tracking);
        u.setTrackings(trackings);

        userRepository.save(u);

        assertTrue((userRepository.findUserByUsername(username).getFirstname()+userRepository.findUserByUsername(username).getLastname()).equals((u.getFirstname()+u.getLastname())));
    }

    @Test
    public void bUpdateUser()
    {
        User u = userRepository.findUserByUsername(username);
        u.setGender(Gender.MALE);
        userRepository.save(u);
        assertTrue(userRepository.findUserByUsername(username).getGender().equals(Gender.MALE));

        Tracking tracking = u.getTrackings().get(u.getTrackings().size() - 1);
        tracking.setTotalDuration(60 * 60);
        tracking.setTotalDistance(10000);
        tracking.setMaxSpeed(12.5);
        tracking.setAvgSpeed(10);
        trackingRepository.save(tracking);
        assertTrue(trackingRepository.findOne(tracking.getTracking_id()).getAvgSpeed() == tracking.getAvgSpeed());

    }



    @Test
    public void delete()
    {
        competitionRepository.delete(userRepository.findUserByUsername(username).getCompetitionsCreated());
        userRepository.delete(userRepository.findUserByAuthId(authId).getUser_id());
        scheduleRepository.delete(scheduleRepository.findScheduleByName("TestSchedule"));
        goalRepository.delete(goalRepository.findGoalByName(goalname));
        trackingRepository.delete(trackingRepository.findAll().get(trackingRepository.findAll().size() - 1));
        assertTrue(userRepository.findUserByAuthId(authId) == null);
    }




}
