package com.crs.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelRideRequest {
    private String reason;
}
