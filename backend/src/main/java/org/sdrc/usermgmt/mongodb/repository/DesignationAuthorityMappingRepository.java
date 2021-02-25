package org.sdrc.usermgmt.mongodb.repository;

import java.util.List;

import org.sdrc.usermgmt.mongodb.domain.Designation;
import org.sdrc.usermgmt.mongodb.domain.DesignationAuthorityMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository(value="mongoDesignationAuthorityMappingRepository")
public interface DesignationAuthorityMappingRepository extends MongoRepository<DesignationAuthorityMapping, String> {

	List<DesignationAuthorityMapping> findByDesignationIn(List<Designation> desgList);

	List<DesignationAuthorityMapping> findByDesignationIdIn(List<String> desgList);

}
