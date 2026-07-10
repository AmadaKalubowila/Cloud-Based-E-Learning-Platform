package com.edu.elearning.controller.userManagement;

import com.edu.elearning.constant.AppConstant;
import com.edu.elearning.dto.userManagement.request.LoginRequest;
import com.edu.elearning.dto.userManagement.request.PasswordChangeRequest;
import com.edu.elearning.dto.userManagement.request.UserRegisterRequest;
import com.edu.elearning.dto.userManagement.request.UserUpdateRequest;
import com.edu.elearning.dto.userManagement.response.LoginResponse;
import com.edu.elearning.dto.userManagement.response.UserAuditResponse;
import com.edu.elearning.dto.userManagement.response.UserResponse;
import com.edu.elearning.exception.ElearningException;
import com.edu.elearning.service.userManagement.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserManagementService userService;

    @PostMapping("/create")
    public UserResponse createUser(@RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.createUser(userRegisterRequest);
    }

    @PostMapping(value = "/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @PutMapping(value = "/password")
    public String restPassword(@RequestBody PasswordChangeRequest passwordChangeRequest) {
        return userService.resetPassword(passwordChangeRequest);
    }

    @PostMapping(value = "/resetPassword")
    public String requestPasswordReset(@RequestParam String username) {
        return userService.requestPasswordReset(username);
    }

    @PostMapping(value = "/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ElearningException("Invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return userService.logout(token);
    }

    @PostMapping(value = "/refresh")
    public LoginResponse refresh(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ElearningException("Invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return userService.refresh(token);
    }

    @GetMapping("/reset-password")
    public String showResetPage(@RequestParam String token) {
        return "Token received: " + token;
    }

    @PutMapping("/unlock-user/{username}")
    public String unlockUser(@PathVariable String username, @RequestHeader("Authorization") String token) {
        return userService.unlockUser(username);
    }

    @PutMapping("/update")
    public UserResponse updateUser(@RequestBody UserUpdateRequest userUpdateRequest, @RequestHeader("Authorization") String token) {
        return userService.updateUser(userUpdateRequest);
    }

    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam String username, @RequestHeader("Authorization") String token) {
        return userService.deleteUser(username);
    }

    @GetMapping("/getById")
    public UserResponse deleteUser(@RequestParam Long id) {
        return userService.fetchUserById(id);
    }


    @GetMapping(value = "/getAll")
    public Page<UserAuditResponse> getAll(
            @RequestParam(defaultValue = AppConstant.DEFAULT_PAGE_No) final int page,
            @RequestParam(defaultValue = AppConstant.DEFAULT_PAGE_SIZE) final int size,
            @RequestParam(defaultValue = "id") final String sortField,
            @RequestParam(defaultValue = AppConstant.DEF_SORT_DIR) final String sortDirection,
            @RequestParam Map<String, String> allParams,
            @RequestHeader("Authorization") String token) {

        allParams.remove("page");
        allParams.remove("size");
        allParams.remove("sortField");
        allParams.remove("sortDirection");

        return userService.getAllAuditResponses(allParams, page, size, sortField, sortDirection);
    }

    @GetMapping(value = "/getAllUsers")
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = AppConstant.DEFAULT_PAGE_No) final int page,
            @RequestParam(defaultValue = AppConstant.DEFAULT_PAGE_SIZE) final int size,
            @RequestParam(defaultValue = "id") final String sortField,
            @RequestParam(defaultValue = AppConstant.DEF_SORT_DIR) final String sortDirection,
            @RequestParam Map<String, String> allParams,
            @RequestHeader("Authorization") String token) {

        allParams.remove("page");
        allParams.remove("size");
        allParams.remove("sortField");
        allParams.remove("sortDirection");

        return userService.getAllUsers(allParams, page, size, sortField, sortDirection);
    }

}
