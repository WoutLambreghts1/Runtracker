package be.kdg.runtracker.backend.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by Wout on 7/02/2017.
 */
@Configuration
@EnableMongoRepositories
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "runtrackerdb";
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(
                new MongoClientURI( "mongodb://runtracking:Runtracker2017@schadronds.synology.me:27017/runtrackerdb" )
        );
    }

}
