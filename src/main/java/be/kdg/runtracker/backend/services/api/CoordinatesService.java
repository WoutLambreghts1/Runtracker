package be.kdg.runtracker.backend.services.api;

import be.kdg.runtracker.backend.dom.tracking.Coordinate;

import java.util.List;

public interface CoordinatesService {

    List<Coordinate> readCoordinatesByTrackingId(long trackingId);

    void createCoordinatesCollection(long trackingId, List<Coordinate> coordinates);

    void addCoordinateToCollection(long trackingId, Coordinate coordinate);

    void deleteCoordinatesCollection(long trackingId);

}
