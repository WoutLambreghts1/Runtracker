package be.kdg.runtracker.backend.persistence;

import be.kdg.runtracker.backend.dom.tracking.Coordinate;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;


/**
 * Created by Wout on 4/02/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongoDbTest.class)
@ComponentScan("be.kdg.runtracker")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MongoDbTest {

    @Value("1")
    private long trackingId;

    private CoordinatesRepositoryImpl coordinatesRepository = new CoordinatesRepositoryImpl();

    @Test
    public void aSaveToMongoDb()
    {

        //CREATE RANDOM COORDINATES EACH SECOND
        Random r = new Random();
        List<Coordinate> coordinateList;
        coordinateList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            coordinateList.add(new Coordinate(r.nextDouble(),r.nextDouble(),trackingId));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        coordinatesRepository.createCoordinatesCollection(trackingId,coordinateList);
    }

    @Test
    public void bReadFromMongoDb(){
        assertTrue(coordinatesRepository.readCoordinatesByTrackingId(trackingId).size() > 0);
        assertTrue(coordinatesRepository.readCoordinatesByTrackingId(trackingId).get(0) != null);
        assertTrue(coordinatesRepository.readCoordinatesByTrackingId(trackingId).get(0).getTrackingID() == trackingId);
    }


    @Test
    public void cUpdateCollection(){
        Coordinate c = new Coordinate(56.8,53.2,trackingId);
        coordinatesRepository.addCoordinateToCollection(trackingId, c);
        assertTrue(coordinatesRepository.readCoordinatesByTrackingId(trackingId).get(coordinatesRepository.readCoordinatesByTrackingId(trackingId).size() - 1).getLat() == c.getLat() &&
                        coordinatesRepository.readCoordinatesByTrackingId(trackingId).get(coordinatesRepository.readCoordinatesByTrackingId(trackingId).size() - 1).getLon() == c.getLon()
        );
    }


    @Test
    public void deleteCollection(){
        coordinatesRepository.deleteCoordinatesCollection(trackingId);
        System.out.println(coordinatesRepository.readCoordinatesByTrackingId(trackingId).size());
        assertTrue(coordinatesRepository.readCoordinatesByTrackingId(trackingId).size() == 0);
    }





}
