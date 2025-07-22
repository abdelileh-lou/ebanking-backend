package org.sid.ebanking.repositories;

import org.sid.ebanking.entities.BankAccount;
import org.sid.ebanking.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
}
