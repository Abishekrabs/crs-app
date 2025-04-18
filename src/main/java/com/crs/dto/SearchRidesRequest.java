package com.crs.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRidesRequest {
    private String location;
    private String departureDate;
    private Integer passengers;
    private Double maxPrice;
}
