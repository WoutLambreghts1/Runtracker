package be.kdg.runtracker.backend.dom.tracking;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

/**
 * @author Wout
 */
@Document
public class Coordinate{

    @Id
    private long time;

    private double lat;

    private double lon;

    private long trackingId;

    private double speed;

    public Coordinate() {
    }

    public Coordinate(double lat, double lon, long trackingId, double speed) {
        this.lat = lat;
        this.lon = lon;
        this.trackingId = trackingId;
        this.speed = speed;
        time = LocalTime.now().toSecondOfDay();
    }

    public Coordinate(double lat, double lon, LocalTime time, long trackingId, double speed) {
        this.lat = lat;
        this.lon = lon;
        this.time = time.toSecondOfDay();
        this.trackingId = trackingId;
        this.speed = speed;
    }

    public Coordinate(double lat, double lon, long time, long trackingId, double speed) {
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.trackingId = trackingId;
        this.speed = speed;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTrackingId() {
        return this.trackingId;
    }

    public void setTrackingId(long trackingId) {
        this.trackingId = trackingId;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }
}
