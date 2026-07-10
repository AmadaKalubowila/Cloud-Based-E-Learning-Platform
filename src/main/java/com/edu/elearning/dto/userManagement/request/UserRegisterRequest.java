package com.edu.elearning.dto.userManagement.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterRequest {
    private String fullName;
    private String role;
    private String email;
    private String userName;
    private String password;
    private String address;
    private String phone;
    private String department;
    private String batch;
}
