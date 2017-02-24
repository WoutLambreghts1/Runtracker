package be.kdg.runtracker.backend.services.api;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.profile.User;

import java.util.List;

public interface CompetitionService {

    List<Competition> findAllCompetitions();

    Competition findCompetitionByCompetitionId(long competitionId);

    void saveCompetition(Competition competition);

    void deleteCompetition(User user, Competition competition);

}
