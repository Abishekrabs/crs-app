package com.crs.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateRideRequest {
    private Integer rating;
    private String comment;
}
