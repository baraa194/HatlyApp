package com.Hatly.Backend.resturant.model;

import com.Hatly.Backend.resturant.enums.MemberStatus;
import com.Hatly.Backend.resturant.enums.RestaurantRole;
import com.Hatly.Backend.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "restaurant_members")
@Data
public class RestaurantMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RestaurantRole role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}