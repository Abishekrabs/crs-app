package com.crs.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusResponse {
    private String id;
    private String status;
    private String message;
    private LocalDateTime updatedAt;
}
