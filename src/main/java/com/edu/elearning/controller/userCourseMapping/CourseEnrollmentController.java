package com.edu.elearning.controller.userCourseMapping;

import com.edu.elearning.dto.userCourseMappings.request.EnrollmentCreate;
import com.edu.elearning.dto.userCourseMappings.response.EnrollmentResponse;
import com.edu.elearning.service.userCourseMappings.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/courseEnrollments")
@RequiredArgsConstructor
public class CourseEnrollmentController {


    private final CourseEnrollmentService enrollmentService;


    @PostMapping("/enrollStudent")
    public EnrollmentResponse enrollStudent(@RequestHeader("Authorization") String token,
            @RequestBody EnrollmentCreate enrollmentCreate) {

        return enrollmentService.enrollStudent(
                enrollmentCreate
        );
    }


    @GetMapping("/getEnrollmentById/{id}")
    public EnrollmentResponse getEnrollmentById(@RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        return enrollmentService.getEnrollmentById(id);
    }


    @GetMapping("/getAll")
    public Page<EnrollmentResponse> getAllEnrollments(
            @RequestHeader("Authorization") String token,
            @RequestParam Map<String, String> filters,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortField,

            @RequestParam(defaultValue = "DESC")
            String sortDirection
    ) {

        return enrollmentService.getAllEnrollments(
                filters,
                page,
                size,
                sortField,
                sortDirection
        );
    }


    @PostMapping("/remove/{id}")
    public EnrollmentResponse removeEnrollment(@RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        return  enrollmentService.removeEnrollment(id);

    }
}