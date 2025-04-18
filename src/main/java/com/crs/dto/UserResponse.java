package com.crs.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String profilePicture;
    private Double rating;
    private Integer ratingCount;
}
