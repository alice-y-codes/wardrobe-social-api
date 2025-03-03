package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Like;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.exceptions.PostAccessException;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.helpers.PostServiceHelper;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
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
    private final OutfitService outfitService;
    private final PostServiceHelper postServiceHelper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           LikeRepository likeRepository,
                           UserService userService,
                           OutfitService outfitService, PostServiceHelper postServiceHelper) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.outfitService = outfitService;
        this.postServiceHelper = postServiceHelper;
    }

    /**
     * Creates a new post with the specified content, outfit, and visibility.
     *
     * @param userId the ID of the user creating the post
     * @param content the content of the post
     * @param outfitId the ID of the outfit associated with the post
     * @param visibility the visibility of the post (e.g., PUBLIC, PRIVATE, FRIENDS_ONLY)
     * @return the created post
     * @throws IllegalArgumentException if the user or outfit cannot be found
     */
    @Override
    @Transactional
    public Post createPost(Long userId, String content, Long outfitId, PostVisibility visibility) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Outfit outfit = outfitService.getOutfit(outfitId)
                .orElseThrow(() -> new ResourceNotFoundException("Outfit not found with ID: " + outfitId));

        Post post = Post.builder()
                .user(user)
                .outfit(outfit)
                .content(content)
                .visibility(visibility)
                .build();

        return postRepository.save(post);
    }

    /**
     * Retrieves a post by its ID, if accessible by the specified viewer.
     *
     * @param postId the ID of the post to retrieve
     * @param viewerId the ID of the viewer attempting to access the post
     * @return an Optional containing the post if accessible, otherwise an empty Optional
     */
    @Override
    @Transactional
    public Post getPost(Long postId, Long viewerId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        if (!postServiceHelper.isPostAccessibleToUser(post, viewerId)) {
            throw new PostAccessException("Post is not accessible to the viewer");
        }
        return post;
    }

    /**
     * Updates an existing post with new content, outfit, or visibility.
     *
     * @param postId the ID of the post to update
     * @param post the new post data to update with
     * @return the updated post
     * @throws ResourceNotFoundException if the post is not found
     */
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

        // Check for changes in the post fields and update if necessary
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

    /**
     * Deletes a post by its ID, if the user is the owner of the post.
     *
     * @param postId the ID of the post to delete
     * @param userId the ID of the user attempting to delete the post
     * @throws IllegalArgumentException if the user is not the owner of the post
     */
    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        // Ensure only the post owner can delete the post
        if (!post.getUser().getId().equals(userId)) {
            throw new PostAccessException("Only the post owner can delete the post");
        }

        postRepository.deleteById(postId);
    }

    /**
     * Likes a post, incrementing the like count and creating a Like entity.
     *
     * @param postId the ID of the post to like
     * @param userId the ID of the user liking the post
     * @return true if the like was successful, false if the user had already liked the post
     */
    @Override
    @Transactional
    public boolean likePost(Long postId, Long userId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Optional<User> userOptional = userService.findById(userId);

        if (postOptional.isEmpty() || userOptional.isEmpty()) {
            throw new ResourceNotFoundException("Post or user not found");
        }

        Post post = postOptional.get();
        User user = userOptional.get();

        // Check if the user has already liked the post using the helper method
        if (postServiceHelper.hasUserLikedPost(postId, userId)) {
            return false;
        }

        Like like = Like.builder()
                .post(post)
                .user(user)
                .build();

        likeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);

        return true;
    }

    /**
     * Unlikes a post, decrementing the like count and removing the Like entity.
     *
     * @param postId the ID of the post to unlike
     * @param userId the ID of the user unliking the post
     * @return true if the unlike was successful, false if the user had not liked the post before
     */
    @Override
    @Transactional
    public boolean unlikePost(Long postId, Long userId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Optional<User> userOptional = userService.findById(userId);

        if (postOptional.isEmpty() || userOptional.isEmpty()) {
            throw new ResourceNotFoundException("Post or user not found");
        }

        Post post = postOptional.get();
        User user = userOptional.get();

        if (!postServiceHelper.hasUserLikedPost(postId, userId)) {
            return false;
        }

        Like like = likeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found"));

        likeRepository.delete(like);
        post.setLikeCount(post.getLikeCount() - 1);
        postRepository.save(post);

        return true;
    }
}
