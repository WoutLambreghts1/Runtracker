package be.kdg.runtracker.frontend.dto;

import be.kdg.runtracker.backend.dom.tracking.Coordinate;
import be.kdg.runtracker.backend.dom.tracking.Tracking;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ShortTracking {

    private Long trackingId;
    private Timestamp time;
    private long totalDuration;
    private long totalDistance;
    private double maxSpeed;
    private double avgSpeed;
    private double avgPace;
    private ShortUser user;
    List<Coordinate> coordinates;

    public ShortTracking(Tracking tracking) {
        this.trackingId = tracking.getTrackingId();
        this.time = tracking.getTime();
        this.totalDuration = tracking.getTotalDuration();
        this.totalDistance = tracking.getTotalDistance();
        this.maxSpeed = tracking.getMaxSpeed();
        this.avgSpeed = tracking.getAvgSpeed();
        this.avgPace = tracking.getAvgPace();
        if (tracking.getUser() != null) {
            this.user = new ShortUser(tracking.getUser());
        } else { this.user = null; }
        if (tracking.getCoordinates() != null && !tracking.getCoordinates().isEmpty()) {
            this.coordinates = tracking.getCoordinates();
        } else { this.coordinates = new ArrayList<>(); }
    }

    public Long getTrackingId() {
        return trackingId;
    }

    public Timestamp getTime() {
        return time;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public long getTotalDistance() {
        return totalDistance;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public ShortUser getUser() {
        return user;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public double getAvgPace() {
        return avgPace;
    }
}
