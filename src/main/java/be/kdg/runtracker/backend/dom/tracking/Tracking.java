package be.kdg.runtracker.backend.dom.tracking;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

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

    @ManyToMany(targetEntity = Coordinate.class)
    private List<Coordinate> coordinates;

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

    public List<Coordinate> getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

}
