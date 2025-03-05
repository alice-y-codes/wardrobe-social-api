package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.FriendRequestDto;
import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/friends")
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final UserSearchService userSearchService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService, UserSearchService userSearchService) {
        this.friendshipService = friendshipService;
        this.userSearchService = userSearchService;
    }

    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestBody FriendRequestDto request) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        Friendship friendship = friendshipService.sendFriendRequest(currentUserId, request.getRecipientId());
        return ResponseEntity.ok(friendship);
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody FriendRequestDto request) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        Friendship friendship = friendshipService.acceptFriendRequest(request.getRequestId(), currentUserId);
        return ResponseEntity.ok(friendship);
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectFriendRequest(@RequestBody FriendRequestDto request) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        friendshipService.rejectFriendRequest(request.getRequestId(), currentUserId);
        return ResponseEntity.ok("Friend request rejected");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFriend(@RequestBody FriendRequestDto request) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        friendshipService.removeFriend(currentUserId, request.getFriendId());
        return ResponseEntity.ok("Friend removed");
    }

    @GetMapping
    public ResponseEntity<?> getFriends() {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        List<User> friends = friendshipService.getFriends(currentUserId);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingFriendRequests() {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        List<Friendship> pendingRequests = friendshipService.getPendingFriendRequests(currentUserId);
        return ResponseEntity.ok(pendingRequests);
    }

    /**
     * Utility method to get the current authenticated user
     */
    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String username = authentication.getName();
        return userSearchService.findUserByUsername(username);
    }
}