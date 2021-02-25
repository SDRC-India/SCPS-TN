package org.sdrc.usermgmt.repository;

import org.sdrc.usermgmt.domain.DesignationAuthorityMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value="jpaDesignationAuthorityMappingRepository")
public interface DesignationAuthorityMappingRepository extends JpaRepository<DesignationAuthorityMapping, Integer> {

}
