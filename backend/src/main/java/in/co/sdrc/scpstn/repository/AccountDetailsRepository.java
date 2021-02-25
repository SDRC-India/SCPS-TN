package in.co.sdrc.scpstn.repository;

import java.util.List;

import org.sdrc.usermgmt.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.scpstn.domain.AccountDetails;

public interface AccountDetailsRepository extends JpaRepository<AccountDetails, Integer> {

	AccountDetails findByAccount(Account account);

	List<AccountDetails> findByAccountIdIn(List<Integer> accountList);
}
