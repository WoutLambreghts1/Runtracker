package be.kdg.runtracker.frontend.dto;

import be.kdg.runtracker.backend.dom.profile.Gender;
import be.kdg.runtracker.backend.dom.profile.User;

import java.sql.Date;

public class ShortUser {

    private Long userId;
    private String username;
    private String authId;
    private String firstname;
    private String lastname;
    private Gender gender;
    private String city;
    private Date birthday;
    private boolean online;

    private double maxSpeed;
    private double avgSpeed;
    private long maxDistance;
    private double avgDistance;
    private long totalDistance;
    private int nrOfCompetitionsWon;

    public ShortUser(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.authId = user.getAuthId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.gender = user.getGender();
        this.city = user.getCity();
        this.birthday = user.getBirthday();

        this.maxSpeed = user.getMaxSpeed();
        this.avgSpeed = user.getAvgSpeed();
        this.maxDistance = user.getMaxDistance();
        this.avgDistance = user.getAvgDistance();
        this.totalDistance = user.getTotalDistance();
        this.nrOfCompetitionsWon = user.getNrOfCompetitionsWon();
        this.online = user.isOnline();
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthId() {
        return authId;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Gender getGender() {
        return gender;
    }

    public String getCity() {
        return city;
    }

    public Date getBirthday() {
        return birthday;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public long getMaxDistance() {
        return maxDistance;
    }

    public double getAvgDistance() {
        return avgDistance;
    }

    public long getTotalDistance() {
        return totalDistance;
    }

    public int getNrOfCompetitionsWon() {
        return nrOfCompetitionsWon;
    }

    public boolean isOnline() {
        return online;
    }
}
