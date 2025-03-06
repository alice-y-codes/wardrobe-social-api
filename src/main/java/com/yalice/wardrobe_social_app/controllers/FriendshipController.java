package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.friendship.FriendRequestDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling friendship-related operations.
 * Provides endpoints for managing friend requests and friendships.
 */
@RestController
@RequestMapping("/api/friendships")
public class FriendshipController extends ApiBaseController {

    private final FriendService friendService;

    @Autowired
    public FriendshipController(FriendService friendService, AuthUtils authUtils) {
        super(authUtils);
        this.friendService = friendService;
    }

    /**
     * Sends a friend request to another user.
     *
     * @param recipientId the ID of the user to send the request to
     * @return ResponseEntity containing the friend request
     */
    @PostMapping("/requests")
    public ResponseEntity<ApiResponse<FriendRequestDto>> sendFriendRequest(@RequestParam Long recipientId) {
        return handleEntityAction(() -> friendService.sendFriendRequest(getLoggedInUser().getId(), recipientId),
                "send", "friend request");
    }

    /**
     * Accepts a friend request.
     *
     * @param requestId the ID of the friend request to accept
     * @return ResponseEntity containing the friendship
     */
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<ApiResponse<FriendResponseDto>> acceptFriendRequest(@PathVariable Long requestId) {
        return handleEntityAction(() -> friendService.acceptFriendRequest(getLoggedInUser().getId(), requestId),
                "accept", "friend request");
    }

    /**
     * Rejects a friend request.
     *
     * @param requestId the ID of the friend request to reject
     * @return ResponseEntity with a success message
     */
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectFriendRequest(@PathVariable Long requestId) {
        return handleEntityAction(() -> {
            friendService.rejectFriendRequest(getLoggedInUser().getId(), requestId);
            return null; // No result needed for rejection
        }, "reject", "friend request");
    }

    /**
     * Gets all pending friend requests for the current user.
     *
     * @return ResponseEntity containing the list of friend requests
     */
    @GetMapping("/requests/pending")
    public ResponseEntity<ApiResponse<List<FriendRequestDto>>> getPendingFriendRequests() {
        return handleEntityAction(() -> friendService.getPendingFriendRequests(getLoggedInUser().getId()),
                "retrieve", "pending friend requests");
    }

    /**
     * Gets all friends of the current user.
     *
     * @return ResponseEntity containing the list of friendships
     */
    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<List<FriendResponseDto>>> getFriends() {
        return handleEntityAction(() -> friendService.getFriends(getLoggedInUser().getId()),
                "retrieve", "friends");
    }
}
