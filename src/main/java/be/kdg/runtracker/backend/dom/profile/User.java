package be.kdg.runtracker.backend.dom.profile;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.scheduling.Schedule;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
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
@Table(name="User")
public class User implements Serializable { 


    @Id
    @GeneratedValue
    @Column(nullable=false)
    private Long user_id;

    @Column(nullable=false)
    @Basic(optional=false)
    @NotNull
    private String username;

    @Column(nullable=false)
    @Basic(optional=false)
    @NotNull
    private long authId;

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
    private String email;

    @Basic
    private String phone;

    @Basic
    private Date birthday;

    @Basic
    private double weight;

    @Basic
    private double plength;

    @ManyToOne(targetEntity = Schedule.class,fetch = FetchType.EAGER)
    private Schedule schedule;

    @ManyToMany(targetEntity = User.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<User> friends;

    @OneToMany(targetEntity = Competition.class,mappedBy = "userCreated",cascade = CascadeType.REMOVE)
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

    public Long getUser_id() {
        return this.user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getAuthId() {
        return authId;
    }

    public void setAuthId(long authId) {
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPlength() {
        return this.plength;
    }

    public void setPlength(double plength) {
        this.plength = plength;
    }


    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
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

    public void removeTracking(User friend){
        if(friends.contains(friend)){
            friends.remove(friend);
        }
    }


}
