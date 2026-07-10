package com.edu.elearning.service.userCourseMappings;

import com.edu.elearning.dto.userCourseMappings.request.EnrollmentCreate;
import com.edu.elearning.dto.userCourseMappings.response.EnrollmentResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface CourseEnrollmentService {


    EnrollmentResponse enrollStudent(
            EnrollmentCreate enrollmentCreate
    );


    EnrollmentResponse getEnrollmentById(Long id);


    Page<EnrollmentResponse> getAllEnrollments(
            Map<String,String> filters,
            int page,
            int size,
            String sortField,
            String sortDirection
    );


    EnrollmentResponse removeEnrollment(Long id);

}