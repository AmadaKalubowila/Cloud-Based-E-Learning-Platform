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
    public CourseAssignmentResponse assignLecturer(@RequestHeader("Authorization") String token,
            @RequestBody CourseAssignmentCreate request) {

        return assignmentService.assignLecturer(request);
    }

    @GetMapping("/getAssignmentById/{id}")
    public CourseAssignmentResponse getAssignmentById(@RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        return assignmentService.getAssignmentById(id);
    }

    @GetMapping("/getAll")
    public Page<CourseAssignmentResponse> getAllAssignments(@RequestHeader("Authorization") String token,
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


    @PostMapping("/remove/{id}")
    public CourseAssignmentResponse removeAssignment(@RequestHeader("Authorization") String token,
            @PathVariable Long id) {

         return  assignmentService.removeAssignment(id);

    }
}