package be.kdg.runtracker.backend.services.api;

import be.kdg.runtracker.backend.dom.tracking.Tracking;

import java.util.List;

public interface TrackingService {

    List<Tracking> findAllTrackings();

    Tracking findTrackingByTrackingId(long trackingId);

    void saveTracking(Tracking tracking);

    void deleteTrackings(List<Tracking> trackings);

    void deleteTrackingByTrackingId(long trackingId);

}
