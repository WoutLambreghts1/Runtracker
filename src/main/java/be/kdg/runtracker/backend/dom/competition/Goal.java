package be.kdg.runtracker.backend.dom.competition;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Wout
 */

@Entity
@Table(name="Goal")
public class Goal implements Serializable {

    @Id
    @GeneratedValue
    @Column(nullable=false)
    private Long id;

    @Column(nullable=false)
    @Basic
    @NotNull
    private String name;

    @Column(nullable=false)
    @Basic
    @NotNull
    private long distance;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDistance() {
        return this.distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

}
