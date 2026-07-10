package com.edu.elearning.repository;

import com.edu.elearning.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Courses, Long>,
        JpaSpecificationExecutor<Courses> {

}