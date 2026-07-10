package com.edu.elearning.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Entity
@Table(name = "course_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CourseAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", nullable = false)
    private UserDetails user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Courses course;

    private LocalDateTime assignedDate;

}