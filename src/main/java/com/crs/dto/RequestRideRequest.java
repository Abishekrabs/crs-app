package com.crs.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestRideRequest {
    private String rideId;
    private Integer passengers;
}
