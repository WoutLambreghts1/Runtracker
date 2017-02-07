package be.kdg.runtracker.backend.persistence;

import be.kdg.runtracker.backend.dom.competition.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Wout on 4/02/2017.
 */
public interface GoalRepository extends JpaRepository<Goal,Long> {
    Goal findGoalByDistance(long distance);
    Goal findGoalByName(String name);
}
