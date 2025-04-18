package com.crs.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
    private String id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
