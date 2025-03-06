package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.friendship.FriendRequestDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
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
        return handleFriendRequestAction(
                () -> friendService.sendFriendRequest(getLoggedInUser().getId(), recipientId),
                "send friend request", recipientId
        );
    }

    /**
     * Accepts a friend request.
     *
     * @param requestId the ID of the friend request to accept
     * @return ResponseEntity containing the friendship
     */
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<ApiResponse<FriendResponseDto>> acceptFriendRequest(@PathVariable Long requestId) {
        return handleFriendRequestAction(
                () -> friendService.acceptFriendRequest(getLoggedInUser().getId(), requestId),
                "accept friend request", requestId
        );
    }

    /**
     * Rejects a friend request.
     *
     * @param requestId the ID of the friend request to reject
     * @return ResponseEntity with a success message
     */
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectFriendRequest(@PathVariable Long requestId) {
        return handleFriendRequestAction(
                () -> {
                    friendService.rejectFriendRequest(getLoggedInUser().getId(), requestId);
                    return null;
                },
                "reject friend request", requestId
        );
    }

    /**
     * Gets all pending friend requests for the current user.
     *
     * @return ResponseEntity containing the list of friend requests
     */
    @GetMapping("/requests/pending")
    public ResponseEntity<ApiResponse<List<FriendRequestDto>>> getPendingFriendRequests() {
        return handleGetFriendRequestsAction(
                () -> friendService.getPendingFriendRequests(getLoggedInUser().getId()), "pending friend requests"
        );
    }

    /**
     * Gets all friends of the current user.
     *
     * @return ResponseEntity containing the list of friendships
     */
    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<List<FriendResponseDto>>> getFriends() {
        return handleGetFriendRequestsAction(
                () -> friendService.getFriends(getLoggedInUser().getId()), "friends"
        );
    }

    // Helper method to handle friend request actions (send, accept, reject)
    private <T> ResponseEntity<ApiResponse<T>> handleFriendRequestAction(
            FriendRequestAction<T> action, String actionName, Long requestId) {
        User currentUser = getLoggedInUser();
        try {
            T response = action.execute();
            logger.info("Successfully performed '{}' action for user ID: {} and target ID: {}",
                    actionName, currentUser.getId(), requestId);
            return createSuccessResponse(actionName + " successful", response);
        } catch (Exception e) {
            logger.error("Failed to {} for user ID: {} and target ID: {}", actionName, currentUser.getId(), requestId, e);
            return createInternalServerErrorResponse("Failed to " + actionName);
        }
    }

    // Helper method to handle retrieval of friend requests (pending or friends)
    private <T> ResponseEntity<ApiResponse<T>> handleGetFriendRequestsAction(
            FriendRequestsRetriever<T> retriever, String requestType) {
        User currentUser = getLoggedInUser();
        try {
            T response = retriever.retrieve();
            logger.info("Successfully retrieved {} for user ID: {}", requestType, currentUser.getId());
            return createSuccessResponse(requestType + " retrieved successfully", response);
        } catch (Exception e) {
            logger.error("Failed to retrieve {} for user ID: {}", requestType, currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to retrieve " + requestType);
        }
    }

    // Functional interface for friend request actions
    @FunctionalInterface
    interface FriendRequestAction<T> {
        T execute() throws Exception;
    }

    // Functional interface for retrieving friend requests or friendships
    @FunctionalInterface
    interface FriendRequestsRetriever<T> {
        T retrieve() throws Exception;
    }
}
