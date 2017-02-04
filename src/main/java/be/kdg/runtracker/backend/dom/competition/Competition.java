package be.kdg.runtracker.backend.dom.competition;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author Wout
 */

@Entity
@Table(name="Competition")
public class Competition implements Serializable {


    @Id
    @GeneratedValue
    @Column(nullable=false)
    private Long competition_id;

    @Column(nullable=false)
    @NotNull
    private CompetitionType competitionType;

    @ManyToOne(targetEntity = Goal.class,fetch = FetchType.EAGER)
    private Goal goal;

    @ManyToOne(targetEntity = User.class,fetch = FetchType.EAGER)
    private User userCreated;

    @ManyToOne(targetEntity = User.class,fetch = FetchType.EAGER)
    private User userWon;

    @OneToMany(targetEntity = Tracking.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Tracking> trackings;

    @ManyToMany(targetEntity = User.class,mappedBy = "competitionsRun")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<User> usersRun;

    public Competition(User userCreated, Goal goal, CompetitionType competitionType) {
        this.userCreated = userCreated;
        this.goal = goal;
        this.competitionType = competitionType;
    }

    public Competition() {
    }

    public Long getCompetition_id() {
        return this.competition_id;
    }

    public void setCompetition_id(Long competition_id) {
        this.competition_id = competition_id;
    }

    public CompetitionType getCompetitionType() {
        return this.competitionType;
    }

    public void setCompetitionType(CompetitionType competitionType) {
        this.competitionType = competitionType;
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
        return this.trackings;
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

}
