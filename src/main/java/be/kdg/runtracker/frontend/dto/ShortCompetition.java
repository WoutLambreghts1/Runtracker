package be.kdg.runtracker.frontend.dto;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Tracking;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ShortCompetition {

    private Long competitionId;
    private Goal goal;
    private ShortUser userCreated;
    private ShortUser userWon;
    private List<ShortUser> usersRun;
    private String name;
    private List<ShortTracking> trackings;
    private Timestamp time;

    public ShortCompetition(Competition competition) {
        this.competitionId = competition.getCompetitionId();
        this.goal = competition.getGoal();
        this.userCreated = new ShortUser(competition.getUserCreated());
        if (competition.getUserWon() != null)
            this.userWon = new ShortUser(competition.getUserWon());
        this.usersRun = new ArrayList<>();
        this.name = competition.getName();
        this.time = competition.getTime();
        this.trackings = new ArrayList<>();

        for (User user : competition.getUsersRun()) {
            usersRun.add(new ShortUser(user));
        }

        for (Tracking tracking : competition.getTrackings()) {
            trackings.add(new ShortTracking(tracking));
        }
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public Goal getGoal() {
        return goal;
    }

    public ShortUser getUserCreated() {
        return userCreated;
    }

    public ShortUser getUserWon() {
        return userWon;
    }

    public List<ShortUser> getUsersRun() {
        return usersRun;
    }

    public String getName() {
        return name;
    }

    public List<ShortTracking> getTrackings() {
        return trackings;
    }

    public Timestamp getTime() {
        return time;
    }
}
