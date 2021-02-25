package org.sdrc.usermgmt.mongodb.repository;

import org.sdrc.usermgmt.mongodb.domain.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository(value="mongoAccountRepository")
public interface AccountRepository extends MongoRepository<Account, String> {

	Account findByUserName(String name);

	Account findByEmail(String string);

	Account findById(String userId);

}
