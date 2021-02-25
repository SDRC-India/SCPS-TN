package in.co.sdrc.scpstn.repository;

import in.co.sdrc.scpstn.domain.AccountFacilityMapping;

import java.util.List;

import org.sdrc.usermgmt.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountFacilityMappingRepository extends JpaRepository<AccountFacilityMapping, Integer> {

	List<AccountFacilityMapping> findByFacilityFacilityIdAndAccountId(Integer facilityId,
			int userId);

	List<AccountFacilityMapping> findByAccount(Account acc);
	
	List<AccountFacilityMapping> findByFacilityFacilityIdIn(Integer facilityId);

	
}
