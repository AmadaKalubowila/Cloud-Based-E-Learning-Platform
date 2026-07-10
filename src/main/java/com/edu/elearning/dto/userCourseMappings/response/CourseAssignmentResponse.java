package com.edu.elearning.dto.userCourseMappings.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class CourseAssignmentResponse {

    private Long id;

    private Long lecturerId;

    private String lecturerName;

    private Long courseId;

    private String courseName;

    private LocalDateTime assignedDate;
}