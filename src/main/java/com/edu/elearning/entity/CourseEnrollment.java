package com.edu.elearning.entity;


import com.edu.elearning.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Entity
@Table(name = "course_enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CourseEnrollment extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private UserDetails user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Courses course;

    private LocalDateTime enrolledDate;

    private Integer progressPercentage;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus enrollmentStatus;

}