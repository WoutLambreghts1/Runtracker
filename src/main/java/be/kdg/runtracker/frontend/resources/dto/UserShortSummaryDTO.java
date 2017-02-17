package be.kdg.runtracker.frontend.resources.dto;

import be.kdg.runtracker.backend.dom.profile.User;

/**
 * This class is a DTO representation of a {@link be.kdg.runtracker.backend.dom.profile.User} object.
 */
public class UserShortSummaryDTO {

    private String username;
    private String firstname;
    private String lastname;

    public UserShortSummaryDTO(User user) {
        this.username = user.getUsername();
        this.firstname = user.getLastname();
        this.lastname = user.getLastname();
    }
}
