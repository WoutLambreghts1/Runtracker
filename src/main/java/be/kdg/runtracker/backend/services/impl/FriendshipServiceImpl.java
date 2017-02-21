package be.kdg.runtracker.backend.services.impl;

import be.kdg.runtracker.backend.dom.profile.Friendship;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.persistence.api.FriendshipRepository;
import be.kdg.runtracker.backend.services.api.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Wout on 21/02/2017.
 */
@Service("FriendshipService")
@Transactional
public class FriendshipServiceImpl implements FriendshipService {

    private FriendshipRepository friendshipRepository;

    @Autowired
    public FriendshipServiceImpl(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    @Override
    public List<Friendship> findAllFriendships() {
        return friendshipRepository.findAll();
    }

    @Override
    public Friendship findFriendshipByUserAndFriend(User user, User friend) {
        return user.getFriendships().stream().filter(f -> f.getFriend().equals(friend)).findFirst().get();
    }

    @Override
    public void saveFriendship(Friendship friendship) {
        friendshipRepository.save(friendship);
    }

    @Override
    public void deleteFriendshipByFriendshipId(long friendshipId) {
        friendshipRepository.delete(friendshipId);
    }

    @Override
    public void deleteFriendship(Friendship friendship) {
        friendshipRepository.delete(friendship);
    }
}
