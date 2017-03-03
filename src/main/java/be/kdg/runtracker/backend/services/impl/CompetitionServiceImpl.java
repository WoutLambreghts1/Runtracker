package be.kdg.runtracker.backend.services.impl;

import be.kdg.runtracker.backend.dom.competition.Competition;
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
    public Competition findCompetitionByCompetitionId(long competitionId) {
        return this.competitionRepository.findCompetitionByCompetitionId(competitionId);
    }

    @Override
    public Competition saveCompetition(Competition competition) {
        return this.competitionRepository.save(competition);
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


        for (User runner : competition.getUsersRun()) {
            runner.getCompetitionsRun().remove(competition);
            this.userRepository.save(runner);
        }


        competition.setGoal(null);
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
