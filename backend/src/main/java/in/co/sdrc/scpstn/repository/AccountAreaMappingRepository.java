package in.co.sdrc.scpstn.repository;

import in.co.sdrc.scpstn.domain.AccountAreaMapping;

import java.util.List;

import org.sdrc.usermgmt.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountAreaMappingRepository extends JpaRepository<AccountAreaMapping, Integer> {
	AccountAreaMapping findByAccount(Account acc);

		List<AccountAreaMapping> findByAccountAccountDesignationMappingDesignationIdAndAreaAreaId( Integer roleId, Integer areaId);
}
