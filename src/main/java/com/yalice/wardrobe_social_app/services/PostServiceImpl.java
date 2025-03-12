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
import com.yalice.wardrobe_social_app.mappers.PostMapper;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.helpers.PostServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final ProfileService profileService;
    private final OutfitService outfitService;
    private final PostServiceHelper postServiceHelper;
    private final PostMapper postMapper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           LikeRepository likeRepository,
                           ProfileService profileService,
                           OutfitService outfitService,
                           PostServiceHelper postServiceHelper,
                           PostMapper postMapper) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.profileService = profileService;
        this.outfitService = outfitService;
        this.postServiceHelper = postServiceHelper;
        this.postMapper = postMapper;
    }

    @Override
    @Transactional
    public PostResponseDto createPost(Long profileId, PostDto postDto) {
        logger.info("Creating post for profileId: {}", profileId);

        Profile profile = validateProfile(profileId);
        Outfit outfit = validateOutfit(postDto.getOutfitId());

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

        return postMapper.toResponseDto(post);
    }

    @Override
    @Transactional
    public PostResponseDto getPost(Long postId, Long viewerId) {
        logger.info("Fetching post with ID: {} for viewerId: {}", postId, viewerId);

        Post post = validatePost(postId);
        validatePostAccess(post, viewerId);

        return postMapper.toResponseDto(post);
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(Long postId, Long profileId, PostDto postDto) {
        logger.info("Updating post with ID: {} for profileId: {}", postId, profileId);

        Post existingPost = validatePost(postId);
        validateOwnership(existingPost, profileId);

        existingPost.setContent(postDto.getContent());
        existingPost.setVisibility(Post.PostVisibility.valueOf(postDto.getVisibility()));

        if (!existingPost.getOutfit().getId().equals(postDto.getOutfitId())) {
            Outfit newOutfit = validateOutfit(postDto.getOutfitId());
            existingPost.setOutfit(newOutfit);
        }

        Post updatedPost = postRepository.save(existingPost);
        logger.info("Post updated successfully with ID: {}", updatedPost.getId());

        return postMapper.toResponseDto(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long profileId) {
        logger.info("Deleting post with ID: {} by profileId: {}", postId, profileId);

        Post post = validatePost(postId);
        validateOwnership(post, profileId);

        postRepository.deleteById(postId);
        logger.info("Post with ID: {} deleted successfully", postId);
    }

    @Override
    @Transactional
    public boolean toggleLikePost(Long postId, Long profileId) {
        logger.info("Toggling like for post with ID: {} by profileId: {}", postId, profileId);

        Post post = validatePost(postId);
        Profile profile = validateProfile(profileId);

        boolean hasProfileLiked = postServiceHelper.hasProfileLikedPost(postId, profileId);
        logger.debug("Has profile ID: {} liked post with ID: {}? {}", profileId, postId, hasProfileLiked);

        if (hasProfileLiked) {
            return unlikePost(post, profile);
        } else {
            return likePost(post, profile);
        }
    }

    private Profile validateProfile(Long profileId) {
        Profile profile = profileService.getProfileEntityById(profileId);

        if (profile == null) {
            throw new ResourceNotFoundException("Profile not found with ID: " + profileId);
        }

        return profile;
    }

    private Outfit validateOutfit(Long outfitId) {
        Outfit outfit = outfitService.getOutfitEntityById(outfitId);

        if (outfit == null) {
            throw new ResourceNotFoundException("Outfit not found with ID: " + outfitId);
        }

        return outfit;
    }

    private Post validatePost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));
    }

    private void validatePostAccess(Post post, Long viewerId) {
        if (!postServiceHelper.isPostAccessibleToUser(post, viewerId)) {
            logger.error("Post with ID: {} is not accessible to viewerId: {}", post.getId(), viewerId);
            throw new PostAccessException("Post is not accessible to the viewer");
        }
    }

    private void validateOwnership(Post post, Long profileId) {
        if (!post.getProfile().getId().equals(profileId)) {
            logger.error("Profile ID: {} is not authorized to modify post ID: {}", profileId, post.getId());
            throw new PostAccessException("You are not authorized to modify this post.");
        }
    }

    private boolean likePost(Post post, Profile profile) {
        Like like = Like.builder()
                .post(post)
                .profile(profile)
                .build();

        likeRepository.save(like);
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
        logger.info("Post with ID: {} liked by profile ID: {}", post.getId(), profile.getId());

        return true;
    }

    private boolean unlikePost(Post post, Profile profile) {
        Like like = likeRepository.findByPostAndProfile(post, profile)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found"));

        likeRepository.delete(like);
        post.setLikeCount(post.getLikeCount() - 1);
        postRepository.save(post);
        logger.info("Post with ID: {} unliked by profile ID: {}", post.getId(), profile.getId());

        return true;
    }
}
