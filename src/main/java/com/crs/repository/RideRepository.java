package com.crs.repository;

import com.crs.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, String> {

    List<Ride> findByDriverId(String driverId);
    List<Ride> findByStatus(Ride.RideStatus status);

    @Query("SELECT r FROM Ride r WHERE " +
            "LOWER(r.pickupLocation.address) LIKE LOWER(CONCAT('%', :location, '%')) AND " +
            "r.departureDate = :departureDate AND " +
            "r.maxPassengers >= :passengers AND " +
            "r.price <= :maxPrice AND " +
            "r.status = 'ACTIVE'")
    List<Ride> searchAvailableRides(
            @Param("location") String location,
            @Param("departureDate") LocalDate departureDate,
            @Param("passengers") Integer passengers,
            @Param("maxPrice") Double maxPrice
    );
}
