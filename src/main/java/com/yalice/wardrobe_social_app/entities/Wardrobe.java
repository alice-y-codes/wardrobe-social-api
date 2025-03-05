package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "wardrobes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wardrobe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link Wardrobe to Profile
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)  // Profile instead of User
    private Profile profile;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "wardrobe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Item> items;
}
