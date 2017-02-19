package be.kdg.runtracker.backend.services.impl;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.exceptions.NotFoundException;
import be.kdg.runtracker.backend.persistence.api.CompetitionRepository;
import be.kdg.runtracker.backend.persistence.api.TrackingRepository;
import be.kdg.runtracker.backend.persistence.api.UserRepository;
import be.kdg.runtracker.backend.persistence.impl.CoordinatesRepositoryMongo;
import be.kdg.runtracker.backend.services.api.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("TrackingService")
@Transactional
public class TrackingServiceImpl implements TrackingService {

    private TrackingRepository trackingRepository;
    private CompetitionRepository competitionRepository;
    private CoordinatesRepositoryMongo coordinatesRepositoryMongo;
    private UserRepository userRepository;

    @Autowired
    public TrackingServiceImpl(TrackingRepository trackingRepository, CompetitionRepository competitionRepository, CoordinatesRepositoryMongo coordinatesRepositoryMongo, UserRepository userRepository) {
        this.trackingRepository = trackingRepository;
        this.competitionRepository = competitionRepository;
        this.coordinatesRepositoryMongo = coordinatesRepositoryMongo;
        this.userRepository = userRepository;
    }

    @Override
    public List<Tracking> findAllTrackings() {
        return this.trackingRepository.findAll();
    }

    @Override
    public Tracking findTrackingByTrackingId(long trackingId) {
        return this.trackingRepository.findTrackingByTrackingId(trackingId);
    }

    @Override
    public void saveTracking(Tracking tracking, User user) {
        tracking.setUser(user);
        user.addTracking(tracking);

        this.trackingRepository.save(tracking);
        this.userRepository.save(user);
        long trackingId = this.trackingRepository.findAll().get(this.trackingRepository.findAll().size() -1).getTrackingId();

        tracking.getCoordinates().stream().forEach(t -> t.setTrackingId(trackingId));
        this.coordinatesRepositoryMongo.createCoordinatesCollection(trackingId, tracking.getCoordinates());
    }

    @Override
    public void deleteTrackings(List<Tracking> trackings, User user) {
        if (trackings != null && !trackings.isEmpty()) {
            for (Tracking tracking : trackings) {
                deleteTracking(tracking, user);
            }
        }
    }

    @Override
    public void deleteTracking(Tracking tracking, User user) {
        Competition competition = null;
        if (tracking != null) competition = tracking.getCompetition();

        if (user.getTrackings().contains(tracking)) {
            user.getTrackings().remove(tracking);
            this.coordinatesRepositoryMongo.deleteCoordinatesCollection(tracking.getTrackingId());
            this.userRepository.save(user);
        } else {
            throw new NotFoundException("User with token does not have Tracking with trackingId " + tracking.getTrackingId() + ".");
        }

        if (competition != null) {
            competition.removeTracking(tracking);
            this.competitionRepository.save(competition);
        }

        this.coordinatesRepositoryMongo.deleteCoordinatesCollection(tracking.getTrackingId());
        this.trackingRepository.delete(tracking.getTrackingId());
    }

}
