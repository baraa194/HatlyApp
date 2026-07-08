package com.Hatly.Backend.resturant.controller;

import com.Hatly.Backend.resturant.dto.BranchRequest;
import com.Hatly.Backend.resturant.dto.BranchResponse;
import com.Hatly.Backend.resturant.dto.UpdateBranchStatusRequest;
import com.Hatly.Backend.resturant.service.RestBranchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/branch")
public class BranchController {
    @Autowired
    private RestBranchService branchService;


    @PostMapping("/restaurants/{restaurantId}")
    public ResponseEntity<BranchResponse> createBranch(
            @PathVariable Long restaurantId,
            @Valid @RequestBody BranchRequest req) {

        BranchResponse response = branchService.createBranch(restaurantId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/restaurants/{restaurantId}/branches")
    public ResponseEntity<List<BranchResponse>> getAllBranches(@PathVariable Long restaurantId) {
        List<BranchResponse> responses = branchService.getAllRestBranches(restaurantId);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/restaurants/{restaurantId}/branches/{id}")
    public ResponseEntity<BranchResponse> getBranchById(
            @PathVariable Long restaurantId,
            @PathVariable Long id) {

        BranchResponse response = branchService.getRestBranchById(restaurantId, id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/branches/{id}")
    public ResponseEntity<Map<String, Object>> updateBranch(
            @PathVariable Long id,
            @RequestBody BranchRequest req) {

        Map<String, Object> result = branchService.updateBranch(id, req);
        return ResponseEntity.ok(result);
    }


    @PatchMapping("/branches/{id}/status")
    public ResponseEntity<Map<String, Object>> updateBranchStatus(
            @PathVariable Long id,
            @RequestBody UpdateBranchStatusRequest req) {

        Map<String, Object> result = branchService.updateBranchStatus(id, req);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{restaurantId}/nearest-branch")
    public ResponseEntity<BranchResponse> getNearestBranch(
            @PathVariable Long restaurantId,
            @RequestParam("lat") BigDecimal userLat,
            @RequestParam("lng") BigDecimal userLng) {

        BranchResponse response = branchService.getNearestBranchData(restaurantId, userLat, userLng);
        return ResponseEntity.ok(response);
    }
}
