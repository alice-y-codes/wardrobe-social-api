package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String provider;

    @Column
    private String profilePicture;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Outfit> outfits = new ArrayList<>();


    // private string Bio
    // private List<String> socialMediaLinks

    // Size-related fields
//    private String bodyType; // e.g., "petite", "athletic", "plus-size"
//    private double height; // Height in centimeters or inches
//    private double weight; // Weight in kilograms or pounds
//    private String shoeSize; // Shoe size, could be a String for various sizing systems
//    private String clothingSize; // Clothing size, e.g., "S", "M", "L", or specific measurements

}