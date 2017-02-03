package be.kdg.runtracker.backend.persistence;

import be.kdg.runtracker.backend.dom.profile.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Wout on 3/02/2017.
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
