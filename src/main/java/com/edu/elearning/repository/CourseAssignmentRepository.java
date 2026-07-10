package com.edu.elearning.repository;

import com.edu.elearning.entity.CourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, Long>,
        JpaSpecificationExecutor<CourseAssignment> {
}
