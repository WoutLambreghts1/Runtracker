package be.kdg.runtracker.backend.dom.tracking;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Time;

/**
 * @author Wout
 */

@Entity
@Table(name="Coordinate")
public class Coordinate implements Serializable {

    @Id
    @GeneratedValue
    @Column(nullable=false)
    private Long coordinate_id;

    @Column(nullable=false)
    @Basic
    @NotNull
    private double lat;

    @Column(nullable=false)
    @Basic
    @NotNull
    private String lon;

    @Basic
    private Time time;

    @Basic
    private long trackingID;

    public Long getCoordinate_id() {
        return this.coordinate_id;
    }

    public void setCoordinate_id(Long coordinate_id) {
        this.coordinate_id = coordinate_id;
    }

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getLon() {
        return this.lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public Time getTime() {
        return this.time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public long getTrackingID() {
        return this.trackingID;
    }

    public void setTrackingID(long trackingID) {
        this.trackingID = trackingID;
    }

}
