package be.kdg.runtracker.backend.dom.competition;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Wout
 */

@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="goalId", scope = Goal.class)
@Table(name="Goal")
public class Goal implements Serializable {

    @Id
    @GeneratedValue
    @Column(nullable=false, name = "goal_id")
    private Long goalId;

    @Column(nullable=false)
    @Basic
    @NotNull
    private String name;

    @Column(nullable=false)
    @Basic
    @NotNull
    private long distance;

    public Goal(String name, long distance) {
        this.name = name;
        this.distance = distance;
    }

    public Goal() {
    }

    public Long getGoalId() {
        return this.goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDistance() {
        return this.distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

}
