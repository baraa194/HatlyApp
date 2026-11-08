package com.Hatly.Backend.resturant.service;


import com.Hatly.Backend.resturant.dto.BranchRequest;
import com.Hatly.Backend.resturant.dto.BranchResponse;
import com.Hatly.Backend.resturant.dto.UpdateBranchStatusRequest;
import com.Hatly.Backend.resturant.mapper.BranchResponseMapper;
import com.Hatly.Backend.resturant.model.Restaurant;
import com.Hatly.Backend.resturant.model.RestaurantBranch;
import com.Hatly.Backend.resturant.repo.RestBranchRepo;
import com.Hatly.Backend.resturant.repo.RestaurantRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RestBranchService {
    @Autowired
    RestBranchRepo restBranchRepo;
    @Autowired
    BranchResponseMapper  branchMapper;
    @Autowired
    RestaurantRepo restaurantRepo;

    public List<BranchResponse> getAllRestBranches(Long restaurantId){
        List< RestaurantBranch> branches = restBranchRepo.findByRestaurantId(restaurantId);

        return branches.stream()
                .map(branch -> branchMapper.toResponse(branch))
                .toList();
    }

    public BranchResponse getRestBranchById(Long restaurantId, Long id){
        RestaurantBranch restaurantBranch = restBranchRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found for restaurant id: " + id));

        return branchMapper.toResponse(restaurantBranch);
    }
    @Transactional
    public BranchResponse createBranch(Long restaurantId, BranchRequest req) {

        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));

        RestaurantBranch branch = new RestaurantBranch();
        branch.setAddressDetailed(req.getAddressDetailed());
        branch.setLat(req.getLat());
        branch.setLng(req.getLng());
        branch.setOpenAt(req.getOpenAt());
        branch.setCloseAt(req.getCloseAt());
        branch.setDeliveryRadius(req.getDeliveryRadius());
        branch.setCurrency(req.getCurrency());
        branch.setCommission(req.getCommission());
        branch.setAcceptOrders(req.getAcceptOrders());
        branch.setLabel(req.getLabel());
        branch.setIsActive(req.getIsActive());
        branch.setRestaurant(restaurant);



        RestaurantBranch savedBranch = restBranchRepo.save(branch);
        return branchMapper.toResponse(savedBranch);
    }
    @Transactional
    public Map<String, Object> updateBranch(Long branchId, BranchRequest req) {
        RestaurantBranch branch = restBranchRepo.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + branchId));

        Restaurant restaurant = branch.getRestaurant();
        if (restaurant == null) {
            throw new RuntimeException("Branch is not linked to any restaurant");
        }

        if (req.getAddressDetailed() != null) branch.setAddressDetailed(req.getAddressDetailed());
        if (req.getLat() != null) branch.setLat(req.getLat());
        if (req.getLng() != null) branch.setLng(req.getLng());
        if (req.getDeliveryRadius() != null) branch.setDeliveryRadius(req.getDeliveryRadius());
        if (req.getCurrency() != null) branch.setCurrency(req.getCurrency());
        if (req.getOpenAt() != null) branch.setOpenAt(req.getOpenAt());
        if (req.getCloseAt() != null) branch.setCloseAt(req.getCloseAt());
        if (req.getAcceptOrders() != null) branch.setAcceptOrders(req.getAcceptOrders());
        if(req.getLabel() != null) branch.setLabel(req.getLabel());
        if(req.getCommission() != null) branch.setCommission(req.getCommission());
        if(req.getAcceptOrders() != null) branch.setAcceptOrders(req.getAcceptOrders());
        if(req.getIsActive()) branch.setIsActive(req.getIsActive());

        RestaurantBranch updatedBranch = restBranchRepo.save(branch);

        BranchResponse branchResponse = branchMapper.toResponse(updatedBranch);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Branch updated successfully");
        response.put("branch", branchResponse);

        return response;
    }
    @Transactional
    public Map<String, Object> updateBranchStatus(Long branchId, UpdateBranchStatusRequest req) {

        RestaurantBranch branch = restBranchRepo.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + branchId));


        if (req.getIsActive() != null) {
            branch.setIsActive(req.getIsActive());
        }
        if (req.getCommission() != null) {
            branch.setCommission(req.getCommission());
        }
        RestaurantBranch updatedBranch = restBranchRepo.save(branch);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Branch status updated successfully by admin");

        Map<String, Object> branchInfo = new HashMap<>();
        branchInfo.put("id", updatedBranch.getId());
        branchInfo.put("isActive", updatedBranch.getIsActive());
        branchInfo.put("acceptOrders", updatedBranch.getAcceptOrders());
        branchInfo.put("commission", updatedBranch.getCommission());

        response.put("branch", branchInfo);

        return response;
    }
    public BranchResponse getNearestBranchData(Long restaurantId, BigDecimal userLat, BigDecimal userLng) {

        RestaurantBranch nearestBranch = restBranchRepo.findNearestBranchForRestaurant(restaurantId, userLat, userLng)
                .orElseThrow(() -> new RuntimeException("Sorry, no active branches available for this restaurant nearby."));


        return branchMapper.toResponse(nearestBranch);
    }

}
