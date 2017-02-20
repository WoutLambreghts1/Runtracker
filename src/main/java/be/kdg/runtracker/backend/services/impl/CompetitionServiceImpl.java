package be.kdg.runtracker.backend.services.impl;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.persistence.api.CompetitionRepository;
import be.kdg.runtracker.backend.persistence.api.GoalRepository;
import be.kdg.runtracker.backend.persistence.api.TrackingRepository;
import be.kdg.runtracker.backend.persistence.api.UserRepository;
import be.kdg.runtracker.backend.persistence.impl.CoordinatesRepositoryMongo;
import be.kdg.runtracker.backend.services.api.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("CompetitionService")
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

    private CompetitionRepository competitionRepository;
    private TrackingRepository trackingRepository;
    private CoordinatesRepositoryMongo coordinatesRepositoryMongo;
    private GoalRepository goalRepository;
    private UserRepository userRepository;

    @Autowired
    public CompetitionServiceImpl(CompetitionRepository competitionRepository, TrackingRepository trackingRepository, CoordinatesRepositoryMongo coordinatesRepositoryMongo, UserRepository userRepository, GoalRepository goalRepository) {
        this.competitionRepository = competitionRepository;
        this.trackingRepository = trackingRepository;
        this.coordinatesRepositoryMongo = coordinatesRepositoryMongo;
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Competition> findAllCompetitions() {
        return competitionRepository.findAll();
    }

    @Override
    public List<Competition> findAvailableCompetitions(User user) {
        List<Competition> allCompetitions = this.competitionRepository.findAll();
        List<Competition> availableCompetitions = new ArrayList<>();

        if (allCompetitions != null || !allCompetitions.isEmpty()) {
            Date today = new Date();
            for (Competition competition : allCompetitions) {
                if (competition.getDeadline().after(today) && (competition.getMaxParticipants() > competition.getUsersRun().size()) && !user.getCompetitionsRun().contains(competition)) {
                    availableCompetitions.add(competition);
                }
            }
        }

        return availableCompetitions;
    }

    @Override
    public Competition findCompetitionByCompetitionId(long competitionId) {
        return this.competitionRepository.findCompetitionByCompetitionId(competitionId);
    }

    @Override
    public void saveCompetition(Competition competition) {
        this.competitionRepository.save(competition);
    }

    @Override
    public void deleteCompetition(User user, Competition competition) {
        List<Tracking> trackings = competition.getTrackings();
        if (trackings != null && !trackings.isEmpty()) {
            for (Tracking tracking : trackings) {
                tracking.setCompetition(null);
                tracking.setUser(null);
                this.trackingRepository.save(tracking);
                this.coordinatesRepositoryMongo.deleteCoordinatesCollection(tracking.getTrackingId());
                this.trackingRepository.delete(tracking.getTrackingId());
            }
        }

        Goal goal = competition.getGoal();
        if (goal != null) this.goalRepository.delete(goal.getGoalId());

        competition.setUserCreated(null);
        competition.setUsersRun(null);
        competition.setUserWon(null);
        competition.setTrackings(null);
        this.competitionRepository.save(competition);

        if (user.getCompetitionsCreated() != null && user.getCompetitionsCreated().contains(competition))
            user.getCompetitionsCreated().remove(competition);
        if (user.getCompetitionsRun() != null && user.getCompetitionsRun().contains(competition))
            user.getCompetitionsRun().remove(competition);
        if (user.getCompetitionsWon() != null && user.getCompetitionsWon().contains(competition))
            user.getCompetitionsWon().remove(competition);
        this.userRepository.save(user);

        this.competitionRepository.delete(competition.getCompetitionId());
    }

}
