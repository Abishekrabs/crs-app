package com.crs.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestResponse {
    private String id;
    private RideResponse ride;
    private String status;
    private Integer passengers;
    private LocalDateTime createdAt;
}
