package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.Like;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.exceptions.PostAccessException;
import com.yalice.wardrobe_social_app.exceptions.PostNotFoundException;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import com.yalice.wardrobe_social_app.services.helpers.PostServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostServiceImpl extends BaseService implements PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final ProfileService profileService;
    private final OutfitService outfitService;
    private final PostServiceHelper postServiceHelper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           LikeRepository likeRepository,
                           ProfileService profileService,
                           OutfitService outfitService,
                           PostServiceHelper postServiceHelper) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.profileService = profileService;
        this.outfitService = outfitService;
        this.postServiceHelper = postServiceHelper;
    }

    @Override
    @Transactional
    public PostResponseDto createPost(Long profileId, PostDto postDto) {
        logger.info("Creating post for profileId: {}", profileId);
        Profile profile = profileService.getProfileEntityById(profileId);
        if (profile == null) {
            logger.error("Profile not found with ID: {}", profileId);
            throw new ResourceNotFoundException("Profile not found with ID: " + profileId);
        }

        Outfit outfit = outfitService.getOutfitEntityById(postDto.getOutfitId());
        if (outfit == null) {
            logger.error("Outfit not found with ID: {}", postDto.getOutfitId());
            throw new ResourceNotFoundException("Outfit not found with ID: " + postDto.getOutfitId());
        }

        Post post = Post.builder()
                .profile(profile)
                .title(postDto.getTitle())
                .featureImage(postDto.getFeatureImage())
                .outfit(outfit)
                .content(postDto.getContent())
                .visibility(Post.PostVisibility.valueOf(postDto.getVisibility()))
                .build();

        post = postRepository.save(post);
        logger.info("Post created successfully with ID: {}", post.getId());

        return convertToPostResponseDto(post);
    }

    @Override
    @Transactional
    public PostResponseDto getPost(Long postId, Long viewerId) {
        logger.info("Fetching post with ID: {} for viewerId: {}", postId, viewerId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with ID: {}", postId);
                    return new ResourceNotFoundException("Post not found with ID: " + postId);
                });

        if (!postServiceHelper.isPostAccessibleToUser(post, viewerId)) {
            logger.error("Post with ID: {} is not accessible to viewerId: {}", postId, viewerId);
            throw new PostAccessException("Post is not accessible to the viewer");
        }

        return convertToPostResponseDto(post);
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(Long postId, Long profileId, PostDto postDto) {
        logger.info("Updating post with ID: {} for profileId: {}", postId, profileId);
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with ID: {}", postId);
                    return new ResourceNotFoundException("Post not found with ID: " + postId);
                });

        if (!existingPost.getProfile().getId().equals(profileId)) {
            logger.error("Profile ID: {} is not authorized to update post ID: {}", profileId, postId);
            throw new PostAccessException("You are not authorized to update this post.");
        }

        existingPost.setContent(postDto.getContent());
        existingPost.setVisibility(Post.PostVisibility.valueOf(postDto.getVisibility()));

        if (!existingPost.getOutfit().getId().equals(postDto.getOutfitId())) {
            Outfit newOutfit = outfitService.getOutfitEntityById(postDto.getOutfitId());
            if (newOutfit == null) {
                logger.error("Outfit not found with ID: {}", postDto.getOutfitId());
                throw new ResourceNotFoundException("Outfit not found with ID: " + postDto.getOutfitId());
            }
            existingPost.setOutfit(newOutfit);
        }

        Post updatedPost = postRepository.saveAndFlush(existingPost);
        logger.info("Post updated successfully with ID: {}", updatedPost.getId());

        return convertToPostResponseDto(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long profileId) {
        logger.info("Deleting post with ID: {} by profileId: {}", postId, profileId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with ID: {}", postId);
                    return new PostNotFoundException("Post not found with ID: " + postId);
                });

        if (!post.getProfile().getId().equals(profileId)) {
            logger.error("Profile ID: {} is not authorized to delete post ID: {}", profileId, postId);
            throw new PostAccessException("Only the post owner can delete the post");
        }

        postRepository.deleteById(postId);
        logger.info("Post with ID: {} deleted successfully", postId);
    }

    @Override
    @Transactional
    public boolean toggleLikePost(Long postId, Long profileId) {
        logger.info("Toggling like for post with ID: {} by profileId: {}", postId, profileId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with ID: {}", postId);
                    return new ResourceNotFoundException("Post not found with ID: " + postId);
                });

        Profile profile = profileService.getProfileEntityById(profileId);
        if (profile == null) {
            logger.error("Profile not found with ID: {}", profileId);
            throw new ResourceNotFoundException("Profile not found with ID: " + profileId);
        }

        boolean hasProfileLiked = postServiceHelper.hasProfileLikedPost(postId, profileId);
        logger.debug("Has profile ID: {} liked post with ID: {}? {}", profileId, postId, hasProfileLiked);

        if (!hasProfileLiked) {
            // Like the post
            Like like = Like.builder()
                    .post(post)
                    .profile(profile)
                    .build();

            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);

            logger.info("Post with ID: {} liked by profile ID: {}", postId, profileId);
            return true;
        } else {
            // Unlike the post
            Like like = likeRepository.findByPostAndProfile(post, profile)
                    .orElseThrow(() -> new ResourceNotFoundException("Like not found"));

            likeRepository.delete(like);
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);

            logger.info("Post with ID: {} unliked by profile ID: {}", postId, profileId);
            return true;
        }
    }
}
