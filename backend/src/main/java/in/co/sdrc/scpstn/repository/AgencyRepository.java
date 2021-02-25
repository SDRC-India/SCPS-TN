package in.co.sdrc.scpstn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.scpstn.domain.Agency;

public interface AgencyRepository extends JpaRepository<Agency,Integer>{
	
	
	public Agency findByAgencyName(String agencyName);
	
	public Agency findByEncryptedAgencyId(String encryptedAgencyId);
	
	public Agency findByAgencyId(Integer agencyId);
	
	public List<Agency> findAll();
	

}
