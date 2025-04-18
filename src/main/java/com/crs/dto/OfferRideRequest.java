package com.crs.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferRideRequest {
    private LocationRequest pickupLocation;
    private LocationRequest dropoffLocation;
    private String departureDate;
    private String departureTime;
    private Double price;
    private Integer maxPassengers;
    private String vehicleId;
}
