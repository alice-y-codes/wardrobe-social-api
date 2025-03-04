package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.Like;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.exceptions.PostAccessException;
import com.yalice.wardrobe_social_app.exceptions.PostNotFoundException;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.services.helpers.PostServiceHelper;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing {@link Post} entities.
 * This service handles the business logic for creating, retrieving, updating, deleting, liking, and unliking posts.
 */
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final OutfitService outfitService;
    private final PostServiceHelper postServiceHelper;

    /**
     * Constructor to inject dependencies for post, like, user, and outfit services.
     *
     * @param postRepository the {@link PostRepository} for interacting with post data.
     * @param likeRepository the {@link LikeRepository} for interacting with like data.
     * @param userService the {@link UserService} for interacting with user data.
     * @param outfitService the {@link OutfitService} for interacting with outfit data.
     * @param postServiceHelper the helper class for additional post-related logic.
     */
    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           LikeRepository likeRepository,
                           UserService userService,
                           OutfitService outfitService,
                           PostServiceHelper postServiceHelper) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.outfitService = outfitService;
        this.postServiceHelper = postServiceHelper;
    }

    /**
     * Creates a new post with the specified content, outfit, and visibility.
     * It associates the post with the given user and outfit.
     *
     * @param postDto the {@link PostDto} containing the details of the post to create.
     * @param userId the ID of the user creating the post.
     * @return the created post wrapped in a {@link PostResponseDto}.
     * @throws IllegalArgumentException if the user or outfit cannot be found.
     * @throws ResourceNotFoundException if the user or outfit does not exist.
     */
    @Override
    @Transactional
    public PostResponseDto createPost(Long userId, PostDto postDto) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Outfit outfit = outfitService.getOutfit(postDto.getOutfitId())
                .orElseThrow(() -> new ResourceNotFoundException("Outfit not found with ID: " + postDto.getOutfitId()));

        Post post = Post.builder()
                .user(user)
                .title(postDto.getTitle())
                .featureImage(postDto.getFeatureImage())
                .outfit(outfit)
                .content(postDto.getContent())
                .visibility(PostVisibility.valueOf(postDto.getVisibility()))
                .build();

        post = postRepository.save(post);

        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getFeatureImage(),
                post.getContent(),
                post.getOutfit().getId(),
                post.getVisibility().name(),
                user.getUsername()
        );
    }

    /**
     * Retrieves a post by its ID, if accessible by the specified viewer.
     * This checks if the post's visibility allows the viewer to access it.
     *
     * @param postId the ID of the post to retrieve.
     * @param viewerId the ID of the viewer attempting to access the post.
     * @return the post wrapped in a {@link PostResponseDto}.
     * @throws ResourceNotFoundException if the post is not found.
     * @throws PostAccessException if the post is not accessible to the viewer.
     */
    @Override
    @Transactional
    public PostResponseDto getPost(Long postId, Long viewerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        if (!postServiceHelper.isPostAccessibleToUser(post, viewerId)) {
            throw new PostAccessException("Post is not accessible to the viewer");
        }

        User user = post.getUser();

        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getFeatureImage(),
                post.getContent(),
                post.getOutfit().getId(),
                post.getVisibility().name(),
                user.getUsername()
        );
    }

    /**
     * Updates an existing post with new content, outfit, or visibility.
     * Only the post owner can update the post.
     *
     * @param postId the ID of the post to update.
     * @param userId the ID of the user requesting the update.
     * @param postDto the updated post data.
     * @return the updated post wrapped in a {@link PostResponseDto}.
     * @throws ResourceNotFoundException if the post is not found.
     * @throws PostAccessException if the user is not authorized to update the post.
     */
    @Override
    @Transactional
    public PostResponseDto updatePost(Long postId, Long userId, PostDto postDto) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        if (!existingPost.getUser().getId().equals(userId)) {
            throw new PostAccessException("You are not authorized to update this post.");
        }

        existingPost.setContent(postDto.getContent());
        existingPost.setVisibility(PostVisibility.valueOf(postDto.getVisibility()));

        if (!existingPost.getOutfit().getId().equals(postDto.getOutfitId())) {
            Outfit newOutfit = outfitService.getOutfit(postDto.getOutfitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Outfit not found with ID: " + postDto.getOutfitId()));
            existingPost.setOutfit(newOutfit);
        }

        Post updatedPost = postRepository.saveAndFlush(existingPost);

        return new PostResponseDto(
                updatedPost.getId(),
                updatedPost.getTitle(),
                updatedPost.getFeatureImage(),
                updatedPost.getContent(),
                updatedPost.getOutfit().getId(),
                updatedPost.getVisibility().name(),
                existingPost.getUser().getUsername()
        );
    }

    /**
     * Deletes a post by its ID, if the user is the owner of the post.
     *
     * @param postId the ID of the post to delete.
     * @param userId the ID of the user attempting to delete the post.
     * @throws PostNotFoundException if the post is not found.
     * @throws PostAccessException if the user is not authorized to delete the post.
     */
    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        if (!post.getUser().getId().equals(userId)) {
            throw new PostAccessException("Only the post owner can delete the post");
        }

        postRepository.deleteById(postId);
    }

    /**
     * Likes a post, incrementing the like count and creating a Like entity.
     *
     * @param postId the ID of the post to like.
     * @param userId the ID of the user liking the post.
     * @return true if the like was successful, false if the user had already liked the post.
     * @throws ResourceNotFoundException if the post or user cannot be found.
     */
    @Override
    @Transactional
    public boolean likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

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
     * @param postId the ID of the post to unlike.
     * @param userId the ID of the user unliking the post.
     * @return true if the unlike was successful, false if the user had not liked the post before.
     * @throws ResourceNotFoundException if the post or user cannot be found.
     */
    @Override
    @Transactional
    public boolean unlikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

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
