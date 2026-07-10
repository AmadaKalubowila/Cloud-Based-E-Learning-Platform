package com.edu.elearning.dto.userManagement.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuditResponse {
    private Long id;
    private String userName;
    private String action;
    private String details;
    private LocalDateTime createdAt;
    private Long userId;
    private String fullName;
}
