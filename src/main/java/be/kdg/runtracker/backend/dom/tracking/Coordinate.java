package be.kdg.runtracker.backend.dom.tracking;

import java.time.LocalTime;

/**
 * @author Wout
 */
public class Coordinate{

    private double lat;
    private double lon;
    private LocalTime time;
    private long trackingID;
    private double speed;

    public Coordinate(double lat, double lon, long trackingID,double speed) {
        this.lat = lat;
        this.lon = lon;
        this.trackingID = trackingID;
        this.speed = speed;
        time = LocalTime.now();
    }

    public Coordinate(double lat, double lon, LocalTime time, long trackingID,double speed) {
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.trackingID = trackingID;
    }

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return this.lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public LocalTime getTime() {
        return this.time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public long getTrackingID() {
        return this.trackingID;
    }

    public void setTrackingID(long trackingID) {
        this.trackingID = trackingID;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }
}
