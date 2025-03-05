package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "wardrobe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Item> items = new HashSet<>();
}
