package com.edu.elearning.service.courseManagement;

import com.edu.elearning.dto.courseManagement.request.CourseCreate;
import com.edu.elearning.dto.courseManagement.request.CourseUpdate;
import com.edu.elearning.dto.courseManagement.response.CourseResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface CourseService {
    CourseResponse createCourse(CourseCreate courseCreate);

    CourseResponse updateCourse(CourseUpdate courseUpdate);

    CourseResponse getCourseById(Long id);

    void deleteCourse(Long id);

    Page<CourseResponse> getAllCourses(
            Map<String,String> filters,
            int page,
            int size,
            String sortField,
            String sortDirection
    );
}
