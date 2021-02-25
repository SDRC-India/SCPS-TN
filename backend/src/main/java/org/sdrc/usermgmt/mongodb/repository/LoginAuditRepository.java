package org.sdrc.usermgmt.mongodb.repository;

import org.sdrc.usermgmt.mongodb.domain.LoginAudit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author subham
 *
 */

@Repository(value="mongoLoginAuditRepository")
public interface LoginAuditRepository extends MongoRepository<LoginAudit, String> {

}
