package be.kdg.runtracker.backend.services.api;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;

import java.util.List;

public interface TrackingService {

    List<Tracking> findAllTrackings();

    Tracking findTrackingByTrackingId(long trackingId);

    void saveTracking(Tracking tracking, User user);

    void deleteTrackings(List<Tracking> trackings, User user);

    void deleteTracking(Tracking tracking, User user);

}
