package be.kdg.runtracker.backend.services.impl;

import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.persistence.api.GoalRepository;
import be.kdg.runtracker.backend.services.api.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("GoalService")
@Transactional
public class GoalServiceImpl implements GoalService {

    private GoalRepository goalRepository;

    @Autowired
    public GoalServiceImpl(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @Override
    public List<Goal> findAllGoals() {
        return this.goalRepository.findAll();
    }

    @Override
    public Goal findGoalByGoalId(long goalId) {
        return this.goalRepository.findOne(goalId);
    }

    @Override
    public void saveGoal(Goal goal) {
        this.goalRepository.save(goal);
    }

    @Override
    public void deleteGoalsByGoalId(long goalId) {
        this.goalRepository.delete(goalId);
    }

    @Override
    public void deleteGoals(List<Goal> goals) {
        this.goalRepository.delete(goals);
    }

}
