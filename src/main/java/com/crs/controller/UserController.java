package com.crs.controller;

import com.crs.dto.UserResponse;
import com.crs.dto.VehicleRequest;
import com.crs.dto.VehicleResponse;
import org.springframework.security.core.userdetails.User;
import com.crs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(userService.getProfile(user.getUsername()));
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleResponse>> getUserVehicles() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(userService.getUserVehicles(user.getUsername()));
    }

    @PostMapping("/vehicles")
    public ResponseEntity<VehicleResponse> addVehicle(
            @RequestBody VehicleRequest request) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(userService.addVehicle(user.getUsername(), request));
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return (User) auth.getPrincipal();
    }
}