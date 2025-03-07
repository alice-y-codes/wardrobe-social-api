package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.Friendship.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friendship, Long> {
    boolean existsBySenderIdAndRecipientId(Long senderId, Long recipientId);
    List<Friendship> findByRecipientIdAndStatus(Long recipientId, FriendshipStatus status);
    List<Friendship> findBySenderIdOrRecipientIdAndStatus(Long userId, FriendshipStatus status);
    Optional<Friendship> findFriendshipBetweenUsers(Long userId1, Long userId2);
}