package com.edu.elearning.entity;

import com.edu.elearning.enums.CourseStatus;
import com.edu.elearning.enums.Departments;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "courses")
@SuperBuilder
public class Courses extends BaseEntity{

    @Column(nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String courseName;

    @Column(length = 1000)
    private String description;

    private String thumbnailUrl;

    private Integer durationHours;

    @Enumerated(EnumType.STRING)
    private CourseStatus courseStatus;

    @Enumerated(EnumType.STRING)
    private Departments departments;

    @ManyToMany
    @JoinTable(
            name = "course_modules",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "module_id")
    )
    private List<Modules> modules = new ArrayList<>();
}
