package com.edu.elearning.dto.userManagement.request;

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
public class UserUpdateRequest {
    private Long id;
    private String fullName;
    private String email;
    private String address;
    private String phone;
    private String userName;
    private Status status;
    private String department;
    private String batch;
}
