package com.edu.elearning.repository;

import com.edu.elearning.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseEnrollmentRepository
        extends JpaRepository<CourseEnrollment, Long>, JpaSpecificationExecutor<CourseEnrollment> {

}