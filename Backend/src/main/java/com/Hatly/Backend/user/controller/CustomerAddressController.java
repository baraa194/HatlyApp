package com.Hatly.Backend.user.controller;
import com.Hatly.Backend.user.dto.CustomerAddressdto;
import com.Hatly.Backend.user.service.CustomerAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/addresses")
public class CustomerAddressController {

    @Autowired
    private CustomerAddressService addressService;

    @PostMapping
    public ResponseEntity<?> addAddress(@RequestBody CustomerAddressdto addressDto) {
        try {
            CustomerAddressdto savedAddress = addressService.addCustomerAddress(addressDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody CustomerAddressdto addressDto) {
        try {
            CustomerAddressdto updatedAddress = addressService.updateCustomerAddress(addressDto, id);
            return ResponseEntity.ok(updatedAddress);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable Long id) {
        try {
            CustomerAddressdto address = addressService.getAddressById(id);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        try {
            addressService.deleteCustomerAddress(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Address deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAddresses(@RequestParam Long userId) {
        try {
            List<CustomerAddressdto> list = addressService.getAddresses(userId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    @GetMapping("/user/{userId}/default")
    public ResponseEntity<CustomerAddressdto> getDefaultAddress(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getDefaultCustomerAddress(userId));
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        return ResponseEntity.status(status).body(error);
    }

}
