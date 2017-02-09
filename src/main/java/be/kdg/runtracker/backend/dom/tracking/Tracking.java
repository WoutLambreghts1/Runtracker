package be.kdg.runtracker.backend.dom.tracking;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.profile.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Wout
 */

@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="trackingId")
@Table(name="Tracking")
public class Tracking implements Serializable {

    @Id
    @GeneratedValue
    @Column(nullable=false, name = "tracking_id")
    private Long trackingId;

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

    @ManyToOne(targetEntity = Competition.class)
    private Competition competition;

    @Transient
    List<Coordinate> coordinates;

    public Tracking() {
    }

    public Tracking(long totalDuration, long totalDistance, double maxSpeed, double avgSpeed) {
        this.time = Timestamp.valueOf(LocalDateTime.now());
        this.totalDuration = totalDuration;
        this.totalDistance = totalDistance;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
    }

    public Tracking(long totalDuration, long totalDistance, double maxSpeed, double avgSpeed, List<Coordinate> coordinates) {
        this.time = Timestamp.valueOf(LocalDateTime.now());
        this.totalDuration = totalDuration;
        this.totalDistance = totalDistance;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.coordinates = coordinates;
    }

    public Tracking(long totalDuration, long totalDistance, double maxSpeed, double avgSpeed, List<Coordinate> coordinates, Competition competition) {
        this.time = Timestamp.valueOf(LocalDateTime.now());
        this.totalDuration = totalDuration;
        this.totalDistance = totalDistance;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.coordinates = coordinates;
        this.competition = competition;
    }

    public Long getTrackingId() {
        return this.trackingId;
    }

    public void setTrackingId(Long trackingId) {
        this.trackingId = trackingId;
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

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}
