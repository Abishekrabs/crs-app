package com.crs.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {
    private String address;
    private Double latitude;
    private Double longitude;
}
