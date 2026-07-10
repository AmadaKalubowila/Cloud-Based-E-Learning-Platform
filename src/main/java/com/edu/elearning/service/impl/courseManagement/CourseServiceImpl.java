package com.edu.elearning.service.impl.courseManagement;

import com.edu.elearning.dto.courseManagement.request.CourseCreate;
import com.edu.elearning.dto.courseManagement.request.CourseUpdate;
import com.edu.elearning.dto.courseManagement.response.CourseResponse;
import com.edu.elearning.entity.Courses;
import com.edu.elearning.entity.Modules;
import com.edu.elearning.enums.CourseStatus;
import com.edu.elearning.enums.Departments;
import com.edu.elearning.enums.Status;
import com.edu.elearning.exception.ElearningException;
import com.edu.elearning.repository.CourseRepository;
import com.edu.elearning.repository.ModulesRepository;

import com.edu.elearning.service.courseManagement.CourseService;
import com.edu.elearning.specification.CommonSpecifications;
import com.edu.elearning.utility.Sorting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {


    private final CourseRepository courseRepository;

    private final ModulesRepository modulesRepository;



    @Override
    public CourseResponse createCourse(CourseCreate courseCreate) {


        List<Modules> modules =
                modulesRepository.findAllById(courseCreate.getModuleIds());


        Courses course = Courses.builder()
                .courseCode(courseCreate.getCourseCode())
                .courseName(courseCreate.getCourseName())
                .description(courseCreate.getDescription())
                .thumbnailUrl(courseCreate.getThumbnailUrl())
                .durationHours(courseCreate.getDurationHours())
                .departments(
                        Departments.valueOf(courseCreate.getDepartments())
                )
                .courseStatus(CourseStatus.DRAFT)
                .status(Status.ACTIVE)
                .modules(modules)
                .build();


        Courses savedCourse = courseRepository.save(course);


        return convertToDTO(savedCourse);
    }



    @Override
    public CourseResponse updateCourse(CourseUpdate courseUpdate) {


        Courses course = courseRepository.findById(courseUpdate.getId())
                .orElseThrow(() ->
                        new ElearningException("Course not found")
                );


        if(courseUpdate.getCourseCode()!=null){
            course.setCourseCode(courseUpdate.getCourseCode());
        }


        if(courseUpdate.getCourseName()!=null){
            course.setCourseName(courseUpdate.getCourseName());
        }


        if(courseUpdate.getDescription()!=null){
            course.setDescription(courseUpdate.getDescription());
        }


        if(courseUpdate.getThumbnailUrl()!=null){
            course.setThumbnailUrl(courseUpdate.getThumbnailUrl());
        }


        if(courseUpdate.getDurationHours()!=null){
            course.setDurationHours(courseUpdate.getDurationHours());
        }


        if(courseUpdate.getCourseStatus()!=null){
            course.setCourseStatus(
                    CourseStatus.valueOf(courseUpdate.getStatus())
            );
        }

        if(courseUpdate.getStatus()!=null){
            course.setStatus(
                    Status.valueOf(String.valueOf(course.getStatus()))
            );
        }


        if(courseUpdate.getDepartments()!=null){
            course.setDepartments(
                    Departments.valueOf(courseUpdate.getDepartments())
            );
        }


        if(courseUpdate.getModuleIds()!=null){

            List<Modules> modules =
                    modulesRepository.findAllById(courseUpdate.getModuleIds());

            course.setModules(modules);
        }


        Courses updatedCourse = courseRepository.save(course);


        return convertToDTO(updatedCourse);
    }



    @Override
    public CourseResponse getCourseById(Long id) {


        Courses course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new ElearningException("Course not found")
                );


        return convertToDTO(course);
    }



    @Override
    public void deleteCourse(Long id) {


        Courses course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new ElearningException("Course not found")
                );


        courseRepository.delete(course);
    }



    @Override
    public Page<CourseResponse> getAllCourses(
            Map<String, String> filters,
            int page,
            int size,
            String sortField,
            String sortDirection) {


        Specification<Courses> specification =
                CommonSpecifications.getSpecification(filters, Courses.class);


        Page<Courses> courses =
                courseRepository.findAll(
                        specification,
                        Sorting.sorting(
                                page,
                                size,
                                sortField,
                                sortDirection
                        )
                );


        return courses.map(this::convertToDTO);
    }




    private CourseResponse convertToDTO(Courses course){


        List<Long> moduleIds =
                course.getModules()
                        .stream()
                        .map(Modules::getId)
                        .toList();


        return CourseResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .durationHours(course.getDurationHours())
                .courseStatus(course.getCourseStatus().name())
                .status(course.getStatus().name())
                .departments(course.getDepartments().name())
                .moduleIds(moduleIds)
                .build();
    }
}