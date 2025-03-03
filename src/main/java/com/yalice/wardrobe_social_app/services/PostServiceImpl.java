package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.PostDto;
import com.yalice.wardrobe_social_app.dtos.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.Like;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.exceptions.PostAccessException;
import com.yalice.wardrobe_social_app.exceptions.PostNotFoundException;
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
     *
     * @param postDto the post data transfer object containing post details
     * @param userId the ID of the user creating the post
     * @return the created post wrapped in a PostResponseDto
     * @throws IllegalArgumentException if the user or outfit cannot be found
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

        // Return the Post wrapped in PostResponseDto
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
     *
     * @param postId the ID of the post to retrieve
     * @param viewerId the ID of the viewer attempting to access the post
     * @return an Optional containing the post if accessible, otherwise an empty Optional
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
     *
     * @param postId the ID of the post to update
     * @param postDto the updated post data
     * @return the updated post wrapped in PostResponseDto
     * @throws ResourceNotFoundException if the post is not found
     */
    @Override
    @Transactional
    public PostResponseDto updatePost(Long postId, Long userId, PostDto postDto) {
        // Fetch the post from the database
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        // Ensure that only the owner of the post can update it
        if (!existingPost.getUser().getId().equals(userId)) {
            throw new PostAccessException("You are not authorized to update this post.");
        }

        // Update the post content
        existingPost.setContent(postDto.getContent());
        existingPost.setVisibility(PostVisibility.valueOf(postDto.getVisibility()));

        // If the outfit ID is different, update the outfit
        if (!existingPost.getOutfit().getId().equals(postDto.getOutfitId())) {
            Outfit newOutfit = outfitService.getOutfit(postDto.getOutfitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Outfit not found with ID: " + postDto.getOutfitId()));
            existingPost.setOutfit(newOutfit);
        }

        // Save the updated post
        Post updatedPost = postRepository.saveAndFlush(existingPost);

        // Return the updated post as a response DTO
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
     * @param postId the ID of the post to delete
     * @param userId the ID of the user attempting to delete the post
     * @throws IllegalArgumentException if the user is not the owner of the post
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
     * @param postId the ID of the post to like
     * @param userId the ID of the user liking the post
     * @return true if the like was successful, false if the user had already liked the post
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
     * @param postId the ID of the post to unlike
     * @param userId the ID of the user unliking the post
     * @return true if the unlike was successful, false if the user had not liked the post before
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
