package com.edu.elearning.service.impl.userCourseMappings;


import com.edu.elearning.dto.userCourseMappings.request.EnrollmentCreate;
import com.edu.elearning.dto.userCourseMappings.response.EnrollmentResponse;
import com.edu.elearning.entity.CourseEnrollment;
import com.edu.elearning.entity.Courses;
import com.edu.elearning.entity.User;
import com.edu.elearning.enums.EnrollmentStatus;
import com.edu.elearning.enums.Role;
import com.edu.elearning.enums.Status;
import com.edu.elearning.exception.ElearningException;
import com.edu.elearning.repository.CourseEnrollmentRepository;
import com.edu.elearning.repository.CourseRepository;
import com.edu.elearning.repository.UserRepository;
import com.edu.elearning.service.impl.email.EmailService;
import com.edu.elearning.service.userCourseMappings.CourseEnrollmentService;
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
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {


    private final CourseEnrollmentRepository enrollmentRepository;

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    private final EmailService emailService;


    @Override
    public EnrollmentResponse enrollStudent(
            EnrollmentCreate enrollmentCreate) {


        User user = userRepository.findById(
                enrollmentCreate.getStudentId()
        ).orElseThrow(() ->
                new ElearningException("User not found")
        );


        if (user.getRole() != Role.STUDENT) {
            throw new ElearningException(
                    "Only students can enroll courses"
            );
        }


        Courses course = courseRepository.findById(
                enrollmentCreate.getCourseId()
        ).orElseThrow(() ->
                new ElearningException("Course not found")
        );


        CourseEnrollment enrollment = CourseEnrollment.builder()
                .user(user.getUserDetails())
                .course(course)
                .enrolledDate(LocalDateTime.now())
                .progressPercentage(0)
                .enrollmentStatus(EnrollmentStatus.ACTIVE)
                .status(Status.ACTIVE)
                .build();


        CourseEnrollment saved =
                enrollmentRepository.save(enrollment);

        String htmlContent = emailService.buildEnrollmentEmail(
                user.getUserDetails().getFullName(),
                course.getCourseName(),
                saved.getEnrolledDate()
        );
        emailService.sendHtmlEmail(
                user.getUserDetails().getEmail(),
                "You're Enrolled: " + course.getCourseName(),
                htmlContent
        );

        return convertToDTO(saved);
    }


    @Override
    public EnrollmentResponse getEnrollmentById(Long id) {


        CourseEnrollment enrollment =
                enrollmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ElearningException(
                                        "Enrollment not found"
                                )
                        );


        return convertToDTO(enrollment);
    }


    @Override
    public Page<EnrollmentResponse> getAllEnrollments(
            Map<String, String> filters,
            int page,
            int size,
            String sortField,
            String sortDirection) {


        Specification<CourseEnrollment> specification =
                CommonSpecifications.getSpecification(
                        filters,
                        CourseEnrollment.class
                );


        Page<CourseEnrollment> enrollments =
                enrollmentRepository.findAll(
                        specification,
                        Sorting.sorting(
                                page,
                                size,
                                sortField,
                                sortDirection
                        )
                );


        return enrollments.map(this::convertToDTO);
    }


    @Override
    public EnrollmentResponse removeEnrollment(Long id) {


        CourseEnrollment enrollment =
                enrollmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ElearningException(
                                        "Enrollment not found"
                                )
                        );

        enrollment.setStatus(Status.INACTIVE);
        enrollmentRepository.save(enrollment);
        return convertToDTO(enrollment);
    }


    private EnrollmentResponse convertToDTO(
            CourseEnrollment enrollment) {


        return EnrollmentResponse.builder()
                .id(enrollment.getId())

                .studentId(
                        enrollment.getUser().getId()
                )

                .studentName(
                        enrollment.getUser().getFullName()
                )

                .courseId(
                        enrollment.getCourse().getId()
                )

                .courseName(
                        enrollment.getCourse().getCourseName()
                )

                .enrolledDate(
                        enrollment.getEnrolledDate()
                )

                .progressPercentage(
                        enrollment.getProgressPercentage()
                )

                .status(
                        enrollment.getStatus().name()
                )

                .build();
    }
}