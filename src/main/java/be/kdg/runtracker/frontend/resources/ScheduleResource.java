package be.kdg.runtracker.frontend.resources;

import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;

public class ScheduleResource extends ResourceSupport implements Serializable {

    private Long schedule_id;
    private String name;
    private int nrOfWeeks;

    public Long getSchedule_id() {
        return schedule_id;
    }

    public String getName() {
        return name;
    }

    public int getNrOfWeeks() {
        return nrOfWeeks;
    }

    public void setSchedule_id(Long schedule_id) {
        this.schedule_id = schedule_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNrOfWeeks(int nrOfWeeks) {
        this.nrOfWeeks = nrOfWeeks;
    }

}
