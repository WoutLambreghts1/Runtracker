package be.kdg.runtracker.frontend.dto;

import be.kdg.runtracker.backend.dom.competition.Competition;
import be.kdg.runtracker.backend.dom.competition.CompetitionType;
import be.kdg.runtracker.backend.dom.competition.Goal;
import be.kdg.runtracker.backend.dom.profile.User;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ShortCompetition {

    private Long competitionId;
    private CompetitionType competitionType;
    private Date deadline;
    private int maxParticipants;
    private boolean isFinished;
    private Goal goal;
    private ShortUser userCreated;
    private ShortUser userWon;
    private List<ShortUser> usersRun;

    public ShortCompetition(Competition competition) {
        this.competitionId = competition.getCompetitionId();
        this.competitionType = competition.getCompetitionType();
        this.deadline = competition.getDeadline();
        this.maxParticipants = competition.getMaxParticipants();
        this.isFinished = competition.isFinished();
        this.goal = competition.getGoal();
        this.userCreated = new ShortUser(competition.getUserCreated());
        if (competition.getUserWon() != null)
            this.userWon = new ShortUser(competition.getUserWon());
        this.usersRun = new ArrayList<>();

        for (User user : competition.getUsersRun()) {
            usersRun.add(new ShortUser(user));
        }
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public CompetitionType getCompetitionType() {
        return competitionType;
    }

    public Date getDeadline() {
        return deadline;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public boolean isFinished() {
        return isFinished;
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
}
