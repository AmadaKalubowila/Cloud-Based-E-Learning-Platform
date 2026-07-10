package com.edu.elearning.service.impl.userCourseMappings;

import com.edu.elearning.dto.userCourseMappings.request.CourseAssignmentCreate;
import com.edu.elearning.dto.userCourseMappings.response.CourseAssignmentResponse;
import com.edu.elearning.entity.CourseAssignment;
import com.edu.elearning.entity.Courses;
import com.edu.elearning.entity.User;
import com.edu.elearning.enums.Role;
import com.edu.elearning.enums.Status;
import com.edu.elearning.exception.ElearningException;
import com.edu.elearning.repository.CourseAssignmentRepository;
import com.edu.elearning.repository.CourseRepository;
import com.edu.elearning.repository.UserRepository;
import com.edu.elearning.service.userCourseMappings.CourseAssignmentService;
import com.edu.elearning.specification.CommonSpecifications;
import com.edu.elearning.utility.Sorting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CourseAssignmentServiceImpl
        implements CourseAssignmentService {


    private final CourseAssignmentRepository assignmentRepository;

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;


    @Override
    public CourseAssignmentResponse assignLecturer(
            CourseAssignmentCreate assignmentCreate) {


        User lecturer = userRepository.findById(
                assignmentCreate.getLecturerId()
        ).orElseThrow(() ->
                new ElearningException(
                        "Lecturer not found"
                )
        );


        if (lecturer.getRole() != Role.LECTURER) {

            throw new ElearningException(
                    "Only lecturers can be assigned courses"
            );
        }


        Courses course = courseRepository.findById(
                assignmentCreate.getCourseId()
        ).orElseThrow(() ->
                new ElearningException(
                        "Course not found"
                )
        );


        CourseAssignment assignment =
                CourseAssignment.builder()
                        .user(lecturer.getUserDetails())
                        .course(course)
                        .assignedDate(LocalDateTime.now())
                        .build();


        CourseAssignment saved =
                assignmentRepository.save(assignment);


        return convertToDTO(saved);
    }


    @Override
    public CourseAssignmentResponse getAssignmentById(Long id) {


        CourseAssignment assignment =
                assignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ElearningException(
                                        "Assignment not found"
                                )
                        );


        return convertToDTO(assignment);
    }


    @Override
    public Page<CourseAssignmentResponse> getAllAssignments(
            Map<String, String> filters,
            int page,
            int size,
            String sortField,
            String sortDirection) {


        Specification<CourseAssignment> specification =
                CommonSpecifications.getSpecification(
                        filters,
                        CourseAssignment.class
                );


        Page<CourseAssignment> assignments =
                assignmentRepository.findAll(
                        specification,
                        Sorting.sorting(
                                page,
                                size,
                                sortField,
                                sortDirection
                        )
                );


        return assignments.map(this::convertToDTO);
    }


    @Override
    public CourseAssignmentResponse removeAssignment(Long id) {


        CourseAssignment assignment =
                assignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ElearningException(
                                        "Assignment not found"
                                )
                        );

        assignment.setStatus(Status.INACTIVE);
        return convertToDTO(assignmentRepository.save(assignment));
    }


    private CourseAssignmentResponse convertToDTO(
            CourseAssignment assignment) {


        return CourseAssignmentResponse.builder()

                .id(assignment.getId())

                .lecturerId(
                        assignment.getUser().getId()
                )

                .lecturerName(
                        assignment.getUser().getFullName()
                )

                .courseId(
                        assignment.getCourse().getId()
                )

                .courseName(
                        assignment.getCourse().getCourseName()
                )

                .assignedDate(
                        assignment.getAssignedDate()
                )

                .build();
    }
}