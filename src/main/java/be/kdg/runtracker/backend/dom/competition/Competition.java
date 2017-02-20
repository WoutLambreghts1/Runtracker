package be.kdg.runtracker.backend.dom.competition;

import be.kdg.runtracker.backend.dom.profile.User;
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
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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

    @Column(nullable=false)
    @NotNull
    private CompetitionType competitionType;

    @Basic
    @NotNull
    private Date deadline;

    @Basic
    @NotNull
    private int maxParticipants;

    @Basic
    @NotNull
    private boolean isFinished;

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

    public Competition(User userCreated, Goal goal, CompetitionType competitionType, int deadlineInAmountOfDays, int maxParticipants) {
        this.userCreated = userCreated;
        this.goal = goal;
        this.competitionType = competitionType;
        this.maxParticipants = maxParticipants;
        this.isFinished = false;

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Brussels"));
        calendar.add(Calendar.DATE, deadlineInAmountOfDays);
        this.deadline = new Date(calendar.getTimeInMillis());

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

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
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

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished() {
        this.isFinished = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Competition that = (Competition) o;

        if (maxParticipants != that.maxParticipants) return false;
        if (competitionType != that.competitionType) return false;
        if (goal != null ? !goal.equals(that.goal) : that.goal != null) return false;
        return !(userCreated != null ? !userCreated.equals(that.userCreated) : that.userCreated != null);

    }

    @Override
    public int hashCode() {
        int result = competitionType.hashCode();
        result = 31 * result + maxParticipants;
        result = 31 * result + goal.hashCode();
        result = 31 * result + userCreated.getUsername().hashCode();
        return result;
    }
}
