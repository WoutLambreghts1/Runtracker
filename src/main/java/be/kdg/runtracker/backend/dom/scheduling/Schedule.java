package be.kdg.runtracker.backend.dom.scheduling;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author Wout
 */

@Entity
@Table(name="Schedule")
public class Schedule implements Serializable {

    @Id
    @GeneratedValue
    @Column(nullable=false)
    private Long schedule_id;

    @Column(nullable=false)
    @Basic
    @NotNull
    private String name;

    @Basic
    private int nrOfWeeks;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = Event.class, cascade = CascadeType.REMOVE)
    private List<Event> events;

    public Long getSchedule_id() {
        return this.schedule_id;
    }

    public void setSchedule_id(Long schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNrOfWeeks() {
        return this.nrOfWeeks;
    }

    public void setNrOfWeeks(int nrOfWeeks) {
        this.nrOfWeeks = nrOfWeeks;
    }

    public List<Event> getEvents() {
        return this.events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Schedule(String name, int nrOfWeeks, List<Event> events) {
        this.name = name;
        this.nrOfWeeks = nrOfWeeks;
        this.events = events;
    }

    public Schedule() {
    }
}
