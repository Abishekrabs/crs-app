package com.crs.repository;

import com.crs.entity.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface RideRequestRepository extends JpaRepository<RideRequest, String> {
    List<RideRequest> findByPassengerId(String passengerId);
    Optional<RideRequest> findByRideIdAndPassengerId(String rideId, String passengerId);
}