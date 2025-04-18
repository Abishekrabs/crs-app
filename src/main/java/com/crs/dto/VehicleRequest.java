package com.crs.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {
    private String model;
    private String color;
    private String licensePlate;
}
