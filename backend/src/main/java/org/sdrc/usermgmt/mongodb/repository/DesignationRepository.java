package org.sdrc.usermgmt.mongodb.repository;

import java.util.List;

import org.sdrc.usermgmt.mongodb.domain.Designation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "mongoDesignationRepository")
public interface DesignationRepository extends MongoRepository<Designation, String> {

	Designation findByCode(String string);

	List<Designation> findByIdIn(List<String> designationIds);

	List<Designation> findAllByOrderByIdAsc();

	List<Designation> findBySlugIdIn(List<Integer> designationIds);

	Designation findById(String designationIds);

}
