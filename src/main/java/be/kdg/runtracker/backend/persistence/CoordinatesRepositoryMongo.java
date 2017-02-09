package be.kdg.runtracker.backend.persistence;

import be.kdg.runtracker.backend.dom.tracking.Coordinate;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Wout on 7/02/2017.
 */
@Repository
public class CoordinatesRepositoryMongo implements CoordinatesRepository {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<Coordinate> readCoordinatesByTrackingId(long trackingId) {

        return  mongoTemplate.getCollection(String.valueOf(trackingId)).find().toArray().stream().map(d -> docToCoordinate(d)).sorted(Comparator.comparing(t -> t.getTime())).collect(Collectors.toList());
    }

    @Override
    public void createCoordinatesCollection(long trackingId, List<Coordinate> coordinates) {
        mongoTemplate.createCollection(String.valueOf(trackingId));
        if(coordinates != null){
            List<DBObject> objects = coordinates.stream().map(c -> coordinateToDoc(c)).collect(Collectors.toList());
            mongoTemplate.getCollection(String.valueOf(trackingId)).insert(objects);
        }
    }

    @Override
    public void deleteCoordinatesCollection(long trackingId) {
        mongoTemplate.getCollection(String.valueOf(trackingId)).drop();
    }

    @Override
    public void addCoordinateToCollection(long trackingId, Coordinate coordinate) {
        mongoTemplate.getCollection(String.valueOf(trackingId)).insert(coordinateToDoc(coordinate));
    }

    private DBObject coordinateToDoc(Coordinate coordinate){
        DBObject basicDBObject = new BasicDBObject();

        basicDBObject.put("lat",coordinate.getLat());
        basicDBObject.put("lon",coordinate.getLon());
        basicDBObject.put("time",coordinate.getTime().toSecondOfDay());
        basicDBObject.put("trackingId",coordinate.getTrackingId());
        basicDBObject.put("speed",coordinate.getSpeed());

        return basicDBObject;
    }

    private Coordinate docToCoordinate(DBObject dbObject){
        BasicDBObject object = (BasicDBObject) dbObject;
        Coordinate coordinate = new Coordinate(object.getDouble("lat"),object.getDouble("lon"), LocalTime.ofSecondOfDay(object.getInt("time")),object.getLong("trackingId"),object.getDouble("speed"));
        return coordinate;
    }
}
