package org.sdrc.usermgmt.repository;

import java.util.List;

import org.sdrc.usermgmt.domain.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "jpaDesignationRepository")
public interface DesignationRepository extends JpaRepository<Designation, Integer> {

	Designation findByCode(String string);

	List<Designation> findByIdIn(List<Integer> designationIds);

	List<Designation> findAllByOrderByIdAsc();

	Designation findByName(String string);

}
