package be.kdg.runtracker.frontend.dto;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.User;

import java.util.ArrayList;
import java.util.List;

public class ShortCompetition {

    private Long competitionId;
    private Goal goal;
    private ShortUser userCreated;
    private ShortUser userWon;
    private List<ShortUser> usersRun;
    private String topic;
    private String name;

    public ShortCompetition(Competition competition) {
        this.competitionId = competition.getCompetitionId();
        this.goal = competition.getGoal();
        this.userCreated = new ShortUser(competition.getUserCreated());
        if (competition.getUserWon() != null)
            this.userWon = new ShortUser(competition.getUserWon());
        this.usersRun = new ArrayList<>();
        this.topic = competition.getTopic();
        this.name = competition.getName();

        for (User user : competition.getUsersRun()) {
            usersRun.add(new ShortUser(user));
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

    public String getTopic() {
        return topic;
    }
}
