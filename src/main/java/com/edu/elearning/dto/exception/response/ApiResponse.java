package com.edu.elearning.dto.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .data(data)
                .message("Success")
                .build();
    }

    public static <T> ApiResponse<T> error(String message){
        return ApiResponse.<T>builder()
                .message(message)
                .build();
    }
}