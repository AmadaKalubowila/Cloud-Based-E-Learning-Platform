package com.edu.elearning.controller.userCourseMapping;

import com.edu.elearning.dto.userCourseMappings.request.CourseAssignmentCreate;
import com.edu.elearning.dto.userCourseMappings.response.CourseAssignmentResponse;
import com.edu.elearning.service.userCourseMappings.CourseAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/courseAssignments")
@RequiredArgsConstructor
public class CourseAssignmentController {
    private final CourseAssignmentService assignmentService;

    @PostMapping("/assign")
    public CourseAssignmentResponse assignLecturer(
            @RequestBody CourseAssignmentCreate request) {

        return assignmentService.assignLecturer(request);
    }


    @GetMapping("/getAssignmentById/{id}")
    public CourseAssignmentResponse getAssignmentById(
            @PathVariable Long id) {

        return assignmentService.getAssignmentById(id);
    }


    @GetMapping("/getAll")
    public Page<CourseAssignmentResponse> getAllAssignments(
            @RequestParam Map<String, String> filters,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortField,

            @RequestParam(defaultValue = "DESC")
            String sortDirection) {


        return assignmentService.getAllAssignments(
                filters,
                page,
                size,
                sortField,
                sortDirection
        );
    }


    @DeleteMapping("/remove/{id}")
    public String removeAssignment(
            @PathVariable Long id) {

        assignmentService.removeAssignment(id);

        return "Lecturer assignment removed successfully";
    }
}