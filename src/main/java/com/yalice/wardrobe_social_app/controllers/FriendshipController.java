package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.friendship.FriendRequestDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendshipResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
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

    private final FriendshipService friendshipService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService, AuthUtils authUtils) {
        super(authUtils);
        this.friendshipService = friendshipService;
    }

    /**
     * Sends a friend request to another user.
     *
     * @param recipientId the ID of the user to send the request to
     * @return ResponseEntity containing the friend request
     */
    @PostMapping("/requests")
    public ResponseEntity<ApiResponse<FriendRequestDto>> sendFriendRequest(@RequestParam Long recipientId) {
        logger.info("Attempting to send friend request to user ID: {}", recipientId);

        User currentUser = getLoggedInUser();
        try {
            FriendRequestDto request = friendshipService.sendFriendRequest(currentUser.getId(), recipientId);
            logger.info("Successfully sent friend request from user ID: {} to user ID: {}",
                    currentUser.getId(), recipientId);
            return createSuccessResponse("Friend request sent successfully", request);
        } catch (Exception e) {
            logger.error("Failed to send friend request from user ID: {} to user ID: {}",
                    currentUser.getId(), recipientId, e);
            return createInternalServerErrorResponse("Failed to send friend request");
        }
    }

    /**
     * Accepts a friend request.
     *
     * @param requestId the ID of the friend request to accept
     * @return ResponseEntity containing the friendship
     */
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<ApiResponse<FriendshipResponseDto>> acceptFriendRequest(@PathVariable Long requestId) {
        logger.info("Attempting to accept friend request with ID: {}", requestId);

        User currentUser = getLoggedInUser();
        try {
            FriendshipResponseDto friendship = friendshipService.acceptFriendRequest(currentUser.getId(), requestId);
            logger.info("Successfully accepted friend request with ID: {} by user ID: {}",
                    requestId, currentUser.getId());
            return createSuccessResponse("Friend request accepted successfully", friendship);
        } catch (Exception e) {
            logger.error("Failed to accept friend request with ID: {} by user ID: {}",
                    requestId, currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to accept friend request");
        }
    }

    /**
     * Rejects a friend request.
     *
     * @param requestId the ID of the friend request to reject
     * @return ResponseEntity with a success message
     */
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectFriendRequest(@PathVariable Long requestId) {
        logger.info("Attempting to reject friend request with ID: {}", requestId);

        User currentUser = getLoggedInUser();
        try {
            friendshipService.rejectFriendRequest(currentUser.getId(), requestId);
            logger.info("Successfully rejected friend request with ID: {} by user ID: {}",
                    requestId, currentUser.getId());
            return createSuccessResponse("Friend request rejected successfully", null);
        } catch (Exception e) {
            logger.error("Failed to reject friend request with ID: {} by user ID: {}",
                    requestId, currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to reject friend request");
        }
    }

    /**
     * Gets all pending friend requests for the current user.
     *
     * @return ResponseEntity containing the list of friend requests
     */
    @GetMapping("/requests/pending")
    public ResponseEntity<ApiResponse<List<FriendRequestDto>>> getPendingFriendRequests() {
        logger.info("Retrieving pending friend requests for current user");

        User currentUser = getLoggedInUser();
        try {
            List<FriendRequestDto> requests = friendshipService.getPendingFriendRequests(currentUser.getId());
            logger.info("Successfully retrieved {} pending friend requests for user ID: {}",
                    requests.size(), currentUser.getId());
            return createSuccessResponse("Pending friend requests retrieved successfully", requests);
        } catch (Exception e) {
            logger.error("Failed to retrieve pending friend requests for user ID: {}",
                    currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to retrieve pending friend requests");
        }
    }

    /**
     * Gets all friends of the current user.
     *
     * @return ResponseEntity containing the list of friendships
     */
    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<List<FriendshipResponseDto>>> getFriends() {
        logger.info("Retrieving friends for current user");

        User currentUser = getLoggedInUser();
        try {
            List<FriendshipResponseDto> friendships = friendshipService.getFriends(currentUser.getId());
            logger.info("Successfully retrieved {} friends for user ID: {}",
                    friendships.size(), currentUser.getId());
            return createSuccessResponse("Friends retrieved successfully", friendships);
        } catch (Exception e) {
            logger.error("Failed to retrieve friends for user ID: {}", currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to retrieve friends");
        }
    }
}