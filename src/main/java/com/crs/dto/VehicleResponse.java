package com.crs.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {
    private String id;
    private String model;
    private String color;
    private String licensePlate;
}
