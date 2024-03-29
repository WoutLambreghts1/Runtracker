package be.kdg.runtracker.frontend.dto;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.tracking.Coordinate;
import be.kdg.runtracker.backend.dom.tracking.Tracking;

import java.util.List;

public class TrackingDTO {

    private long totalDuration;
    private double totalDistance;
    private double maxSpeed;
    private double avgSpeed;
    private double avgPace;
    private Competition competition;
    private List<Coordinate> coordinates;

    public TrackingDTO() {
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public double getAvgPace() {
        return avgPace;
    }

    public Competition getCompetition() {
        return competition;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public void setTotalDistance(long totalDistance) {
        this.totalDistance = totalDistance;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public void setAvgPace(double avgPace) {
        this.avgPace = avgPace;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public Tracking getAsTrackingWithoutCompetition() {
        return new Tracking(this.totalDuration, this.totalDistance, this.maxSpeed, this.avgSpeed, this.avgPace, this.coordinates);
    }

    public Tracking getAsTrackingWithCompetition() {
        return null;
    }

}
