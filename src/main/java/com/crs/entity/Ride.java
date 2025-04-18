package com.crs.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.*;
import java.util.List;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_location_id", nullable = false)
    private Location pickupLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dropoff_location_id", nullable = false)
    private Location dropoffLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDate departureDate;

    @Column(nullable = false)
    private String departureTime;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer maxPassengers;

    private Integer passengers = 0;
    private String distance;
    private String duration;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ACTIVE'")
    private RideStatus status = RideStatus.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RideRequest> requests;

    public enum RideStatus {
        ACTIVE,
        PENDING,
        REQUESTED,    // Ride is just requested (not yet accepted)
        CONFIRMED,    // Driver has accepted the ride
        IN_PROGRESS,  // Ride is ongoing
        COMPLETED,    // Ride is finished
        CANCELLED     // Ride was cancelled
    }
}