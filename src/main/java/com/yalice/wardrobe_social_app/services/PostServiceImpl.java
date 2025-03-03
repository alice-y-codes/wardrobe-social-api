package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Like;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final FriendshipService friendshipService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           LikeRepository likeRepository, UserService userService,
                           FriendshipService friendshipService) {
        this.postRepository = postRepository;
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

        // Check if the viewer has access to the post
        if (isPostAccessibleToUser(post, viewerId)) {
            return Optional.of(post);
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public Post updatePost(Long postId, Post post) {
        if (postId == null) {
            throw new IllegalArgumentException("Post ID cannot be null");
        }

        if (post == null) {
            throw new IllegalArgumentException("Post object cannot be null");
        }

        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        User user = existingPost.getUser();

        boolean hasChanges = false;
        if (!existingPost.getContent().equals(post.getContent())) {
            existingPost.setContent(post.getContent());
            hasChanges = true;
        }
        if (!existingPost.getOutfit().equals(post.getOutfit())) {
            existingPost.setOutfit(post.getOutfit());
            hasChanges = true;
        }
        if (!existingPost.getVisibility().equals(post.getVisibility())) {
            existingPost.setVisibility(post.getVisibility());
            hasChanges = true;
        }


        if (!hasChanges) {
            return existingPost;
        }

        Post updatedPost = Post.builder()
                .id(postId)
                .user(user)
                .content(post.getContent())
                .outfit(post.getOutfit())
                .visibility(post.getVisibility())
                .build();

        return postRepository.saveAndFlush(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Only the post owner can delete the post");
        }

        postRepository.deleteById(postId);
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
