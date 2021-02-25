package org.sdrc.usermgmt.repository;

import org.sdrc.usermgmt.domain.LoginAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author subham
 *
 */
@Repository(value="jpaLoginAuditRepository")
public interface LoginAuditRepository extends JpaRepository<LoginAudit, Integer>{

}
