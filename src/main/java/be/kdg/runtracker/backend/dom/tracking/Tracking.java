package be.kdg.runtracker.backend.dom.tracking;

import be.kdg.runtracker.backend.dom.profile.User;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Wout
 */

@Entity
@Table(name="Tracking")
public class Tracking implements Serializable {

    @Id
    @GeneratedValue
    @Column(nullable=false)
    private Long tracking_id;

    @Basic
    private Timestamp time;

    @Basic
    private long totalDuration;

    @Basic
    private long totalDistance;

    @Basic
    private double maxSpeed;

    @Basic
    private double avgSpeed;

    @ManyToOne(targetEntity = User.class)
    private User user;

    public Tracking() {
    }

    public Tracking(long totalDuration, long totalDistance, double maxSpeed, double avgSpeed) {
        this.time = Timestamp.valueOf(LocalDateTime.now());
        this.totalDuration = totalDuration;
        this.totalDistance = totalDistance;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
    }

    public Long getTracking_id() {
        return this.tracking_id;
    }

    public void setTracking_id(Long tracking_id) {
        this.tracking_id = tracking_id;
    }

    public Timestamp getTime() {
        return this.time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public long getTotalDuration() {
        return this.totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public long getTotalDistance() {
        return this.totalDistance;
    }

    public void setTotalDistance(long totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getMaxSpeed() {
        return this.maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getAvgSpeed() {
        return this.avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
