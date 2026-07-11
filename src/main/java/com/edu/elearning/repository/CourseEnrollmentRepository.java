package com.edu.elearning.repository;

import com.edu.elearning.entity.CourseEnrollment;
import com.edu.elearning.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseEnrollmentRepository
        extends JpaRepository<CourseEnrollment, Long>, JpaSpecificationExecutor<CourseEnrollment> {
    List<CourseEnrollment> findByCourseIn(List<Courses> courses);

}