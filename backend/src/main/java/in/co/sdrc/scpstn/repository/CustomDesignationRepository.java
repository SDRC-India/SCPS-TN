package in.co.sdrc.scpstn.repository;

import java.util.List;

import org.sdrc.usermgmt.domain.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomDesignationRepository extends JpaRepository<Designation, Integer> {
	List<Designation> findByCodeIn(List<String> desingationCodes);
}
