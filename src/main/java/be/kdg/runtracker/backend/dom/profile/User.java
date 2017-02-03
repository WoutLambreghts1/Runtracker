package be.kdg.runtracker.backend.dom.profile;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.scheduling.Schedule;
import be.kdg.runtracker.backend.dom.tracking.Tracking;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

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
    private String password;

    @Basic
    @NotNull
    private String firstname;

    @Basic
    private String lastname;

    @Basic
    private byte gender;

    @Basic
    private String city;

    @Basic
    private String email;

    @Basic
    private String phone;

    @Basic
    private String pictureURL;

    @Basic
    private Date birthday;

    @Basic
    private double weight;

    @Basic
    private double plength;

    @OneToOne(targetEntity = Prestation.class)
    private Prestation prestation;

    @OneToOne(targetEntity = Friendship.class)
    private Friendship friendship;

    @ManyToOne(targetEntity = Schedule.class)
    private Schedule schedule;

    @OneToMany(targetEntity = Friendship.class)
    private List<Friendship> friendships;

    @OneToMany(targetEntity = Competition.class,mappedBy = "userCreated")
    private List<Competition> competitionsCreated;

    @OneToMany(targetEntity = Tracking.class)
    private List<Tracking> trackings;

    @OneToMany(targetEntity = Competition.class,mappedBy = "userWon")
    private List<Competition> competitionsWon;

    @ManyToMany(targetEntity = Competition.class)
    private List<Competition> competitionsRun;

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

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Optional<String> getLastname() {
        return Optional.ofNullable(this.lastname);
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Optional<Byte> getGender() {
        return Optional.ofNullable(this.gender);
    }

    public void setGender(byte gender) {
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

    public String getPictureURL() {
        return this.pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
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

    public Prestation getPrestation() {
        return this.prestation;
    }

    public void setPrestation(Prestation prestation) {
        this.prestation = prestation;
    }

    public Friendship getFriendship() {
        return this.friendship;
    }

    public void setFriendship(Friendship friendship) {
        this.friendship = friendship;
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public List<Friendship> getFriendships() {
        return this.friendships;
    }

    public void setFriendships(List<Friendship> friendships) {
        this.friendships = friendships;
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


}
