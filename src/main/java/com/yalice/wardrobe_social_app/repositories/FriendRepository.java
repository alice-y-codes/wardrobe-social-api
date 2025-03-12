package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.Friendship.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friendship, Long> {
    boolean existsBySenderIdAndRecipientId(Long senderId, Long recipientId);
    List<Friendship> findByRecipientIdAndStatus(Long recipientId, FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.sender.id = :userId1 AND f.recipient.id = :userId2) OR (f.sender.id = :userId2 AND f.recipient.id = :userId1)")
    Optional<Friendship> findFriendshipBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    @Query("SELECT f FROM Friendship f WHERE (f.sender.id = :userId OR f.recipient.id = :userId) AND f.status = :status")
    List<Friendship> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FriendshipStatus status);
}