package be.kdg.runtracker.backend.persistence.api;

import be.kdg.runtracker.backend.dom.profile.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Wout on 21/02/2017.
 */
public interface FriendshipRepository extends JpaRepository<Friendship,Long> {
}
