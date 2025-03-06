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
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import com.yalice.wardrobe_social_app.services.helpers.PostServiceHelper;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostServiceImpl extends BaseService implements PostService {

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
        Profile profile = profileService.getProfileEntityById(profileId);
        if (profile == null) {
            throw new ResourceNotFoundException("Profile not found with ID: " + profileId);
        }

        Outfit outfit = outfitService.getOutfitEntityById(postDto.getOutfitId());
        if (outfit == null) {
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

        return convertToPostResponseDto(post);
    }

    @Override
    @Transactional
    public PostResponseDto getPost(Long postId, Long viewerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        if (!postServiceHelper.isPostAccessibleToUser(post, viewerId)) {
            throw new PostAccessException("Post is not accessible to the viewer");
        }

        return convertToPostResponseDto(post);
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(Long postId, Long profileId, PostDto postDto) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        if (!existingPost.getProfile().getId().equals(profileId)) {
            throw new PostAccessException("You are not authorized to update this post.");
        }

        existingPost.setContent(postDto.getContent());
        existingPost.setVisibility(Post.PostVisibility.valueOf(postDto.getVisibility()));

        if (!existingPost.getOutfit().getId().equals(postDto.getOutfitId())) {
            Outfit newOutfit = outfitService.getOutfitEntityById(postDto.getOutfitId());
            if (newOutfit == null) {
                throw new ResourceNotFoundException("Outfit not found with ID: " + postDto.getOutfitId());
            }
            existingPost.setOutfit(newOutfit);
        }

        Post updatedPost = postRepository.saveAndFlush(existingPost);

        return convertToPostResponseDto(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long profileId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        if (!post.getProfile().getId().equals(profileId)) {
            throw new PostAccessException("Only the post owner can delete the post");
        }

        postRepository.deleteById(postId);
    }

    @Override
    @Transactional
    public boolean toggleLikePost(Long postId, Long profileId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        Profile profile = profileService.getProfileEntityById(profileId);
        if (profile == null) {
            throw new ResourceNotFoundException("Profile not found with ID: " + profileId);
        }

        boolean isLiked = post.getLikes().contains(profileId);

        boolean hasProfileLiked = postServiceHelper.hasUserLikedPost(postId, profileId);

        if (isLiked && !hasProfileLiked) {
            Like like = Like.builder()
                    .post(post)
                    .profile(profile)
                    .build();

            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);

            return true;
        } else if (!isLiked && hasProfileLiked) {
            Like like = likeRepository.findByPostAndProfile(post, profile)
                    .orElseThrow(() -> new ResourceNotFoundException("Like not found"));

            likeRepository.delete(like);
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);

            return true;
        }

        return false;
    }
}
