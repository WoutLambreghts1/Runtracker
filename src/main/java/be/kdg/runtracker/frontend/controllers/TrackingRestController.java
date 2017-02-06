package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.persistence.TrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RepositoryRestController
@RequestMapping("/api/{userId}/trackings/")
public class TrackingRestController {

    private TrackingRepository trackingRepository;

    @Autowired
    public TrackingRestController(TrackingRepository trackingRepository) {
        this.trackingRepository = trackingRepository;
    }

    protected TrackingRestController() { }

}
