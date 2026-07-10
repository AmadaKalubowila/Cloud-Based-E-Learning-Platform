package com.edu.elearning.dto.userCourseMappings.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EnrollmentCreate {

    private Long studentId;

    private Long courseId;


}