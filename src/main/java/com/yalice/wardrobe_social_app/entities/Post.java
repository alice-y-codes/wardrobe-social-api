package com.yalice.wardrobe_social_app.entities;

import com.yalice.wardrobe_social_app.enums.PostVisibility;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outfit_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Outfit outfit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostVisibility visibility;

    private String featureImage;

    @Column(nullable = false)
    @Builder.Default
    private Integer likeCount = 0;
}
