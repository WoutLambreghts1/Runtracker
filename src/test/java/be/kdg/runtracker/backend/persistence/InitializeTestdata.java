package be.kdg.runtracker.backend.persistence;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.competition.CompetitionType;
import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.Gender;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Coordinate;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Wout on 6/02/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InitializeTestdata.class)
@ComponentScan("be.kdg.runtracker")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InitializeTestdata {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    GoalRepository goalRepository;
    @Autowired
    TrackingRepository trackingRepository;
    @Autowired
    CoordinatesRepositoryImpl coordinatesRepositoryImpl;

    //USERNAMES
    @Value("AlexVr")
    private String usernameAlex;
    @Value("JelleM")
    private String usernameJelle;
    @Value("JensS")
    private String usernameJens;
    @Value("StijnE")
    private String usernameStijn;
    @Value("WoutL")
    private String usernameWout;


    //CREATE USERS (NOT SYNCED WITH AUTH0) WITH PRESTATIONS & FRIENDSHIPS
    @Test
    public void aCreateUsers() {
        //CREATE USERS
        List<User> users = new ArrayList<>();

        User alex = new User();
        alex.setFirstname("Alexander");
        alex.setLastname("van Ravestyn");
        alex.setEmail("alexander.vanravestyn@student.kdg.be");
        alex.setGender(Gender.MALE);
        alex.setUsername(usernameAlex);
        users.add(alex);

        User jens = new User();
        jens.setFirstname("Jens");
        jens.setLastname("Schadron");
        jens.setEmail("jens.schadron@student.kdg.be");
        jens.setGender(Gender.MALE);
        jens.setUsername(usernameJens);
        users.add(jens);

        User jelle = new User();
        jelle.setFirstname("Jelle");
        jelle.setLastname("Mannaerts");
        jelle.setEmail("jelle.mannaerts@student.kdg.be");
        jelle.setGender(Gender.MALE);
        jelle.setUsername(usernameJelle);
        users.add(jelle);

        User stijn = new User();
        stijn.setFirstname("Stijn");
        stijn.setLastname("Ergeerts");
        stijn.setEmail("stijn.ergeerts@student.kdg.be");
        stijn.setGender(Gender.MALE);
        stijn.setUsername(usernameStijn);
        users.add(stijn);

        User wout = new User();
        wout.setFirstname("Wout");
        wout.setLastname("Lambreghts");
        wout.setEmail("wout.lambreghts@student.kdg.be");
        wout.setGender(Gender.MALE);
        wout.setUsername(usernameWout);
        users.add(wout);


        //SAVE IF DATA DOESN'T EXISTS
        for (int i = 0; i < users.size(); i++) {
            if (userRepository.findUserByUsername(users.get(i).getUsername()) == null) {
                userRepository.save(users.get(i));
            }
        }

        //CREATE FRIENDS
        for (int i = 0; i < users.size(); i++) {
            if (userRepository.findUserByUsername(users.get(i).getUsername()) != null) {
                if(i < users.size() - 1){
                    users.get(i).addFriend(users.get(i+1));
                }
                userRepository.save(users.get(i));
            }
        }

    }


    //CREATE COMPETITIONS
    @Test
    public void bCreateCompetitions() {
        //CREATE GOALS
        List<Goal> goals = new ArrayList<>();
        Goal goal1 = new Goal("Run 100m",100);
        Goal goal2 = new Goal("Run 200m",200);
        Goal goal3 = new Goal("Run 500m",500);
        Goal goal4 = new Goal("Run 1000m",1000);
        Goal goal5 = new Goal("Run 2000m",2000);
        goals.add(goal1);
        goals.add(goal2);
        goals.add(goal3);
        goals.add(goal4);
        goals.add(goal5);

        for (int i = 0; i < goals.size(); i++) {
            if(goalRepository.findGoalByName(goals.get(i).getName())==null){
                goalRepository.save(goals.get(i));
            }else {
                goals.get(i).setId(goalRepository.findGoalByName(goals.get(i).getName()).getId());
            }
        }


        //CREATE COMPETITIONS
        List<Competition> competitions = new ArrayList<>();
        Competition competition1 = new Competition(userRepository.findUserByUsername(usernameAlex),goal3, CompetitionType.NOT_REALTIME,7,5);
        Competition competition2 = new Competition(userRepository.findUserByUsername(usernameWout),goal4, CompetitionType.REALTIME,0,2);

        //ADD RUNNERS
        competition1.addRunner(userRepository.findUserByUsername(usernameWout));
        competition1.addRunner(userRepository.findUserByUsername(usernameJelle));
        competition2.addRunner(userRepository.findUserByUsername(usernameJelle));
        competition2.addRunner(userRepository.findUserByUsername(usernameAlex));
        competition2.addRunner(userRepository.findUserByUsername(usernameJens));;



        //ADD TRACKINGS
        List<Tracking> trackings = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Tracking t = new Tracking(i*60*15,i*3000,16,13.8);
            t.setTotalDuration(i * 60 * 15);
            trackings.add(t);
            if(i==0){
                t.setUser(userRepository.findUserByUsername(usernameAlex));
            }else {
                t.setUser(userRepository.findUserByUsername(usernameJelle));
            }
            competition1.addTracking(t);
        }


        competitions.add(competition1);
        competitions.add(competition2);

        for (int i = 0; i < competitions.size(); i++) {

            //CHECK MATCHING COMPETITIONS IN DB
            Competition cc = competitions.get(i);
            boolean match = false;
            if(competitionRepository.findAll().size() == 0){
                match =false;
            }else {
                for (int j = 0; j < competitionRepository.findAll().size(); j++) {
                    if (competitionRepository.findAll().get(j).getCompetitionType().equals(cc.getCompetitionType()) && competitionRepository.findAll().get(j).getUserCreated().getUsername().equals(cc.getUserCreated().getUsername()) &&
                            competitionRepository.findAll().get(j).getGoal().getDistance() == cc.getGoal().getDistance()){
                        match=true;
                    };
                }
            }

            if(!match){
                //SAVE ALL TRACKINGS & COMPETITIONS
                trackingRepository.save(competitions.get(i).getTrackings());
                competitionRepository.save(competitions.get(i));

                //CREATE COORDINATES FOR EACH TRACKING
                if(competitions.get(i).getTrackings() != null){
                    for (int j = 0; j < competitions.get(i).getTrackings().size(); j++) {
                        Random r = new Random();
                        List<Coordinate> coordinates = new ArrayList<>();
                        long trackingID = competitionRepository.findAll().get(competitionRepository.findAll().size() - 1).getTrackings().get(j).getTracking_id();
                        for (int l = 0; l < 10; l++) {
                            coordinates.add(new Coordinate(r.nextDouble() * r.nextInt(50),r.nextDouble() * r.nextInt(50), LocalTime.now().plusSeconds(i),trackingID,11+r.nextDouble()));
                        }
                        coordinatesRepositoryImpl.createCoordinatesCollection(coordinates.get(i).getTrackingID(),coordinates);
                    }
                }

            }

        }

    }

    //DELETE EVERYTHING
    /*
    @Test
    public void cDelete() {
        //DELETE USERS

        User u1 = userRepository.findUserByUsername(usernameStijn);
        u1.getFriends().clear();
        userRepository.save(u1);

        User u2 = userRepository.findUserByUsername(usernameAlex);
        u2.getFriends().clear();
        userRepository.save(u2);
        User u3 = userRepository.findUserByUsername(usernameJelle);
        u3.getFriends().clear();
        userRepository.save(u3);
        User u4 = userRepository.findUserByUsername(usernameWout);
        u4.getFriends().clear();
        userRepository.save(u4);
        User u5 = userRepository.findUserByUsername(usernameJens);
        u5.getFriends().clear();
        userRepository.save(u5);

        userRepository.delete(u1);
        userRepository.delete(u2);
        userRepository.delete(u3);
        userRepository.delete(u4);
        userRepository.delete(u5);


        //DELETE COMPETITIONS
        for (int i = 0; i < competitionRepository.findAll().size(); i++) {
            if(competitionRepository.findAll().get(i).getUserCreated() == null){
                for (int j = 0; j < competitionRepository.findAll().get(i).getTrackings().size(); j++) {
                    coordinatesRepositoryImpl.deleteCoordinatesCollection(competitionRepository.findAll().get(i).getTrackings().get(j).getTracking_id());
                }

                competitionRepository.delete(competitionRepository.findAll().get(i));
            }
        }

        //DELETE GOALS
        goalRepository.delete(goalRepository.findGoalByDistance(100));
        goalRepository.delete(goalRepository.findGoalByDistance(200));
        goalRepository.delete(goalRepository.findGoalByDistance(500));
        goalRepository.delete(goalRepository.findGoalByDistance(1000));
        goalRepository.delete(goalRepository.findGoalByDistance(2000));



    }
    */



}




