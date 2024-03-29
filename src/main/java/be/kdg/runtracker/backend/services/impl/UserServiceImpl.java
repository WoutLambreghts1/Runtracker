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
import be.kdg.runtracker.backend.services.api.UserService;
import be.kdg.runtracker.frontend.dto.ShortUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("UserService")
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private CompetitionRepository competitionRepository;
    private GoalRepository goalRepository;
    private TrackingRepository trackingRepository;
    private CoordinatesRepositoryMongo coordinatesRepositoryMongo;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CompetitionRepository competitionRepository, GoalRepository goalRepository, TrackingRepository trackingRepository, CoordinatesRepositoryMongo coordinatesRepositoryMongo) {
        this.userRepository = userRepository;
        this.competitionRepository = competitionRepository;
        this.goalRepository = goalRepository;
        this.trackingRepository = trackingRepository;
        this.coordinatesRepositoryMongo = coordinatesRepositoryMongo;
    }

    @Override
    public List<User> findAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User findUserByAuthId(String authId) {
        User user = this.userRepository.findUserByAuthId(String.valueOf(authId));
        if (user != null) updateUserPrestations(user);
        return user;
    }

    @Override
    public User findUserByUsername(String username) {
        User user = this.userRepository.findUserByUsername(username);
        if (user != null) updateUserPrestations(user);
        return user;
    }

    @Override
    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        List<Competition> competitionsCreated = user.getCompetitionsCreated();
        List<Tracking> trackings = user.getTrackings();

        user.setCompetitionsRun(new ArrayList<>());
        user.setCompetitionsWon(new ArrayList<>());
        user.setCompetitionsCreated(new ArrayList<>());
        user.setTrackings(new ArrayList<>());
        this.userRepository.save(user);

        if (trackings != null && !trackings.isEmpty())
            trackings.stream().forEach(tracking -> this.coordinatesRepositoryMongo.deleteCoordinatesCollection(tracking.getTrackingId()));

        if (trackings != null && !trackings.isEmpty())
            this.trackingRepository.delete(trackings);

        if (competitionsCreated != null && !competitionsCreated.isEmpty()) {
            List<Goal> goals = new ArrayList<>();
            for (Competition competition : competitionsCreated) {
                competition.setUserCreated(null);
                competition.setUsersRun(null);
                competition.setUserWon(null);
                goals.add(competition.getGoal());
                this.competitionRepository.save(competition);
                this.competitionRepository.delete(competition.getCompetitionId());
            }
            if (!goals.isEmpty()) this.goalRepository.delete(goals);
        }

        this.userRepository.delete(user.getUserId());
    }

    @Override
    public ShortUser createUser(User user) {
        boolean exists = true;
        if(findUserByUsername(user.getUsername())==null){
            this.userRepository.save(user);
            exists = false;
        }

        int usernr = 1;
        while (exists){
            if(findUserByUsername(user.getUsername() + usernr)==null){
                user.setUsername(user.getUsername()+usernr);
                this.userRepository.save(user);
                exists=false;
            }
            usernr++;
        };

        return new ShortUser(user);
    }

    private void updateUserPrestations(User user){

        if (user.getTrackings() != null && user.getTrackings().size() > 0){

            //Calculate avg speed
            if (user.getTrackings() != null) {
                double avgSpeed = user.getTrackings().stream().map(t -> t.getAvgSpeed() * t.getTotalDuration()).mapToDouble(Number::doubleValue).sum() /
                        user.getTrackings().stream().map(tt -> tt.getTotalDuration()).mapToLong(Number::longValue).sum();
                user.setAvgSpeed(avgSpeed);
            } else {
                user.setAvgSpeed(0);
            }

            //Calculate avg distance
            if (user.getTrackings() != null) {
                double avgDistance = user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).average().getAsDouble();
                user.setAvgDistance(avgDistance);
            } else {
                user.setAvgDistance(0);
            }

            //Calculate total distance
            if (user.getTrackings() != null) {
                double totalDistance = user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).sum();
                user.setTotalDistance((long) totalDistance);
            } else {
                user.setTotalDistance(0);
            }

            //Calculate max distance
            if (user.getTrackings() != null) {
                double maxDistance = user.getTrackings().stream().map(t -> t.getTotalDistance()).mapToDouble(Number::doubleValue).max().getAsDouble();
                user.setMaxDistance((long) maxDistance);
            } else {
                user.setMaxDistance(0);
            }

            //Calculate max speed
            if (user.getTrackings() != null) {
                double maxSpeed = user.getTrackings().stream().map(t -> t.getMaxSpeed()).mapToDouble(Number::doubleValue).max().getAsDouble();
                user.setMaxSpeed(maxSpeed);
            } else {
                user.setMaxSpeed(0);
            }

            //Calculate nr of wins
            if (user.getCompetitionsWon() != null) {
                int nrOfWins = user.getCompetitionsWon().size();
                user.setNrOfCompetitionsWon(nrOfWins);
            } else {
                user.setNrOfCompetitionsWon(0);
            }

            //Calculate nr of competitions done
            if (user.getCompetitionsRun() != null) {
                int nrDone= user.getCompetitionsRun().size();
                user.setNrOfCompetitionsDone(nrDone);
            } else {
                user.setNrOfCompetitionsDone(0);
            }

            //Update user
            saveUser(user);
        }

    }
}
