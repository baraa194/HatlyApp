package com.Hatly.Backend.resturant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "member_branch_access")
public class MemberBranchAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="member_id")
    private RestaurantMember member;

    @ManyToOne
    @JoinColumn(name="branch_id")
    private RestaurantBranch branch;
}