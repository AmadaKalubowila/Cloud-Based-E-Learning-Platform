package com.edu.elearning.service.userManagement;

import com.edu.elearning.dto.userManagement.request.LoginRequest;
import com.edu.elearning.dto.userManagement.request.PasswordChangeRequest;
import com.edu.elearning.dto.userManagement.request.UserRegisterRequest;
import com.edu.elearning.dto.userManagement.request.UserUpdateRequest;
import com.edu.elearning.dto.userManagement.response.LoginResponse;
import com.edu.elearning.dto.userManagement.response.UserAuditResponse;
import com.edu.elearning.dto.userManagement.response.UserResponse;
import com.edu.elearning.entity.UserDetails;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface UserManagementService {
    UserResponse createUser(UserRegisterRequest userRegisterRequest);

    String resetPassword(PasswordChangeRequest passwordChangeRequest);

    String requestPasswordReset(String username);

    String logout(String refreshToken);

    LoginResponse refresh(String refreshToken);

    LoginResponse login(LoginRequest loginRequest);

    String unlockUser(String username);

    String deleteUser(String username);

    UserResponse updateUser(UserUpdateRequest req);

    Page<UserAuditResponse> getAllAuditResponses(
            Map<String, String> filters, int page, int size, String sortField, String sortDirection);

    UserDetails fetchUserDetails(Long id);

    Page<UserResponse> getAllUsers(
            Map<String, String> filters, int page, int size, String sortField, String sortDirection);

    UserResponse fetchUserById(Long id);
}
