package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.Friendship.FriendshipStatus;
import com.yalice.wardrobe_social_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByRequesterAndStatus(User requester, FriendshipStatus status);

    List<Friendship> findByRecipientAndStatus(User recipient, FriendshipStatus status);

    Optional<Friendship> findByRequesterAndRecipient(User requester, User recipient);

    @Query("SELECT f FROM Friendship f WHERE (f.requester = ?1 AND f.recipient = ?2) OR (f.requester = ?2 AND f.recipient = ?1)")
    Optional<Friendship> findFriendshipBetweenUsers(User user1, User user2);

    @Query("SELECT f FROM Friendship f WHERE (f.requester.id = ?1 OR f.recipient.id = ?1) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendshipsForUser(Long userId);

    @Query("SELECT f.recipient FROM Friendship f WHERE f.requester.id = ?1 AND f.status = 'ACCEPTED'")
    List<User> findFriendsWhoAcceptedRequestFromUser(Long userId);

    @Query("SELECT f.requester FROM Friendship f WHERE f.recipient.id = ?1 AND f.status = 'ACCEPTED'")
    List<User> findFriendsWhoSentAcceptedRequestToUser(Long userId);
}