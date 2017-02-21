package be.kdg.runtracker.backend.dom.profile;


import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Wout
 */

@Entity
@Table(name="Friendship")
public class Friendship implements Serializable {


    @Id
    @GeneratedValue
    @Column(nullable=false,name="friendship_id")
    private Long friendshipId;

    @Basic
    private Date friendsSince;

    @OneToOne(targetEntity = User.class)
    private User friend;

    private boolean accepted;

    public Long getFriendshipId() {
        return friendshipId;
    }

    public void setFriendshipId(Long friendshipId) {
        this.friendshipId = friendshipId;
    }

    public Date getFriendsSince() {
        return friendsSince;
    }

    public void setFriendsSince(Date friendsSince) {
        this.friendsSince = friendsSince;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }


    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public Friendship(User friend) {
        this.friend = friend;
        this.friendsSince = new Date( Calendar.getInstance(TimeZone.getTimeZone("Europe/Brussels")).getTimeInMillis());
        this.accepted = false;
    }

    public Friendship() {
    }

}
