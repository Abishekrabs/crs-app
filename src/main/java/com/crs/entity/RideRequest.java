package com.crs.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ride_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequest {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @Column(nullable = false)
    private Integer passengers;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'REQUESTED'")
    private RequestStatus status = RequestStatus.REQUESTED;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum RequestStatus {
        ACTIVE,
        PENDING,
        REQUESTED,    // Ride is just requested (not yet accepted)
        CONFIRMED,    // Driver has accepted the ride
        IN_PROGRESS,  // Ride is ongoing
        COMPLETED,    // Ride is finished
        CANCELLED     // Ride was cancelled
    }
}
