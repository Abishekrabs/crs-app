package com.crs.service;

import com.crs.dto.*;
import com.crs.entity.*;
import com.crs.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RideService {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final LocationRepository locationRepository;
    private final RideRequestRepository rideRequestRepository;
    private final RatingRepository ratingRepository;

    public RideResponse offerRide(String driverId, OfferRideRequest request) {
        User driver = userRepository.findByEmail(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        Location pickupLocation = saveLocation(request.getPickupLocation());
        Location dropoffLocation = saveLocation(request.getDropoffLocation());

        Ride ride = Ride.builder()
                .id(UUID.randomUUID().toString())
                .driver(driver)
                .pickupLocation(pickupLocation)
                .dropoffLocation(dropoffLocation)
                .vehicle(vehicle)
                .departureDate(LocalDate.parse(request.getDepartureDate()))
                .departureTime(request.getDepartureTime())
                .price(request.getPrice())
                .maxPassengers(request.getMaxPassengers())
                .status(Ride.RideStatus.ACTIVE)
                .distance(calculateDistance(pickupLocation, dropoffLocation))
                .duration(calculateDuration(pickupLocation, dropoffLocation))
                .build();

        rideRepository.save(ride);
        return mapToRideResponse(ride);
    }

    public List<RideResponse> getOfferedRides(String driverId) {
        User driver = userRepository.findByEmail(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        List<Ride> rides = rideRepository.findByDriverId(driver.getId());
        return rides.stream()
                .map(this::mapToRideResponse)
                .collect(Collectors.toList());
    }

    public List<RideResponse> searchAvailableRides(SearchRidesRequest request) {
        LocalDate departureDate = LocalDate.parse(request.getDepartureDate());
        List<Ride> rides = rideRepository.searchAvailableRides(
                request.getLocation(),
                departureDate,
                request.getPassengers(),
                request.getMaxPrice()
        );
        return rides.stream()
                .map(this::mapToRideResponse)
                .collect(Collectors.toList());
    }

    public RideRequestResponse requestRide(String passengerId, RequestRideRequest request) {
        User passenger = userRepository.findByEmail(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getPassengers() + request.getPassengers() > ride.getMaxPassengers()) {
            throw new RuntimeException("Not enough seats available");
        }

        RideRequest rideRequest = RideRequest.builder()
                .id(UUID.randomUUID().toString())
                .ride(ride)
                .status(RideRequest.RequestStatus.REQUESTED)
                .passenger(passenger)
                .passengers(request.getPassengers())
                .build();

        rideRequestRepository.save(rideRequest);
        return mapToRideRequestResponse(rideRequest);
    }

    public RideResponse getRideDetails(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));
        return mapToRideResponse(ride);
    }

    public List<RideResponse> getRequestedRides(String passengerId) {
        User passenger = userRepository.findByEmail(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        List<RideRequest> rideRequests = rideRequestRepository.findByPassengerId(passenger.getId());
        return rideRequests.stream()
                .map(rr -> mapToRideResponse(rr.getRide()))
                .collect(Collectors.toList());
    }

    public StatusResponse acceptRideRequest(String driverId, String rideId, String requestId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getId().equals(rideId)) {
            throw new RuntimeException("Not authorized to accept this ride");
        }

        RideRequest rideRequest = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        rideRequest.setStatus(RideRequest.RequestStatus.CONFIRMED);
        ride.setPassengers(ride.getPassengers() + rideRequest.getPassengers());
        ride.setStatus(Ride.RideStatus.CONFIRMED);

        rideRequestRepository.save(rideRequest);
        rideRepository.save(ride);

        return new StatusResponse(
                ride.getId(),
                ride.getStatus().toString(),
                "Ride request has been accepted",
                LocalDateTime.now()
        );
    }

    public StatusResponse startRide(String driverId, String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Not authorized to start this ride");
        }

        ride.setStatus(Ride.RideStatus.IN_PROGRESS);
        rideRepository.save(ride);

        return new StatusResponse(
                ride.getId(),
                ride.getStatus().toString(),
                "Ride has started",
                LocalDateTime.now()
        );
    }

    public StatusResponse completeRide(String driverId, String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Not authorized to complete this ride");
        }

        ride.setStatus(Ride.RideStatus.COMPLETED);
        rideRepository.save(ride);

        return new StatusResponse(
                ride.getId(),
                ride.getStatus().toString(),
                "Ride has been completed",
                LocalDateTime.now()
        );
    }

    public RatingResponse rateRide(String passengerId, String rideId, RateRideRequest request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        Optional<RideRequest> rideRequest = rideRequestRepository.findByRideIdAndPassengerId(rideId, passengerId);
        if (rideRequest.isEmpty()) {
            throw new RuntimeException("You didn't participate in this ride");
        }

        Rating rating = Rating.builder()
                .id(UUID.randomUUID().toString())
                .ride(ride)
                .passenger(userRepository.findById(passengerId).orElseThrow())
                .driver(ride.getDriver())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        ratingRepository.save(rating);

        // Update driver rating
        User driver = ride.getDriver();
        double newRating = (driver.getRating() * driver.getRatingCount() + request.getRating()) / (driver.getRatingCount() + 1);
        driver.setRating(newRating);
        driver.setRatingCount(driver.getRatingCount() + 1);
        userRepository.save(driver);

        return new RatingResponse(
                rating.getId(),
                rating.getRating(),
                rating.getComment(),
                rating.getCreatedAt()
        );
    }

    public StatusResponse cancelRide(String userId, String rideId, CancelRideRequest request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getDriver().getId().equals(userId)) {
            // Driver is canceling
            ride.setStatus(Ride.RideStatus.CANCELLED);
            rideRepository.save(ride);
        } else {
            // Passenger is canceling
            RideRequest rideRequest = rideRequestRepository.findByRideIdAndPassengerId(rideId, userId)
                    .orElseThrow(() -> new RuntimeException("Ride request not found"));

            rideRequest.setStatus(RideRequest.RequestStatus.CANCELLED);
            rideRequestRepository.save(rideRequest);

            // Update available seats
            ride.setPassengers(ride.getPassengers() - rideRequest.getPassengers());
            rideRepository.save(ride);
        }

        return new StatusResponse(
                ride.getId(),
                ride.getStatus().toString(),
                "Ride has been cancelled",
                LocalDateTime.now()
        );
    }

    private Location saveLocation(LocationRequest locationRequest) {
        Location location = Location.builder()
                .id(UUID.randomUUID().toString())
                .address(locationRequest.getAddress())
                .latitude(locationRequest.getLatitude())
                .longitude(locationRequest.getLongitude())
                .build();
        return locationRepository.save(location);
    }

    private String calculateDistance(Location pickup, Location dropoff) {
        // Simplified calculation - in a real app, use a proper distance API
        double lat1 = pickup.getLatitude();
        double lon1 = pickup.getLongitude();
        double lat2 = dropoff.getLatitude();
        double lon2 = dropoff.getLongitude();

        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.609344; // Convert to km

        return String.format("%.1f km", dist);
    }

    private String calculateDuration(Location pickup, Location dropoff) {
        // Simplified calculation - in a real app, use a proper routing API
        double lat1 = pickup.getLatitude();
        double lon1 = pickup.getLongitude();
        double lat2 = dropoff.getLatitude();
        double lon2 = dropoff.getLongitude();

        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515; // Miles

        // Assume average speed of 60 mph
        double hours = dist / 60;
        int totalMinutes = (int) (hours * 60);

        int hoursPart = totalMinutes / 60;
        int minutesPart = totalMinutes % 60;

        if (hoursPart > 0) {
            return String.format("%d hours %d minutes", hoursPart, minutesPart);
        } else {
            return String.format("%d minutes", minutesPart);
        }
    }

    private RideResponse mapToRideResponse(Ride ride) {
        return RideResponse.builder()
                .id(ride.getId())
                .pickupLocation(mapToLocationResponse(ride.getPickupLocation()))
                .dropoffLocation(mapToLocationResponse(ride.getDropoffLocation()))
                .departureDate(ride.getDepartureDate().toString())
                .departureTime(ride.getDepartureTime())
                .price(ride.getPrice())
                .maxPassengers(ride.getMaxPassengers())
                .passengers(ride.getPassengers())
                .distance(ride.getDistance())
                .duration(ride.getDuration())
                .driver(mapToUserResponse(ride.getDriver()))
                .vehicle(mapToVehicleResponse(ride.getVehicle()))
                .status(ride.getStatus().name())
                .createdAt(ride.getCreatedAt())
                .build();
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

    private LocationResponse mapToLocationResponse(Location location) {
        return LocationResponse.builder()
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }

    private RideRequestResponse mapToRideRequestResponse(RideRequest rideRequest) {
        RideRequestResponse response = new RideRequestResponse();
        response.setId(rideRequest.getId());
        response.setRide(mapToRideResponse(rideRequest.getRide()));
        response.setStatus(rideRequest.getStatus().toString());
        response.setPassengers(rideRequest.getPassengers());
        response.setCreatedAt(rideRequest.getCreatedAt());
        return response;
    }
}