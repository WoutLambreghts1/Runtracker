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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Wout on 3/02/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = be.kdg.runtracker.RuntrackerApplicationTests.class)
@ComponentScan("be.kdg.runtracker")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MySQLTest {
    @Value("Jan")
    private String userFirstName;
    @Value("Jansens")
    private String userLastName;
    @Value("123456789")
    private String username;
    @Value("hash")
    private String password;

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
        u.setPassword(password);
        u.setFirstname(userFirstName);
        u.setLastname(userLastName);
        userRepository.save(u);

        //COMPETITION
        Goal g =  new Goal("Run 100m",100);
        goalRepository.save(g);
        competitionRepository.save(new Competition(u,g, CompetitionType.REALTIME));

        //SCHEDULE
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < 7; i+=2) {
            Event e = new Event(i,"Run 5km","Run 5km within 40 minutes.",60*40);
            events.add(e);
            eventRepository.save(e);
        }
        scheduleRepository.save(new Schedule("Beginner",4,events));
        u.setSchedule(scheduleRepository.findOne(scheduleRepository.count()-1));
        userRepository.save(u);

        //TRACKING
        List<Tracking> trackings = (u.getTrackings() == null)?(new ArrayList<>()):u.getTrackings();
        Tracking tracking = new Tracking();
        trackingRepository.save(tracking);
        trackings.add(tracking);
        u.setTrackings(trackings);

        userRepository.save(u);
    }

    @Test
    public void cUpdateUser()
    {
        User u = userRepository.findUserByUsername(username);
        u.setGender(Gender.MALE);
        userRepository.save(u);
        assertThat(userRepository.findUserByUsername(username).getGender().equals(Gender.MALE));

        Tracking tracking = u.getTrackings().get(u.getTrackings().size() - 1);
        tracking.setTotalDuration(60 * 60);
        tracking.setTotalDistance(10000);
        tracking.setMaxSpeed(12.5);
        tracking.setAvgSpeed(10);
        trackingRepository.save(tracking);
        assertThat(trackingRepository.findOne(tracking.getTracking_id()).equals(tracking));

    }



    @Test
    public void delete()
    {
        competitionRepository.delete(userRepository.findUserByUsername(username).getCompetitionsCreated());
        userRepository.delete(userRepository.findUserByUsername(username).getUser_id());
    }




}
