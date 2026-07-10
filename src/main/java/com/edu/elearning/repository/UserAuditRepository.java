package com.edu.elearning.repository;

import com.edu.elearning.entity.AuditUserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuditRepository extends JpaRepository<AuditUserLog, Long>, JpaSpecificationExecutor<AuditUserLog> {
}
