package be.kdg.runtracker.backend.dom.scheduling;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Wout
 */

@Entity
@Table(name="Event")
public class Event implements Serializable {

    @Id
    @GeneratedValue
    @Column(nullable=false)
    private Long event_id;

    @Column(nullable=false)
    @Basic
    private byte dayOfWeek;

    @Basic
    private String name;

    @Basic
    private String description;

    @Basic
    private long duration;

    public Long getEvent_id() {
        return this.event_id;
    }

    public void setEvent_id(Long event_id) {
        this.event_id = event_id;
    }

    public byte getDayOfWeek() {
        return this.dayOfWeek;
    }

    public void setDayOfWeek(byte dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
