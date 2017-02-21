package be.kdg.runtracker.backend.services.api;

import be.kdg.runtracker.backend.dom.profile.Friendship;
import be.kdg.runtracker.backend.dom.profile.User;

import java.util.List;

/**
 * Created by Wout on 21/02/2017.
 */
public interface FriendshipService {
    List<Friendship> findAllFriendships();

    Friendship findFriendshipByUserAndFriend(User user, User friend);

    void saveFriendship(Friendship friendship);

    void deleteFriendshipByFriendshipId(long friendshipId);

    void deleteFriendship(Friendship friendship);

}
