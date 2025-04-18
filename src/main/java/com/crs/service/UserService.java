package com.crs.service;

import com.crs.dto.UserResponse;
import com.crs.dto.VehicleRequest;
import com.crs.dto.VehicleResponse;
import com.crs.entity.User;
import com.crs.entity.Vehicle;
import com.crs.repository.UserRepository;
import com.crs.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public UserResponse getProfile(String userId) {
        User user = userRepository. findByEmail(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserResponse(user);
    }

    public List<VehicleResponse> getUserVehicles(String userId) {
        User user = userRepository. findByEmail(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Vehicle> vehicles = vehicleRepository.findByUserId(user.getId());
        return vehicles.stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    public VehicleResponse addVehicle(String userId, VehicleRequest request) {
        Vehicle vehicle = Vehicle.builder()
                .id(UUID.randomUUID().toString())
                .user(userRepository.findByEmail(userId).orElseThrow())
                .model(request.getModel())
                .color(request.getColor())
                .licensePlate(request.getLicensePlate())
                .build();

        vehicleRepository.save(vehicle);
        return mapToVehicleResponse(vehicle);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profilePicture(user.getProfilePicture())
                .rating(user.getRating())
                .ratingCount(user.getRatingCount())
                .build();
    }

    private VehicleResponse mapToVehicleResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .model(vehicle.getModel())
                .color(vehicle.getColor())
                .licensePlate(vehicle.getLicensePlate())
                .build();
    }
}