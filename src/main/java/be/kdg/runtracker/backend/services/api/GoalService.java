package be.kdg.runtracker.backend.services.api;

import be.kdg.runtracker.backend.dom.competition.Goal;

import java.util.List;

public interface GoalService {

    List<Goal> findAllGoals();

    Goal findGoalByGoalId(long goalId);

    void saveGoal(Goal goal);

    void deleteGoalsByGoalId(long goalId);

    void deleteGoals(List<Goal> goals);

}
