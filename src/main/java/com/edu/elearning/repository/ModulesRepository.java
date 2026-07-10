package com.edu.elearning.repository;

import com.edu.elearning.entity.Modules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ModulesRepository extends JpaRepository<Modules, Long>, JpaSpecificationExecutor<Modules> {
}
