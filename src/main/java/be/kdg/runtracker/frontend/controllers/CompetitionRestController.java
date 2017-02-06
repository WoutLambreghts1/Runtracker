package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.persistence.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RepositoryRestController
@RequestMapping("/api/competitions/")
public class CompetitionRestController {

    private CompetitionRepository competitionRepository;

    @Autowired
    public CompetitionRestController(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    protected CompetitionRestController() { }

}
