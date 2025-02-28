package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Like;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.PostVisibility;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.CommentRepository;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedServiceImpl implements FeedService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final FriendshipService friendshipService;

    @Autowired
    public FeedServiceImpl(PostRepository postRepository, CommentRepository commentRepository,
            LikeRepository likeRepository, UserService userService,
            FriendshipService friendshipService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    @Override
    public Post createPost(Long userId, String content, Long outfitId, PostVisibility visibility) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        Post post = Post.builder()
                .user(user)
                .content(content)
                .visibility(visibility)
                .build();

        // TODO: Add outfit to post if outfitId is provided

        return postRepository.save(post);
    }

    @Override
    public Optional<Post> getPost(Long postId, Long viewerId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            return Optional.empty();
        }

        Post post = postOptional.get();
        Long postOwnerId = post.getUser().getId();

        // Check if the viewer has access to the post
        if (isPostAccessibleToUser(post, viewerId)) {
            return Optional.of(post);
        }

        return Optional.empty();
    }

    @Override
    public Page<Post> getUserFeed(Long userId, Pageable pageable) {
        // Get the user's friends
        List<User> friends = friendshipService.getFriends(userId);
        List<Long> friendIds = friends.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        // Add the user's own ID to see their own posts
        friendIds.add(userId);

        // Get posts from friends and public posts
        return postRepository.findFeedPostsForUser(friendIds, pageable);
    }

    @Override
    public Page<Post> getUserPosts(Long userId, Long viewerId, Pageable pageable) {
        // Get the user's posts
        Page<Post> userPosts = postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        // Filter posts based on visibility and viewer's relationship with the user
        if (userId.equals(viewerId)) {
            // User can see all their own posts
            return userPosts;
        } else if (friendshipService.areFriends(userId, viewerId)) {
            // Friends can see public and friends-only posts
            List<Post> filteredPosts = userPosts.getContent().stream()
                    .filter(post -> post.getVisibility() == PostVisibility.PUBLIC ||
                            post.getVisibility() == PostVisibility.FRIENDS_ONLY)
                    .collect(Collectors.toList());
            return new PageImpl<>(filteredPosts, pageable, filteredPosts.size());
        } else {
            // Others can only see public posts
            List<Post> filteredPosts = userPosts.getContent().stream()
                    .filter(post -> post.getVisibility() == PostVisibility.PUBLIC)
                    .collect(Collectors.toList());
            return new PageImpl<>(filteredPosts, pageable, filteredPosts.size());
        }
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            throw new IllegalArgumentException("Post not found with ID: " + postId);
        }

        Post post = postOptional.get();
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Only the post owner can delete the post");
        }

        postRepository.delete(post);
    }

    @Override
    public boolean likePost(Long postId, Long userId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Optional<User> userOptional = userService.findById(userId);

        if (postOptional.isEmpty() || userOptional.isEmpty()) {
            throw new IllegalArgumentException("Post or user not found");
        }

        Post post = postOptional.get();
        User user = userOptional.get();

        // Check if the user has already liked the post
        if (likeRepository.existsByPostAndUser(post, user)) {
            return false;
        }

        // Create a new like
        Like like = Like.builder()
                .post(post)
                .user(user)
                .build();

        likeRepository.save(like);

        // Update the post's like count
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);

        return true;
    }

    @Override
    public boolean unlikePost(Long postId, Long userId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Optional<User> userOptional = userService.findById(userId);

        if (postOptional.isEmpty() || userOptional.isEmpty()) {
            throw new IllegalArgumentException("Post or user not found");
        }

        Post post = postOptional.get();
        User user = userOptional.get();

        // Check if the user has liked the post
        Optional<Like> likeOptional = likeRepository.findByPostAndUser(post, user);
        if (likeOptional.isEmpty()) {
            return false;
        }

        // Delete the like
        likeRepository.deleteByPostAndUser(post, user);

        // Update the post's like count
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        postRepository.save(post);

        return true;
    }

    @Override
    public boolean hasUserLikedPost(Long postId, Long userId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Optional<User> userOptional = userService.findById(userId);

        if (postOptional.isEmpty() || userOptional.isEmpty()) {
            return false;
        }

        Post post = postOptional.get();
        User user = userOptional.get();

        return likeRepository.existsByPostAndUser(post, user);
    }

    @Override
    public long getPostLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    @Override
    public Comment addComment(Long postId, Long userId, String content) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Optional<User> userOptional = userService.findById(userId);

        if (postOptional.isEmpty() || userOptional.isEmpty()) {
            throw new IllegalArgumentException("Post or user not found");
        }

        Post post = postOptional.get();
        User user = userOptional.get();

        // Create a new comment
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            throw new IllegalArgumentException("Comment not found with ID: " + commentId);
        }

        Comment comment = commentOptional.get();

        // Check if the user is the comment owner or the post owner
        if (!comment.getUser().getId().equals(userId) && !comment.getPost().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Only the comment owner or post owner can delete the comment");
        }

        commentRepository.delete(comment);
    }

    @Override
    public List<Comment> getPostComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    @Override
    public Page<Comment> getPostComments(Long postId, Pageable pageable) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable);
    }

    private boolean isPostAccessibleToUser(Post post, Long viewerId) {
        Long postOwnerId = post.getUser().getId();

        // Post owner can always view their own posts
        if (postOwnerId.equals(viewerId)) {
            return true;
        }

        // Check post visibility
        PostVisibility visibility = post.getVisibility();

        // Public posts are accessible to everyone
        if (visibility == PostVisibility.PUBLIC) {
            return true;
        }

        // Private posts are only accessible to the owner
        if (visibility == PostVisibility.PRIVATE) {
            return false;
        }

        // FRIENDS_ONLY posts are accessible to friends
        return friendshipService.areFriends(postOwnerId, viewerId);
    }
}