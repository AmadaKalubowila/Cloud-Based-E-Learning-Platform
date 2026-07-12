package com.edu.elearning.controller.courseManagement;

import com.edu.elearning.dto.courseManagement.request.CourseCreate;
import com.edu.elearning.dto.courseManagement.request.CourseUpdate;
import com.edu.elearning.dto.courseManagement.response.CourseResponse;
import com.edu.elearning.service.courseManagement.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {


    private final CourseService courseService;

    @PostMapping("/create")
    public CourseResponse createCourse(
            @RequestBody CourseCreate courseCreate) {

        return courseService.createCourse(courseCreate);
    }


    @PutMapping("/update")
    public CourseResponse updateCourse(
            @RequestBody CourseUpdate courseUpdate) {

        return courseService.updateCourse(courseUpdate);
    }


    @GetMapping("/getById/{id}")
    public CourseResponse getCourseById(
            @PathVariable Long id) {

        return courseService.getCourseById(id);
    }


    @DeleteMapping("/delete/{id}")
    public String deleteCourse(
            @PathVariable Long id) {

        courseService.deleteCourse(id);

        return "Course deleted successfully";
    }


    @GetMapping("/getAll")
    public Page<CourseResponse> getAllCourses(

            @RequestParam Map<String, String> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection) {


        return courseService.getAllCourses(
                filters,
                page,
                size,
                sortField,
                sortDirection
        );
    }
}