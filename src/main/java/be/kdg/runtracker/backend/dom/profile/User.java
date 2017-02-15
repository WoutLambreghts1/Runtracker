package be.kdg.runtracker.backend.dom.profile;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wout
 */

@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="userId", scope = User.class)
@Table(name="User")
public class User implements Serializable { 

    @Id
    @GeneratedValue
    @Column(nullable=false, name = "user_id")
    private Long userId;

    @Column(nullable=false)
    @Basic(optional=false)
    @NotNull
    private String username;

    @Column(nullable=false)
    @Basic(optional=false)
    @NotNull
    private String authId;

    @Basic
    @NotNull
    private String firstname;

    @Basic
    private String lastname;

    @Basic
    private Gender gender;

    @Basic
    private String city;

    @Basic
    private Date birthday;

    @ManyToMany(targetEntity = User.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<User> friends;

    @OneToMany(targetEntity = Competition.class,mappedBy = "userCreated")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Competition> competitionsCreated;

    @OneToMany(targetEntity = Tracking.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Tracking> trackings;

    @OneToMany(targetEntity = Competition.class,mappedBy = "userWon")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Competition> competitionsWon;

    @ManyToMany(targetEntity = Competition.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Competition> competitionsRun;

    @Basic
    private double maxSpeed;

    @Basic
    private double avgSpeed;

    @Basic
    private long maxDistance;

    @Basic
    private double avgDistance;

    @Basic
    private long totalDistance;

    @Basic
    private boolean ranTenKm;

    @Basic
    private boolean ranTwentyKm;

    @Basic
    private boolean ranMarathon;

    @Basic
    private int nrOfCompetitionsWon;

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Gender getGender() {
        return this.gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<Competition> getCompetitionsCreated() {
        return this.competitionsCreated;
    }

    public void setCompetitionsCreated(List<Competition> competitionsCreated) {
        this.competitionsCreated = competitionsCreated;
    }

    public List<Tracking> getTrackings() {
        return this.trackings;
    }

    public void setTrackings(List<Tracking> trackings) {
        this.trackings = trackings;
    }

    public List<Competition> getCompetitionsWon() {
        return this.competitionsWon;
    }

    public void setCompetitionsWon(List<Competition> competitionsWon) {
        this.competitionsWon = competitionsWon;
    }

    public List<Competition> getCompetitionsRun() {
        return this.competitionsRun;
    }

    public void setCompetitionsRun(List<Competition> competitionsRun) {
        this.competitionsRun = competitionsRun;
    }

    public double getAvgDistance() {
        return avgDistance;
    }

    public void setAvgDistance(double avgDistance) {
        this.avgDistance = avgDistance;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public long getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(long maxDistance) {
        this.maxDistance = maxDistance;
    }

    public long getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(long totalDistance) {
        this.totalDistance = totalDistance;
    }

    public boolean isRanTenKm() {
        return ranTenKm;
    }

    public void setRanTenKm(boolean ranTenKm) {
        this.ranTenKm = ranTenKm;
    }

    public boolean isRanTwentyKm() {
        return ranTwentyKm;
    }

    public void setRanTwentyKm(boolean ranTwentyKm) {
        this.ranTwentyKm = ranTwentyKm;
    }

    public boolean isRanMarathon() {
        return ranMarathon;
    }

    public void setRanMarathon(boolean ranMarathon) {
        this.ranMarathon = ranMarathon;
    }

    public int getNrOfCompetitionsWon() {
        return nrOfCompetitionsWon;
    }

    public void setNrOfCompetitionsWon(int nrOfCompetitionsWon) {
        this.nrOfCompetitionsWon = nrOfCompetitionsWon;
    }

    public void addFriend(User friend){
        if(friends == null){
            friends = new ArrayList<>();
        }

        if(!friends.contains(friend)){
            friends.add(friend);
        }
    }

    public void addTracking(Tracking tracking) {

        if(trackings == null) trackings = new ArrayList<>();
        if (!trackings.contains(tracking)) trackings.add(tracking);
    }

    public void addCompetitionsCreated(Competition competition) {
        if (this.competitionsCreated == null) {
            this.competitionsCreated = new ArrayList<>();
        }
        this.competitionsCreated.add(competition);
    }

    public void addCompetitionsRan(Competition competition) {
        if (this.competitionsRun == null) {
            this.competitionsRun = new ArrayList<>();
        }
        this.competitionsRun.add(competition);
    }

    public void addCompetitionsWon(Competition competition) {
        if (this.competitionsWon == null) {
            this.competitionsWon = new ArrayList<>();
        }
        this.competitionsWon.add(competition);
    }

    public void removeTracking(User friend){
        if(friends.contains(friend)){
            friends.remove(friend);
        }
    }


}
