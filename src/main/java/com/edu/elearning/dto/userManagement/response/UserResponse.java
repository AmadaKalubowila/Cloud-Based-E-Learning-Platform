package com.edu.elearning.dto.userManagement.response;

import com.edu.elearning.enums.Departments;
import com.edu.elearning.enums.Role;
import com.edu.elearning.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String registerNo;
    private String fullName;
    private Role role;
    private String email;
    private String userName;
    private String address;
    private String phoneNumber;
    private Status status;
    private Departments department;
    private String batch;
}
