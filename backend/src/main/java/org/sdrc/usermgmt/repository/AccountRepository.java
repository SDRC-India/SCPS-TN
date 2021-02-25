package org.sdrc.usermgmt.repository;

import org.sdrc.usermgmt.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value="jpaAccountRepository")
public interface AccountRepository extends JpaRepository<Account, Integer> {

	Account findByUserName(String name);

	Account findByEmail(String string);

	Account findById(Integer userId);

}
