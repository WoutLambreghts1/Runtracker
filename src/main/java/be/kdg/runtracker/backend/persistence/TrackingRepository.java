package be.kdg.runtracker.backend.persistence;

import be.kdg.runtracker.backend.dom.tracking.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Wout on 3/02/2017.
 */
public interface TrackingRepository extends JpaRepository<Tracking,Long> {

    Tracking findTrackingByTrackingId(long trackingId);

}
