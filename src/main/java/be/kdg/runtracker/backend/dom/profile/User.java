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

    @Basic
    private boolean online;

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

    @OneToMany(targetEntity = Friendship.class)
    private List<Friendship> friendships;

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
    private int nrOfCompetitionsWon;

    @Basic
    private int nrOfCompetitionsDone;

    @Basic
    private String avatar;

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

    public int getNrOfCompetitionsWon() {
        return nrOfCompetitionsWon;
    }

    public void setNrOfCompetitionsWon(int nrOfCompetitionsWon) {
        this.nrOfCompetitionsWon = nrOfCompetitionsWon;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getNrOfCompetitionsDone() {
        return nrOfCompetitionsDone;
    }

    public void setNrOfCompetitionsDone(int nrOfCompetitionsDone) {
        this.nrOfCompetitionsDone = nrOfCompetitionsDone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public List<Friendship> getFriendships() {
        if(this.friendships!=null)return this.friendships;
        return new ArrayList<Friendship>();
    }

    public void addFriendship(Friendship friendship){
        List<Friendship> friendships = getFriendships();
        friendships.add(friendship);
        this.friendships = friendships;
    }

    public void setFriendships(List<Friendship> friendships) {
        this.friendships = friendships;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!userId.equals(user.userId)) return false;
        return authId.equals(user.authId);

    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + authId.hashCode();
        return result;
    }
}
