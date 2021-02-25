package in.co.sdrc.scpstn.repository;

import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.AccountDesignationMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component("customAccountDesignationMappingRepository")
public interface CustomAccountDesignationMappingRepository extends JpaRepository<AccountDesignationMapping, Integer> {

	AccountDesignationMapping findByAccount(Account account);
}
