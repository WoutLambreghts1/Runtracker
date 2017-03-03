package be.kdg.runtracker.backend.dom.competition;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wout
 */

@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="competitionId", scope = Competition.class)
@Table(name="Competition")
public class Competition implements Serializable {

    @Id
    @GeneratedValue
    @Column(nullable=false, name = "competition_id")
    private Long competitionId;

    @Basic
    private String name;

    @Basic
    private Timestamp time;

    @ManyToOne(targetEntity = Goal.class,fetch = FetchType.EAGER)
    private Goal goal;

    @ManyToOne(targetEntity = User.class,fetch = FetchType.EAGER)
    private User userCreated;

    @ManyToOne(targetEntity = User.class,fetch = FetchType.EAGER)
    private User userWon;

    @OneToMany(targetEntity = Tracking.class, cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Tracking> trackings;

    @ManyToMany(targetEntity = User.class,mappedBy = "competitionsRun")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<User> usersRun;

    public Competition(User userCreated, Goal goal,String name) {
        this.userCreated = userCreated;
        this.goal = goal;
        this.name = name;
        this.time = java.sql.Timestamp.valueOf(LocalDateTime.now());

        addRunner(userCreated);
    }

    public Competition() {
    }

    public Long getCompetitionId() {
        return this.competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Goal getGoal() {
        return this.goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public User getUserCreated() {
        return this.userCreated;
    }

    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }

    public User getUserWon() {
        return this.userWon;
    }

    public void setUserWon(User userWon) {
        this.userWon = userWon;
    }

    public List<Tracking> getTrackings() {
        if(this.trackings!=null)return this.trackings;
        return new ArrayList<Tracking>();
    }

    public void setTrackings(List<Tracking> trackings) {
        this.trackings = trackings;
    }

    public List<User> getUsersRun() {
        return this.usersRun;
    }

    public void setUsersRun(List<User> usersRun) {
        this.usersRun = usersRun;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void addRunner(User runner){
        if(usersRun == null){
            usersRun = new ArrayList<>();
        }

        if(!usersRun.stream().filter(u -> u.getUserId() == runner.getUserId()).findFirst().isPresent()){
            usersRun.add(runner);
        }

    }

    public void removeRunner(User runner){
        if(usersRun.stream().filter(u -> u.getUserId() == runner.getUserId()).findFirst().isPresent()){
            usersRun.remove(runner);
        }
    }

    public void addTracking(Tracking tracking){
        if(trackings == null){
            trackings = new ArrayList<>();
        }

        if(!trackings.contains(tracking)){
            trackings.add(tracking);
        }
    }

    public void removeTracking(Tracking tracking){
        if(trackings.contains(tracking)){
            trackings.remove(tracking);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Competition that = (Competition) o;

        return competitionId.equals(that.competitionId);

    }

    @Override
    public int hashCode() {
        int result = competitionId.hashCode();
        result = 31 * result + goal.hashCode();
        result = 31 * result + userCreated.hashCode();
        return result;
    }
}
