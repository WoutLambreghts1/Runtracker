package be.kdg.runtracker.backend.persistence;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.profile.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Wout on 3/02/2017.
 */
public interface CompetitionRepository extends JpaRepository<Competition,Long> {
    List<Competition> findCompetitionByUserCreated(User userCreated);
}
