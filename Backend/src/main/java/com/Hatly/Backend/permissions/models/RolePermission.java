package com.Hatly.Backend.permissions.models;

import com.Hatly.Backend.resturant.enums.RestaurantRole;
import jakarta.persistence.*;

@Entity
@Table(name = "role_permissions")
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RestaurantRole role;

    @ManyToOne
    @JoinColumn(name="permission_id")
    private Permission permission;
}