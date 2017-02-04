package be.kdg.runtracker.backend.persistence;

import be.kdg.runtracker.backend.dom.profile.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Wout on 4/02/2017.
 */
public interface FriendshipRepository extends JpaRepository<Friendship,Long>{
}
