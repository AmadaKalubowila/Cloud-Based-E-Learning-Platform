package com.edu.elearning.dto.userCourseMappings.request;

import lombok.Data;

@Data
public class CourseAssignmentCreate {

    private Long lecturerId;

    private Long courseId;
}
