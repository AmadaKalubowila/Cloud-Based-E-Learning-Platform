package com.edu.elearning.dto.userCourseMappings.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentResponse {

    private Long id;

    private Long studentId;

    private String studentName;

    private Long courseId;

    private String courseName;

    private LocalDateTime enrolledDate;

    private Integer progressPercentage;

    private String status;
}