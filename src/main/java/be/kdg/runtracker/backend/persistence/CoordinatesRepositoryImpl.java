package be.kdg.runtracker.backend.persistence;

import be.kdg.runtracker.backend.dom.tracking.Coordinate;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Wout on 4/02/2017.
 */
public class CoordinatesRepositoryImpl implements CoordinatesRepository{

    private MongoClient mongoClient;
    private MongoDatabase database;

    public CoordinatesRepositoryImpl() {
        configMongo();
    }

    @Override
    public List<Coordinate> readCoordinatesByTrackingId(long trackingId) {
        MongoCollection<Document> collection = database.getCollection(String.valueOf(trackingId));
        List<Document> documents = (List<Document>) collection.find().into(new ArrayList<Document>());
        return documents.stream().map(d -> docToCoordinate(d)).sorted(Comparator.comparing(t -> t.getTime())).collect(Collectors.toList());
    }

    @Override
    public void createCoordinatesCollection(long trackingId, List<Coordinate> coordinates) {
        database.createCollection(String.valueOf(trackingId));
        List<Document> documents = coordinates.stream().map(c -> coordinateToDoc(c)).collect(Collectors.toList());
        database.getCollection(String.valueOf(trackingId)).insertMany(documents);
    }

    @Override
    public void deleteCoordinatesCollection(long trackingId) {
        database.getCollection(String.valueOf(trackingId)).drop();
    }

    @Override
    public void addCoordinateToCollection(long trackingId, Coordinate coordinate) {
        database.getCollection(String.valueOf(trackingId)).insertOne(coordinateToDoc(coordinate));
    }


    private Document coordinateToDoc(Coordinate coordinate){
        Document doc = new Document();

        doc.put("lat",coordinate.getLat());
        doc.put("lon",coordinate.getLon());
        doc.put("time",coordinate.getTime().toSecondOfDay());
        doc.put("trackingID",coordinate.getTrackingID());
        doc.put("speed",coordinate.getSpeed());

        return doc;
    }

    private Coordinate docToCoordinate(Document document){
        Coordinate coordinate = new Coordinate(document.getDouble("lat"),document.getDouble("lon"),LocalTime.ofSecondOfDay(document.getInteger("time")),document.getLong("trackingID"),document.getDouble("speed"));
        return coordinate;
    }

    private void configMongo(){
        //mongoClient = new MongoClient("localhost", 27017);
        mongoClient = new MongoClient(
                new MongoClientURI( "mongodb://runtracking:Runtracker2017@ds135039.mlab.com:35039/runtrackerdb" )
        );
        database = mongoClient.getDatabase("runtrackerdb");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mongoClient.close();
    }
}
