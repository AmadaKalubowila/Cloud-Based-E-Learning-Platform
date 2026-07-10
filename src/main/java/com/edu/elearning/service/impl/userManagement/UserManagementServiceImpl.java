package com.edu.elearning.service.impl.userManagement;

import com.edu.elearning.config.MessageConfig;
import com.edu.elearning.dto.userManagement.request.LoginRequest;
import com.edu.elearning.dto.userManagement.request.PasswordChangeRequest;
import com.edu.elearning.dto.userManagement.request.UserRegisterRequest;
import com.edu.elearning.dto.userManagement.request.UserUpdateRequest;
import com.edu.elearning.dto.userManagement.response.LoginResponse;
import com.edu.elearning.dto.userManagement.response.UserAuditResponse;
import com.edu.elearning.dto.userManagement.response.UserResponse;
import com.edu.elearning.entity.AuditUserLog;
import com.edu.elearning.entity.User;
import com.edu.elearning.entity.UserDetails;
import com.edu.elearning.enums.Departments;
import com.edu.elearning.enums.Role;
import com.edu.elearning.enums.Status;
import com.edu.elearning.exception.ElearningException;
import com.edu.elearning.repository.UserAuditRepository;
import com.edu.elearning.repository.UserDetailsRepository;
import com.edu.elearning.repository.UserRepository;
import com.edu.elearning.service.impl.email.EmailService;
import com.edu.elearning.service.userManagement.UserManagementService;
import com.edu.elearning.specification.CommonSpecifications;
import com.edu.elearning.utility.JwtUtil;
import com.edu.elearning.utility.Sorting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final UserAuditRepository auditUserLogRepository;
    private final MessageConfig messagesConfig;
    private final EmailService emailService;

    @Override
    @Transactional
    public UserResponse createUser(UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new ElearningException("User registration data is missing.");
        }

        userName(userRegisterRequest.getUserName());
        emailValidation(userRegisterRequest.getEmail());
        passwordValidation(userRegisterRequest.getPassword());
        UserDetails userDetails = userDetailsRepository.save(buildUserDetails(userRegisterRequest));
        User user = userRepository.save(buildUser(userRegisterRequest, userDetails));

        auditUserLogRepository.save(buildAuditUserLog(user, "create User", "Create new user to system"));

        return buildUserResponse(user, userDetails);
    }

    private UserDetails buildUserDetails(UserRegisterRequest userRegisterRequest) {
        return UserDetails.builder()
                .fullName(userRegisterRequest.getFullName())
                .email(userRegisterRequest.getEmail())
                .role(Role.valueOf(userRegisterRequest.getRole()))
                .phone(userRegisterRequest.getPhone())
                .address(userRegisterRequest.getAddress())
                .registeredDate(LocalDateTime.now())
                .version(0)
                .status(Status.ACTIVE)
                .batch(userRegisterRequest.getBatch())
                .departments(Departments.valueOf(userRegisterRequest.getDepartment()))
                .registerNo(generateRegisterNo(Role.valueOf(userRegisterRequest.getRole())))
                .build();
    }

    private AuditUserLog buildAuditUserLog(User user, String acton, String details) {
        return AuditUserLog.builder()
                .username(user.getUsername())
                .action(acton)
                .details(details)
                .timestamp(LocalDateTime.now())
                .user(user)
                .build();

    }

    public UserDetails fetchUserDetails(Long id) {
        return userDetailsRepository.findById(id).orElseThrow(() -> new ElearningException("User details not found for the given ID "));
    }

    private User buildUser(UserRegisterRequest userRegisterRequest, UserDetails userDetails) {
        return User.builder()
                .username(userRegisterRequest.getUserName())
                .password(encryptPassword(userRegisterRequest.getPassword()))
                .role(Role.valueOf(userRegisterRequest.getRole()))
                .userDetails(userDetails)
                .failedLoginAttempts(0)
                .locked(false)
                .version(0)
                .status(Status.ACTIVE)
                .build();
    }

    private String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private UserResponse buildUserResponse(User user, UserDetails userDetails) {
        return UserResponse.builder()
                .registerNo(userDetails.getRegisterNo())
                .userId(userDetails.getId())
                .fullName(userDetails.getFullName())
                .role(userDetails.getRole())
                .email(userDetails.getEmail())
                .userName(user.getUsername())
                .address(userDetails.getAddress())
                .phoneNumber(userDetails.getPhone())
                .status(userDetails.getStatus())
                .department(userDetails.getDepartments())
                .batch(userDetails.getBatch())
                .build();
    }

    private void emailValidation(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        UserDetails userDetails = userDetailsRepository.findByEmail(email).orElse(null);

        if (userDetails != null) {
            if (userDetails.getStatus() == Status.ACTIVE) {
                throw new ElearningException(messagesConfig.get("This email exists already"));
            }
        }
    }

    private void userName(String userName) {
        User user = userRepository.findByUsername(userName).orElse(null);
        if (user != null) {
            throw new ElearningException("That username already exits");
        }
    }

    private void passwordValidation(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

        if (!password.matches(passwordRegex)) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character"
            );
        }
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        if (loginRequest.getUserName() == null || loginRequest.getUserName().trim().isEmpty()
                || loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new ElearningException("Username and password are required");
        }

        User user = userRepository.findByUsername(loginRequest.getUserName())
                .orElseThrow(() -> new ElearningException("Invalid username or password"));

        if(user.getStatus() == Status.INACTIVE) {
            throw new ElearningException("This user is already inactive");
        }

        if (user.isLocked()) {
            throw new ElearningException("Account locked due to multiple failed attempts");
        }

        if (!BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {

            int attempts = user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts();
            attempts++;

            user.setFailedLoginAttempts(attempts);

            if (attempts >= 5) {
                user.setLocked(true);
            }

            userRepository.save(user);

            throw new ElearningException("Invalid username or password");
        }

        if (user.getStatus() == Status.INACTIVE) {
            throw new ElearningException("User account is inactive");
        }

        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = JwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());
        String refreshToken = JwtUtil.generateRefreshToken(user.getUsername());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().name())
                .build();
    }

    @Override
    public LoginResponse refresh(String refreshToken) {

        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ElearningException("Invalid refresh token"));

        String newAccessToken = JwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().name())
                .build();
    }

    @Override
    public String logout(String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new ElearningException("Refresh token is required");
        }

        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ElearningException("Invalid refresh token"));

        user.setRefreshToken(null);

        userRepository.save(user);

        return "Logged out successfully";
    }

    @Override
    public String requestPasswordReset(String username) {

        if (username == null || username.trim().isEmpty()) {
            throw new ElearningException("Username is required");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ElearningException("User not found"));


        String token = java.util.UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);


        String resetLink = "http://localhost:8088/user/resetPassword?token=" + token;


//        String html = emailService.buildResetEmail(resetLink);
//
//        emailService.sendHtmlEmail(
//                user.getUserDetails().getEmail(),
//                "Reset Your Password",
//                html
//        );
        auditUserLogRepository.save(buildAuditUserLog(user, "Request password change", "Request password change"));
        return "Password reset request sent: " + token;
    }

    @Override
    public String resetPassword(PasswordChangeRequest passwordChangeRequest) {

        if (passwordChangeRequest.getToken() == null || passwordChangeRequest.getToken().isEmpty()) {
            throw new ElearningException("Invalid reset token");
        }

        User user = userRepository.findByResetToken(passwordChangeRequest.getToken())
                .orElseThrow(() -> new ElearningException("Invalid or expired token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ElearningException("Reset token expired");
        }

        passwordValidation(passwordChangeRequest.getPassword());

        user.setPassword(BCrypt.hashpw(passwordChangeRequest.getPassword(), BCrypt.gensalt()));

        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);

        auditUserLogRepository.save(buildAuditUserLog(user, "Password change", "Password change"));

        return "Password reset successful";
    }

    @Override
    public String unlockUser(String username) {

        if (username == null || username.trim().isEmpty()) {
            throw new ElearningException("Username is required");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ElearningException("User not found"));

        if (!user.isLocked()) {
            return "User is not locked";
        }

        user.setLocked(false);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        auditUserLogRepository.save(buildAuditUserLog(user, "Unlock", "Unlock User"));
        return "User unlocked successfully";
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserUpdateRequest req) {

        UserDetails userDetails = userDetailsRepository.findById(req.getId())
                .orElseThrow(() -> new ElearningException("User not found"));

        User user = userRepository.findByUserDetailsId(req.getId())
                .orElseThrow(() -> new ElearningException("User not found"));


        if (req.getFullName() != null) {
            userName(req.getFullName());
            userDetails.setFullName(req.getFullName());
        }

        if (req.getStatus() != null) {
            userDetails.setStatus(req.getStatus());
        }

        if (req.getEmail() != null) {
            emailValidation(req.getEmail());
            userDetails.setEmail(req.getEmail());
        }

        if (req.getAddress() != null) {
            userDetails.setAddress(req.getAddress());
        }

        if (req.getPhone() != null) {
            userDetails.setPhone(req.getPhone());
        }

        if (req.getUserName() != null) {
            user.setUsername(req.getUserName());
        }

        if (req.getStatus() != null) {
            user.setStatus(req.getStatus());
        }

        userDetailsRepository.save(userDetails);
        userRepository.save(user);

        auditUserLogRepository.save(buildAuditUserLog(user, "User updated", "User updated"));

        return buildUserResponse(user, userDetails);
    }

    @Override
    @Transactional
    public String deleteUser(String username) {

        if (username == null || username.trim().isEmpty()) {
            throw new ElearningException("Username is required");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ElearningException("User not found"));

        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
        UserDetails userDetails = userDetailsRepository.findById(user.getUserDetails().getId()).orElseThrow(() -> new ElearningException("User not found"));
        userDetails.setStatus(Status.INACTIVE);
        userDetailsRepository.save(userDetails);


        auditUserLogRepository.save(buildAuditUserLog(user, "User deleted", "User deleted"));

        return "User deleted successfully";
    }

    @Override
    public Page<UserAuditResponse> getAllAuditResponses(
            Map<String, String> filters, int page, int size, String sortField, String sortDirection) {

        Specification<AuditUserLog> spec =
                CommonSpecifications.getSpecification(filters, AuditUserLog.class);


        Page<AuditUserLog> userAccounts =
                auditUserLogRepository.findAll(spec, Sorting.sorting(page, size, sortField, sortDirection));


        return userAccounts.map(this::convertToDTO);
    }

    @Override
    public Page<UserResponse> getAllUsers(
            Map<String, String> filters, int page, int size, String sortField, String sortDirection) {

        Specification<User> spec =
                CommonSpecifications.getSpecification(filters, User.class);

        Page<User> userAccounts =
                userRepository.findAll(spec, Sorting.sorting(page, size, sortField, sortDirection));

        return userAccounts.map(user -> buildUserResponse(user, user.getUserDetails()));
    }

    private UserAuditResponse convertToDTO(AuditUserLog auditUserLog) {
        return UserAuditResponse.builder()
                .id(auditUserLog.getId())
                .action(auditUserLog.getAction())
                .userName(auditUserLog.getUsername())
                .details(auditUserLog.getDetails())
                .createdAt(auditUserLog.getTimestamp())
                .userId(auditUserLog.getUser().getId())
                .fullName(auditUserLog.getUser().getUserDetails().getFullName())
                .build();
    }

    private String generateRegisterNo(Role role) {
        return role + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

}
