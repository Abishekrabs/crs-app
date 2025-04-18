package com.crs.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideResponse {
    private String id;
    private LocationResponse pickupLocation;
    private LocationResponse dropoffLocation;
    private String departureDate;
    private String departureTime;
    private Double price;
    private Integer maxPassengers;
    private Integer passengers;
    private String distance;
    private String duration;
    private UserResponse driver;
    private VehicleResponse vehicle;
    private String status;
    private LocalDateTime createdAt;
}
