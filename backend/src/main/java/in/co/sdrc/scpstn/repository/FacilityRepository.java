package in.co.sdrc.scpstn.repository;

import java.util.List;

import org.sdrc.usermgmt.domain.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.RepositoryDefinition;

import in.co.sdrc.scpstn.domain.Area;
import in.co.sdrc.scpstn.domain.Facility;

@RepositoryDefinition(domainClass = Facility.class, idClass=Integer.class)
public interface FacilityRepository extends JpaRepository<Facility, Integer> {

	public List<Facility> findByDesignation(Designation designation);
	 
	public List<Facility> findByDesignationAndArea(Designation designation, Area area);
	
	public List<Facility> findByDesignationCodeIn(List<String> designations);
	
	public List<Facility> findAll();
	
	public Facility findByFacilityId(Integer facilityId);

	public List<Facility> findByFacilityTypeIsNotNull();

	public List<Facility> findByDesignationCodeInAndIsActiveTrue(List<String> asList);

	public List<Facility> findByIsActiveIsTrueAndFacilityTypeIsNotNull();
	
	//public List<Facility> findByDesignationDesignationIdAndAreaAreaId(Designation designation, Area area);
}
