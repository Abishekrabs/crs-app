package com.crs.controller;

import com.crs.dto.*;
import com.crs.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    @PostMapping("/offer")
    public ResponseEntity<RideResponse> offerRide(
            @RequestBody OfferRideRequest request) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(rideService.offerRide(user.getUsername(), request));
    }

    @GetMapping("/offered")
    public ResponseEntity<List<RideResponse>> getOfferedRides() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(rideService.getOfferedRides(user.getUsername()));
    }

    @PostMapping("/search")
    public ResponseEntity<List<RideResponse>> searchAvailableRides(@RequestBody SearchRidesRequest request) {
        return ResponseEntity.ok(rideService.searchAvailableRides(request));
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRideDetails(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getRideDetails(rideId));
    }

    @PostMapping("/request")
    public ResponseEntity<RideRequestResponse> requestRide(
            @RequestBody RequestRideRequest request) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(rideService.requestRide(user.getUsername(), request));
    }

    @GetMapping("/requested")
    public ResponseEntity<List<RideResponse>> getRequestedRides() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(rideService.getRequestedRides(user.getUsername()));
    }

    @PutMapping("/{rideId}/accept")
    public ResponseEntity<StatusResponse> acceptRideRequest(
            @PathVariable String rideId,
            @RequestBody Map<String, String> request) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(rideService.acceptRideRequest(
                user.getUsername(),
                rideId,
                request.get("requestId"))
        );
    }

    @PutMapping("/{rideId}/start")
    public ResponseEntity<StatusResponse> startRide(
            @PathVariable String rideId) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(rideService.startRide(user.getUsername(), rideId));
    }

    @PutMapping("/{rideId}/complete")
    public ResponseEntity<StatusResponse> completeRide(
            @PathVariable String rideId) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(rideService.completeRide(user.getUsername(), rideId));
    }

    @PostMapping("/{rideId}/rate")
    public ResponseEntity<RatingResponse> rateRide(
            @PathVariable String rideId,
            @RequestBody RateRideRequest request) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(rideService.rateRide(user.getUsername(), rideId, request));
    }

    @PutMapping("/{rideId}/cancel")
    public ResponseEntity<StatusResponse> cancelRide(
            @PathVariable String rideId,
            @RequestBody CancelRideRequest request) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(rideService.cancelRide(user.getUsername(), rideId, request));
    }

    private org.springframework.security.core.userdetails.User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return (org.springframework.security.core.userdetails.User) auth.getPrincipal();
    }
}