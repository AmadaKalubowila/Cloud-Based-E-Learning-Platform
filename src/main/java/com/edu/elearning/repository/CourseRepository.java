package com.edu.elearning.repository;

import com.edu.elearning.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Courses, Long>,
        JpaSpecificationExecutor<Courses> {


    @Query(value = "SELECT c.* FROM courses c " +
            "JOIN course_modules cm ON c.id = cm.course_id " +
            "WHERE cm.module_id = :moduleId", nativeQuery = true)
    List<Courses> findAllByModuleId(@Param("moduleId") Long moduleId);
}