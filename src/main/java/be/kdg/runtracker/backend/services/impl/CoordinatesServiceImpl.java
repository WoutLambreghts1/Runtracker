package be.kdg.runtracker.backend.services.impl;

import be.kdg.runtracker.backend.dom.tracking.Coordinate;
import be.kdg.runtracker.backend.persistence.api.CoordinatesRepository;
import be.kdg.runtracker.backend.persistence.impl.CoordinatesRepositoryMongo;
import be.kdg.runtracker.backend.services.api.CoordinatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("CoordinatesService")
@Transactional
public class CoordinatesServiceImpl implements CoordinatesService {

    private CoordinatesRepositoryMongo coordinatesRepositoryMongo;

    @Autowired
    public CoordinatesServiceImpl(CoordinatesRepositoryMongo coordinatesRepositoryMongo) {
        this.coordinatesRepositoryMongo = coordinatesRepositoryMongo;
    }

    @Override
    public List<Coordinate> readCoordinatesByTrackingId(long trackingId) {
        return this.coordinatesRepositoryMongo.readCoordinatesByTrackingId(trackingId);
    }

    @Override
    public void createCoordinatesCollection(long trackingId, List<Coordinate> coordinates) {
        this.coordinatesRepositoryMongo.createCoordinatesCollection(trackingId, coordinates);
    }

    @Override
    public void deleteCoordinatesCollection(long trackingId) {
        this.coordinatesRepositoryMongo.deleteCoordinatesCollection(trackingId);
    }

    @Override
    public void addCoordinateToCollection(long trackingId, Coordinate coordinate) {
        this.coordinatesRepositoryMongo.addCoordinateToCollection(trackingId, coordinate);
    }
}
