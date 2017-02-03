package be.kdg.runtracker.backend.dom.profile;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Wout
 */

@Entity
@Table(name="Friendship")
public class Friendship implements Serializable {

    @Id
    @GeneratedValue
    @Column(nullable=false)
    private Long friendship_id;

    @Basic
    @NotNull
    private String friendsSince;

    @OneToOne(targetEntity = User.class,mappedBy = "friendship")
    private User user;

    public Long getFriendship_id() {
        return this.friendship_id;
    }

    public void setFriendship_id(Long friendship_id) {
        this.friendship_id = friendship_id;
    }

    public String getFriendsSince() {
        return this.friendsSince;
    }

    public void setFriendsSince(String friendsSince) {
        this.friendsSince = friendsSince;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
