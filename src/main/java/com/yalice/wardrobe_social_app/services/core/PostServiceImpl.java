package com.yalice.wardrobe_social_app.services.core;

import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.Like;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.exceptions.PostAccessException;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.mappers.PostMapper;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import com.yalice.wardrobe_social_app.services.helpers.ImageHandlerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl extends BaseService<Post, Long> implements PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final ProfileService profileService;
    private final OutfitService outfitService;
    private final PostMapper postMapper;
    private final ImageHandlerService imageHandler;

    public PostServiceImpl(
            PostRepository postRepository,
            LikeRepository likeRepository,
            ProfileService profileService,
            OutfitService outfitService,
            PostMapper postMapper,
            ImageHandlerService imageHandler) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.profileService = profileService;
        this.outfitService = outfitService;
        this.postMapper = postMapper;
        this.imageHandler = imageHandler;
    }

    @Override
    protected JpaRepository<Post, Long> getRepository() {
        return postRepository;
    }

    @Override
    protected String getEntityName() {
        return "Post";
    }

    @Override
    @Transactional
    public PostResponseDto createPost(Long profileId, PostDto postDto, MultipartFile image) {
        logger.info("Creating post for profileId: {}", profileId);

        validationService.validateNotNull(profileId, "Profile ID");
        validationService.validateNotNull(postDto, "Post data");
        validationService.validateNotNull(postDto.getOutfitId(), "Outfit ID");

        Profile profile = profileService.getProfileEntityById(profileId);
        Outfit outfit = outfitService.getOutfitEntityById(postDto.getOutfitId());

        Post post = buildPost(postDto, profile, outfit);
        post = save(post);

        if (image != null && !image.isEmpty()) {
            post.setFeatureImage(imageHandler.handleImageUpload(image, "post", post.getId(), null));
            post = save(post);
        }

        return mapEntity(post, postMapper::toResponseDto);
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(Long postId, Long profileId, PostDto postDto, MultipartFile image) {
        logger.info("Updating post with ID: {} for profileId: {}", postId, profileId);

        Post existingPost = findById(postId);
        validationService.validateOwnership(existingPost.getProfile(), profileId, "post");

        updatePostFields(existingPost, postDto);

        if (!existingPost.getOutfit().getId().equals(postDto.getOutfitId())) {
            Outfit newOutfit = outfitService.getOutfitEntityById(postDto.getOutfitId());
            existingPost.setOutfit(newOutfit);
        }

        existingPost.setFeatureImage(
                imageHandler.handleImageUpload(image, "post", postId, existingPost.getFeatureImage()));

        return mapEntity(save(existingPost), postMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long profileId) {
        logger.info("Deleting post with ID: {} by profileId: {}", postId, profileId);

        Post post = findById(postId);
        validationService.validateOwnership(post.getProfile(), profileId, "post");

        imageHandler.handleImageDelete(post.getFeatureImage());
        delete(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getFeedPosts(Long viewerId, Pageable pageable) {
        Profile viewer = profileService.getProfileEntityById(viewerId);
        List<Long> feedProfileIds = viewer.getFollowing().stream()
                .map(Profile::getId)
                .collect(Collectors.toList());
        feedProfileIds.add(viewerId); // Include viewer's own posts
        return mapPage(postRepository.findByProfileIdInOrderByCreatedAtDesc(feedProfileIds, pageable),
                postMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId, Long viewerId) {
        Post post = findById(postId);
        validatePostAccess(post, viewerId);
        return mapEntity(post, postMapper::toResponseDto);
    }

    @Override
    @Transactional
    public boolean toggleLikePost(Long postId, Long profileId) {
        Post post = findById(postId);
        Profile profile = profileService.getProfileEntityById(profileId);

        return likeRepository.findByPostAndProfile(post, profile)
                .map(like -> {
                    likeRepository.delete(like);
                    post.setLikeCount(post.getLikeCount() - 1);
                    save(post);
                    return false;
                })
                .orElseGet(() -> {
                    Like like = Like.builder()
                            .post(post)
                            .profile(profile)
                            .build();
                    likeRepository.save(like);
                    post.setLikeCount(post.getLikeCount() + 1);
                    save(post);
                    return true;
                });
    }

    private Post buildPost(PostDto postDto, Profile profile, Outfit outfit) {
        return Post.builder()
                .profile(profile)
                .title(postDto.getTitle())
                .outfit(outfit)
                .content(postDto.getContent())
                .visibility(Post.PostVisibility.valueOf(postDto.getVisibility()))
                .build();
    }

    private void updatePostFields(Post post, PostDto postDto) {
        post.setContent(postDto.getContent());
        post.setVisibility(Post.PostVisibility.valueOf(postDto.getVisibility()));
    }

    private void validatePostAccess(Post post, Long viewerId) {
        if (!post.getProfile().getId().equals(viewerId) &&
                post.getVisibility() != Post.PostVisibility.PUBLIC) {
            throw new PostAccessException("Post is not accessible to the viewer");
        }
    }
}
