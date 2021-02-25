package org.sdrc.usermgmt.repository;

import org.sdrc.usermgmt.domain.AccountDesignationMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value="jpaAccountDesignationMappingRepository")
public interface AccountDesignationMappingRepository extends JpaRepository<AccountDesignationMapping, Integer>{

}
