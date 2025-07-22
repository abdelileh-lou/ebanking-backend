package org.sid.ebanking.repositories;

import org.sid.ebanking.entities.AccountOperation;
import org.sid.ebanking.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
}
