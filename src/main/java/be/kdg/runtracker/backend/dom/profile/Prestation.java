package be.kdg.runtracker.backend.dom.profile;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Wout
 */

@Entity
@Table(name="Prestation")
public class Prestation implements Serializable {

    @Id
    @GeneratedValue
    @Column(nullable=false)
    private Long prestation_id;

    @Basic
    private double maxSpeed;

    @Basic
    private double avgSpeed;

    @Basic
    private long maxDistance;

    @Basic
    private double avgDistance;

    @Basic
    private long totalDistance;

    @Basic
    private boolean ranTenKm;

    @Basic
    private boolean ranTwentyKm;

    @Basic
    private boolean ranMarathon;

    @Basic
    private int competitionsWon;

    @OneToOne(targetEntity = User.class,mappedBy = "prestation",fetch = FetchType.EAGER)
    private User user;

    public Long getPrestation_id() {
        return this.prestation_id;
    }

    public void setPrestation_id(Long prestation_id) {
        this.prestation_id = prestation_id;
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

    public long getMaxDistance() {
        return this.maxDistance;
    }

    public void setMaxDistance(long maxDistance) {
        this.maxDistance = maxDistance;
    }

    public double getAvgDistance() {
        return this.avgDistance;
    }

    public void setAvgDistance(double avgDistance) {
        this.avgDistance = avgDistance;
    }

    public long getTotalDistance() {
        return this.totalDistance;
    }

    public void setTotalDistance(long totalDistance) {
        this.totalDistance = totalDistance;
    }

    public boolean isRanTenKm() {
        return this.ranTenKm;
    }

    public void setRanTenKm(boolean ranTenKm) {
        this.ranTenKm = ranTenKm;
    }

    public boolean isRanTwentyKm() {
        return this.ranTwentyKm;
    }

    public void setRanTwentyKm(boolean ranTwentyKm) {
        this.ranTwentyKm = ranTwentyKm;
    }

    public boolean isRanMarathon() {
        return this.ranMarathon;
    }

    public void setRanMarathon(boolean ranMarathon) {
        this.ranMarathon = ranMarathon;
    }

    public int getCompetitionsWon() {
        return this.competitionsWon;
    }

    public void setCompetitionsWon(int competitionsWon) {
        this.competitionsWon = competitionsWon;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
