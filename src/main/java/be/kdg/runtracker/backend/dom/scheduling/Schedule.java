package be.kdg.runtracker.backend.dom.scheduling;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

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
    private byte nrOfWeeks;

    @OneToMany(targetEntity = Event.class)
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

    public Optional<Byte> getNrOfWeeks() {
        return Optional.ofNullable(this.nrOfWeeks);
    }

    public void setNrOfWeeks(byte nrOfWeeks) {
        this.nrOfWeeks = nrOfWeeks;
    }

    public List<Event> getEvents() {
        return this.events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

}
