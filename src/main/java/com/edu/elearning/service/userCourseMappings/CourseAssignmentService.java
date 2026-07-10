package com.edu.elearning.service.userCourseMappings;

import com.edu.elearning.dto.userCourseMappings.request.CourseAssignmentCreate;
import com.edu.elearning.dto.userCourseMappings.response.CourseAssignmentResponse;
import org.springframework.data.domain.Page;

import java.util.Map;


public interface CourseAssignmentService {


    CourseAssignmentResponse assignLecturer(
            CourseAssignmentCreate assignmentCreate
    );


    CourseAssignmentResponse getAssignmentById(Long id);


    Page<CourseAssignmentResponse> getAllAssignments(
            Map<String, String> filters,
            int page,
            int size,
            String sortField,
            String sortDirection
    );


    void removeAssignment(Long id);
}