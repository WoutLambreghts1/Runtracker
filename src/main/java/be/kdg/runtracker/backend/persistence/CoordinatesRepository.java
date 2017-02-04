package be.kdg.runtracker.backend.persistence;

import be.kdg.runtracker.backend.dom.tracking.Coordinate;

import java.util.List;

/**
 * Created by Wout on 4/02/2017.
 */
public interface CoordinatesRepository {
    List<Coordinate> readCoordinatesByTrackingId(long trackingId);
    void createCoordinatesCollection(long trackingId,List<Coordinate> coordinates);
    void deleteCoordinatesCollection(long trackingId);
    void addCoordinateToCollection(long trackingId,Coordinate coordinate);

}
