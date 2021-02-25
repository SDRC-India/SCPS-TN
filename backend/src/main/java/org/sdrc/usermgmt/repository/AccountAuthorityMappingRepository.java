package org.sdrc.usermgmt.repository;

import org.sdrc.usermgmt.domain.AccountAuthorityMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author subham
 *
 */
@Repository(value="jpaAccountAuthorityMappingRepository")
public interface AccountAuthorityMappingRepository extends JpaRepository<AccountAuthorityMapping, Integer>{

}
