package be.kdg.runtracker.frontend.resources.resources;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.profile.Gender;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class UserResource extends ResourceSupport implements Serializable {

    private Long userId;
    private String username;
    private String authId;
    private String firstname;
    private String lastname;
    private Gender gender;
    private String city;
    private Date birthday;

    private double maxSpeed;
    private double avgSpeed;
    private long maxDistance;
    private double avgDistance;
    private long totalDistance;
    private boolean ranTenKm;
    private boolean ranTwentyKm;
    private boolean ranMarathon;
    private int nrOfCompetitionsWon;

    /*
    private List<User> friends;
    private List<Competition> competitionsCreated;
    private List<Tracking> trackings;
    private List<Competition> competitionsWon;
    private List<Competition> competitionsRun;
    */

}
